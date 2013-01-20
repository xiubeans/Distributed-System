import java.io.*;
import java.security.AccessControlException;
import java.security.AccessController;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import org.yaml.snakeyaml.*;

/* The next three comments may all need to be handled in the MessagePasser class instead of here. */
/*Check if a file has been modified since last time by storing an MD5 hash
of the file and checking before each send and receive method...*/

/* Setup sockets for each of the nodes identified in the config file here. */

/* Whenever we call receive, we should get the frontmost msg in the recv queue. */


/* MessagePasser is responsible for keeping track of all IDs assigned and for
 * ensuring monotonicity and uniqueness. The source:ID pair must be unique, but
 * IDs can be reused across different nodes. */

public class MessagePasser {
	int max_vals = 8;
	String[][] conf = new String[max_vals][10]; //temporary holders -- FIX THESE!
	String[][] send_rules = new String[max_vals][10]; //temporary holders
	String[][] recv_rules = new String[max_vals][10]; //temporary holders
	
	// IMPORTANT !!!
	// new fields from Jasper: 
	private static MessagePasser uniqInstance = null;
	ReentrantLock globalLock = new ReentrantLock();	// may be used to synchronize 
	String config_file;
	String local_name;
	AtomicInteger message_id = new AtomicInteger(1);	// atomic message id counter
	Hashtable<String, ConnState> connections = new Hashtable<String, ConnState>();	// maintain all connection state information
	MessageBuffer send_buf;
	MessageBuffer rcv_buf;
	MessageBuffer rcv_delayed_buf;
	
	/*
	 * Constructor: private
	 * IMPORTANT: make it singleton
	 */
	private MessagePasser() {
		// Stupid part
		conf[0] = new String[10];
		conf[0][0] = "Jasper";
		conf[0][1] = "127.0.0.1";
		conf[0][2] = "8001";
		conf[1] = new String[10];
		conf[1][0] = "David";
		conf[1][1] = "127.0.0.1";
		conf[1][2] = "8002";
		conf[2] = new String[10];
		conf[2][0] = "Bill";
		conf[2][1] = "127.0.0.1";
		conf[2][2] = "8003";
		
		// IMPORTANT !!!
		// Smart part
		this.send_buf = new MessageBuffer(1000);
		this.rcv_buf = new MessageBuffer(1000);
		this.rcv_delayed_buf = new MessageBuffer(1000);
		
		// Init the local server which waits for incoming connection
		Runnable runnableServer = new ServerThread();
		Thread threadServer = new Thread(runnableServer);
		threadServer.start();
		
	}
	
	/*
	 * The way how other can get the singleton instance of this class
	 */
	public static synchronized MessagePasser getInstance() {
		if (uniqInstance == null)
			new MessagePasser();
			
		return uniqInstance;
	}
	
	/*
	 * Remember to call it after we firstly getInstance() in our application
	 */
	public void setConfigAndName(String configuration_filename, String local_name) {
		this.config_file = configuration_filename;
		this.local_name = local_name;		
	}
	
	public void buildRule(HashMap rule, int ctr, String type)
	{
		/* Builds the rule in an easy to use format for user
		 * examination. */
		
		int header = 0;
		
		if(type.equals("send"))
		{
			for(int i=0; i<send_rules.length; i++)
			{
				if(send_rules[i][header].equals("*"))
					continue;
				rule.put(send_rules[i][header], send_rules[i][ctr]);
			}
		}
		else if(type.equals("receive"))
		{
			for(int i=0; i<recv_rules.length; i++)
			{
				if(recv_rules[i][header].equals("*"))
					continue;
				rule.put(recv_rules[i][header], recv_rules[i][ctr]);
			}
		}
	}
	
	
	public HashMap matchRules(String type, Message message)
	{
		/*Returns the first rule matched so appropriate actions
		 *can be taken. */
		
		int header = 0;
		String field_name = "";
		HashMap rule = new HashMap();
		
		if(type.equals("send"))
		{
			for(int i=0; i<send_rules.length; i++)
			{
				for(int j=1; j<send_rules[i].length; j++)
				{
					buildRule(rule, j, type); //builds the current rule
					//System.out.println(rule.keySet()+" "+rule.values());
					//System.out.println(send_rules[i][header]);
					field_name = send_rules[i][header].toLowerCase();
					if(field_name.equals("src"))
					{
						//if we have the right field, let's check if the rule value (or *) matches the user input src
						if(!rule.get(field_name).equals(message.getVal(field_name, message)) || !rule.get(field_name).equals("*"))
							continue; //it didn't match, so go on to the next rule
					}
					if(field_name.equals("dest")) //continual IFs because we want to make sure all fields listed in rule match
					{
						if(!rule.get(field_name).equals(message.getVal(field_name, message)) || !rule.get(field_name).equals("*"))
							continue;
					}	
					if(field_name.equals("kind"))
					{
						if(!rule.get(field_name).equals(message.getVal(field_name, message)) || !rule.get(field_name).equals("*"))
							continue;
					}
					if(field_name.equals("id"))
					{
						if(!rule.get(field_name).equals(message.getVal(field_name, message)) || !rule.get(field_name).equals("*"))
							continue;
					}
					if(field_name.equals("nth"))
					{
						if(!rule.get(field_name).equals(message.getVal(field_name, message)) || !rule.get(field_name).equals("*"))
							continue;
					}
					if(field_name.equals("everynth"))
					{
						if(!rule.get(field_name).equals(message.getVal(field_name, message)) || !rule.get(field_name).equals("*"))
							continue;
					}
					else
					{
						System.out.println("Don't forget to add a check for "+field_name+"!");
						continue;	
					}
				}
			}
		}
		else if(type.equals("receive"))
		{
			for(int i=0; i<recv_rules.length; i++)
			{
				for(int j=1; j<recv_rules[i].length; j++)
				{
					buildRule(rule, j, type); //builds the current rule
					//System.out.println(rule.keySet()+" "+rule.values());
					//System.out.println(send_rules[i][header]);
					field_name = recv_rules[i][header].toLowerCase();
					if(field_name.equals("src"))
					{
						//if we have the right field, let's check if the rule value (or *) matches the user input src
						if(!rule.get(field_name).equals(message.getVal(field_name, message)) || !rule.get(field_name).equals("*"))
							continue; //it didn't match, so go on to the next rule
					}
					if(field_name.equals("dest")) //continual IFs because we want to make sure all fields listed in rule match
					{
						if(!rule.get(field_name).equals(message.getVal(field_name, message)) || !rule.get(field_name).equals("*"))
							continue;
					}	
					if(field_name.equals("kind"))
					{
						if(!rule.get(field_name).equals(message.getVal(field_name, message)) || !rule.get(field_name).equals("*"))
							continue;
					}
					if(field_name.equals("id"))
					{
						if(!rule.get(field_name).equals(message.getVal(field_name, message)) || !rule.get(field_name).equals("*"))
							continue;
					}
					if(field_name.equals("nth"))
					{
						if(!rule.get(field_name).equals(message.getVal(field_name, message)) || !rule.get(field_name).equals("*"))
							continue;
					}
					if(field_name.equals("everynth"))
					{
						if(!rule.get(field_name).equals(message.getVal(field_name, message)) || !rule.get(field_name).equals("*"))
							continue;
					}
					else
					{
						System.out.println("Don't forget to add a check for "+field_name+"!");
						continue;	
					}
				}
			}
		}
		return rule; //return the representation of the rule matched to use for handling
	}
	
	
	/*
	 * Send
	 */
	public void send(Message message) {
		
		// get the output stream
		ObjectOutputStream oos = this.connections.get(message.dest).getObjectOutputStream();
		// check against the send rules, and follow the first rule matched
		HashMap rule = this.matchRules("send", message);

		// IMPORTANT !!!!!!
		// Only for testing !!!!
		
		// if one rule matched
		// put code here !
		try{
			
			// get the connection state information
			ConnState conn = this.connections.get(message.dest);

			if(rule != null) {
				// 3 actions: duplicate, drop, and delay
				String action = rule.get("action").toString();
				
				// action: drop -- simply return
				if(action.equals("drop"))
					return;
				// action: duplicate -- send two identical messages, but with different message id
				else if(action.equals("duplicate")) {

					// step 1: send two identical messages
					message.set_id(this.message_id.getAndIncrement());
					oos.writeObject(message);
					conn.getAndIncrementOutMessageCounter();
					message.set_id(this.message_id.getAndIncrement());
					oos.writeObject(message);
					conn.getAndIncrementOutMessageCounter();
					
					// step 2: flush send buffer
					ArrayList<Message> delayed_messages = this.send_buf.nonblockingTakeAll();
					while(!delayed_messages.isEmpty()) {
						Message dl_message = delayed_messages.remove(0);
						dl_message.set_id(this.message_id.getAndIncrement());
						oos.writeObject(dl_message);
						conn.getAndIncrementOutMessageCounter();
					}
				}
				// action: delay -- put the message in the send_buf
				else {
					this.send_buf.nonblockingOffer(message);
				}
			}
			// no rule matched
			else {
				try {
					
					// step 1: write this object to the socket
					message.set_id(this.message_id.getAndIncrement());
					oos.writeObject(message);
					conn.getAndIncrementOutMessageCounter();
					
					// step 2: flush all delayed messages
					ArrayList<Message> delayed_messages = this.send_buf.nonblockingTakeAll();
					while(!delayed_messages.isEmpty()) {
						Message dl_message = delayed_messages.remove(0);
						dl_message.set_id(this.message_id.getAndIncrement());
						oos.writeObject(dl_message);
						conn.getAndIncrementOutMessageCounter();
					}
					
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Receive
	 * Get single message once called; 
	 * Receiving any non-delayed message will append all delayed message in the rcv_delay_buf to the tail of rcv_buf
	 * Blocking mode
	 * If it takes a message with "drop", it will return null
	 */
	public Message receive() {
		Message message = this.rcv_buf.blockingTake();
		// check against receive rules
		HashMap rule = this.matchRules("receive", message);
		
		try {
			
			// get the connection state information
			ConnState conn = this.connections.get(message.src);

			// single rule matched
			if(rule != null) {
				// 3 actions: duplicate, drop, and delay
				// Get the action at first !!!!
				String action = rule.get("action").toString();
				// 1: drop -- drop the message and return null
				if(action.equals("drop")) {
					conn.getAndIncrementInMessageCounter();
					return null;
				}
				// 2: duplicate 
				else if(action.equals("duplicate")) {
					// step 1: duplicate another message, and append it to the tail of the rcv_buf
					this.rcv_buf.nonblockingOffer(message);
					
					// step 2: move all delayed messages in the rcv_delay_buf to the rcv_buf
					ArrayList<Message> delayed_messages = this.rcv_delayed_buf.nonblockingTakeAll();
					while(!delayed_messages.isEmpty()) {
						Message dl_message = delayed_messages.remove(0);
						this.rcv_buf.nonblockingOffer(dl_message);
					}
					
					// step 3: return message
					conn.getAndIncrementInMessageCounter();
					return message;
				}
				// 3: delay -- put it to the rcv_delay_buf and return null
				else {
					this.rcv_delayed_buf.nonblockingOffer(message);
					return null;
				}
			}
			// no rule matched
			else {
				// step 1: move all delayed messages in the rcv_delay_buf to the rcv_buf
				ArrayList<Message> delayed_messages = this.rcv_delayed_buf.nonblockingTakeAll();
				while(!delayed_messages.isEmpty()) {
					Message dl_message = delayed_messages.remove(0);
					this.rcv_buf.nonblockingOffer(dl_message);
				}
				
				// step 2: return message
				return message;
			}
						
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return message;
	}

	
	
	void initSockets() {
		/*Create (from the ArrayList class in Java) sockets for the nodes.
		 *Using TCP sockets, so keep the connection alive for more than just the 
		 *send/receive of a message.
		 */
		
		
	}
	
	
	void parseConfig(String fname) {
		/*Parses the configuration file and stores all of the sections into
		 *their own hash maps. Any field not present is stored as "*" to 
		 *denote a wildcard functionality. 
		 */
		
		InputStream yamlInput = null;
		Yaml yaml = new Yaml();
		String config = "";
		FilePermission perm = null;
		
		/*perm = new FilePermission(fname, "read"); //Currently not working correctly...
		try {
			AccessController.checkPermission(perm);	
		} catch (AccessControlException e) {
			System.out.println("File "+fname+" is not readable.\n");
			System.exit(-1); //probably want to just return -1
		}*/
		
		try {
			yamlInput = new FileInputStream(new File(fname));			
		} catch (FileNotFoundException e) { 
			//error out if not found
			System.out.println("File "+fname+" does not exist.\n");
			System.exit(-1); 
		}
		
		Object data = yaml.load(yamlInput);
		LinkedHashMap<String,String> file_map = (LinkedHashMap<String,String>) data;
		
		String whole = "";
		String[] elements = new String[10]; //figure out appropriate size for these
		String[] pairs = new String[10];
		String[] choices = new String[10];
		String file_part = "";	
		
		for(int ctr=0; ctr<max_vals; ctr++) //quickly initialize all elements
		{
			for(int j=0; j<10; j++)
			{
				conf[ctr][j] = "*";
				send_rules[ctr][j] = "*";
				recv_rules[ctr][j] = "*";
			}
		}
		
		Set set = file_map.entrySet();
		Iterator i = set.iterator();
		int j = 0;
		
		while(i.hasNext())
		{
			Map.Entry me = (Map.Entry)i.next();
			file_part = me.getKey().toString().toLowerCase();
			//System.out.print("\nRaw parsed data --> "+file_part + ": ");
			//System.out.println(me.getValue());
			ArrayList<String> inner = new ArrayList<String>();
			inner.add(me.getValue().toString());
			whole = inner.toString();
			whole = whole.replaceAll("[\\[\\]\\{]", "");
			elements = whole.split("\\},?");
			
			for(j=0; j<elements.length; j++)
			{
				int key = 0;
				int val = 1;
				
				pairs = elements[j].split(", ");
				
				if(file_part.equals("sendrules"))
				{					
					for(int k=0; k<pairs.length; k++)
					{
						choices = pairs[k].split("=");
						choices[key] = choices[key].trim().toLowerCase();
						fillLoop(send_rules, choices, k, val, choices[key]);
					}
				}
				else if(file_part.equals("receiverules"))
				{
					for(int k=0; k<pairs.length; k++)
					{
						choices = pairs[k].split("=");
						choices[key] = choices[key].trim().toLowerCase();
						fillLoop(recv_rules, choices, k, val, choices[key]);
					}
				}
				else if(file_part.equals("configuration")) //handle config third because it happens only once
				{
					//System.out.println("inner: "+elements[j]);
					//System.out.println(pairs.length+" pairs are: "+pairs[0]+" "+pairs[1]+" "+pairs[2]);
					for(int k=0; k<pairs.length; k++)
					{
						choices = pairs[k].split("=");
						choices[key] = choices[key].trim();
						fillLoop(conf, choices, k, val, choices[key]);
					}
				}
				else
				{
					System.out.println("Error!");
					break;
				}
			}
		}	
				
		try {
			yamlInput.close();
		} catch (IOException e) {
			System.out.println("Could not close configuration file\n");
		}
		
		//TODO: Check to see if localName was found in the NAMES section of the config file; if not, return -1 ?
	}
	
	
	void fillLoop(String[][] arr, String[] choices, int k, int val, String field)
	{
		/* Populates all of the configuration file options into a 2D array. */
		
		if(arr[k][0].equals("*"))
			arr[k][0] = field;
		else //because the config fields in the YAML file can be in any order, must align to correct array index
		{
			while(!arr[k][0].equals(field))
			{
				k++;
				if(k == arr.length)
					return;
			}
		}
				
		for(int f=1; f<arr[k].length; f++)
		{
			if(arr[k][f].equals("*"))
			{
				arr[k][f] = choices[val];
				break;
			}
		}
	}
	
			
	String[] getField(String field){
		/* Accessor to return any field desired by the user
		 * program. 
		 * */
		
		String[] tmp = null;
		int i, j;
		
		for(i=0; i<send_rules.length; i++)
		{
			for(j=0; j<send_rules[i].length; j++)
			{
				if(send_rules[i][j].equals("*"))
					break;
			}
			if(send_rules[i][0].equalsIgnoreCase(field))
			{
				tmp = Arrays.copyOfRange(send_rules[i], 0, j);
				return tmp;
			}
		}
		
		for(i=0; i<recv_rules.length; i++)
		{
			for(j=0; j<recv_rules[i].length; j++)
			{
				if(recv_rules[i][j].equals("*"))
					break;
			}
			if(recv_rules[i][0].equalsIgnoreCase(field))
			{
				tmp = Arrays.copyOfRange(recv_rules[i], 0, j);
				return tmp;
			}
		}
		
		for(i=0; i<conf.length; i++)
		{
			//System.out.println("currently looking at "+conf[i][0]);
			for(j=0; j<conf[i].length; j++)
			{
				if(conf[i][j].equals("*"))
					break;
			}
			if(conf[i][0].equalsIgnoreCase(field))
			{
				tmp = Arrays.copyOfRange(conf[i], 0, j);
				return tmp;
			}
		}
		System.out.println("Could not match "+field+" to any field.");
		return tmp;
	}
	
	
	public String[][] populateOptions(MessagePasser mp, String user_input, int max_fields, int max_options)
	{
		/*Stores all of the possible options for each field of a message from
		 *the lab information (such as for the message action) and from the 
		 *configuration file (in the case of names, for example) into a 
		 *two-dimensional array.
		 */

		String[][] all_fields = new String[max_fields][max_options]; 
		int ctr;
		
		for(ctr=0; ctr<max_fields; ctr++) //quickly initialize all elements
		{
			for(int j=0; j<max_options; j++)
				all_fields[ctr][j] = "";
		}
		
		System.out.println("User input: "+user_input);
		for (ctr = 0; ctr<max_fields; ctr++) 
		{
			switch(ctr)
			{
				case 0: //type of message
					all_fields[ctr][0] = "send";
					all_fields[ctr][1] = "receive";
					break;
				case 1:
					String[] names = mp.getField("name");
					int i;
					for(i=1; i<names.length; i++)
					{
						if(!names[i].equals("*"))
							all_fields[ctr][i] = names[i];
					}
					all_fields[ctr][i] = "*";
					break;
				case 2: //what kind of message
					String[] kind = mp.getField("kind");
					for(i=1; i<kind.length; i++)
					{
						if(!kind[i].equals("*"))
							all_fields[ctr][i] = kind[i];
					}
					all_fields[ctr][i] = "*"; //since it's possible to have a message with only an action specified
					break;
				default:
					break;
			}
		}
		return all_fields;
	}
	
	
	public boolean validateUserRequests(String user_input, MessagePasser mp, String local_name)
	{
		/* Determines whether the user has followed the usage
		 * guidelines. A send or receive rule can have up to
		 * nine elements:
		 * <receive | send> <action> <src> <dest> <kind> <ID> <Nth> <EveryNth> <data>
		 * 
		 * Return: false (no) or true (yes)
		 *  */
		
		String[] kind = mp.getField("kind");
		String[] names = mp.getField("name");
		int ctr = 0;
		int max_fields = 3;
		int max_options =  (names.length >= kind.length) ? names.length+1 : kind.length+1; 
		int dest = 1;
		String[] user_options = new String[max_options];
		String[][] all_fields = new String[max_fields][max_options]; /*2D array; outside is all possible fields,
																	   inner is all possible options*/
		user_options = user_input.trim().split("\\s");
		if(user_options.length == 1)
		{
			if(user_input.trim().equalsIgnoreCase("receive"))
				return true;
			else
				return false;
		}
		else if(user_options.length != max_fields)
		{	
			System.out.println("Usage error - must be receive OR send <dest> <kind>");
			return false;
		}	
		
		if(user_options[dest].equalsIgnoreCase(local_name)) //same src and dest
		{
			System.out.println("Error - same src and dest "+local_name+". No loopback functionality offered.");
			return false;
		}
		
		all_fields = populateOptions(mp, user_input, max_fields, max_options);	
		
		/*System.out.println("All fields:");
		for(ctr=0; ctr<max_fields; ctr++)
		{
			for(int a=0; a<max_options; a++)
				System.out.println(all_fields[ctr][a]);
		}*/
		
		for (ctr = 0; ctr<max_fields; ctr++) //verify user entered valid options 
		{
			for(int j=0; j<max_options; j++)
			{
				//System.out.println("Currently checking "+all_fields[ctr][j]+" against "+user_options[ctr].toString());
				if((all_fields[ctr][j].equalsIgnoreCase((user_options[ctr].toString()))))
					break; //found a match for that field
				else
				{
					if(j == max_options-1)
					{
						if(all_fields[ctr][j].equals("")) //need to figure this stuff out nowwwww!
							return false;
					}
					else
						continue;
				}
				return false; //item not in list
			}
		}
		return true;
	}

	
	public int validOption(String user_input)
	{
		int user_action = -1;
		
		if(user_input.length() > 1)
		{
			System.out.println("Unrecognized option "+user_input+". Choices are 1, 2, and 3.");
			return -1;
		}
		
		try {
			user_action = Integer.parseInt(user_input);
		} catch(NumberFormatException e) {
			System.out.println(user_input+" is not an integer.");
			return -1;
		}
		return user_action;
	}	
	
	
	public boolean isNewestConfig(int local_modification_time, int global_modification_time, SFTPConnection svr_conn)
	{
		// get the YAML file at first
		if(!svr_conn.isConnected())
			svr_conn.connect(CONSTANTS.HOST, CONSTANTS.USER);
		clearCounters(); //GET RID OF THIS specific instance of the function...just so we don't forget to do this (such as for below)
    	global_modification_time = svr_conn.getLastModificationTime(CONSTANTS.CONFIGFILE); // record the time-stamp of YAML file
    	if(global_modification_time != local_modification_time)
    	{
    		svr_conn.downloadFile(CONSTANTS.CONFIGFILE, CONSTANTS.LOCALPATH); // download the YAML file
    		clearCounters(); //get rid of the Nth and EveryNth counters upon new config file, as per lab specs.
    		return true;
    	}
    	return false;
	}
	
	
	public void clearCounters()
	{
		System.out.println("MUST WRITE THE FUNCTION TO CLEAR NTH AND EVERYNTH COUNTERS!!!");
	}
}

/*
 * Describe the connection state with another remote end
 * The MessagePasser should keep a set of ConnStates
 */
class ConnState {
	
	String remote_name;		// the remote end
	Socket local_socket;	// the local socket used to connect to remote end
	AtomicInteger in_messsage_counter = new AtomicInteger(0);	// the number of incoming messages to the remote end
	AtomicInteger out_message_counter = new AtomicInteger(0);	// the number of outgoing messages to the remote end
	ObjectOutputStream oos;
	ObjectInputStream ois;

	/*
	 * Constructor
	 */
	public ConnState(String remote_name, Socket local_socket) {
		this.remote_name = remote_name;
		this.local_socket = local_socket;
	}
	
	/*
	 * Atomic methods
	 */
	public int getInMessageCounter() {
		return this.in_messsage_counter.get();
	}
	
	public int getAndIncrementInMessageCounter() {
		return this.in_messsage_counter.getAndIncrement();
	}
	
	public void resetInMessageCounter() {
		this.in_messsage_counter.set(0);
	}
	
	public int getOutMessageCounter() {
		return this.out_message_counter.get();
	}
	
	public int getAndIncrementOutMessageCounter() {
		return this.out_message_counter.getAndIncrement();
	}
	
	public void resetOutMessageCounter() {
		this.out_message_counter.set(0);
	}
	
	public void setObjectOutputStream(ObjectOutputStream oos) {
		this.oos = oos; 
	}
	
	public ObjectOutputStream getObjectOutputStream() {
		return oos;
	}

	public void setObjectInputStream(ObjectInputStream ois) {
		this.ois = ois; 
	}
	
	public ObjectInputStream getObjectInputStream() {
		return ois;
	}
}


/*
 * This class will be created as a new thread, to wait for incoming connections
 */
class ServerThread implements Runnable {
	MessagePasser mmp;
	
	public ServerThread() {
		mmp = MessagePasser.getInstance();
	}
	
	public void run() {
		
		// Get the configuration of local server
		int i;
		for(i = 0; i < this.mmp.max_vals; i++) 
			if(this.mmp.conf[i][0].equals(mmp.local_name))
				break;
		
		// if no such name, terminate the appication
		if(i == this.mmp.max_vals) {
			System.out.println("No such name");
			System.exit(0);
		} 
		// local name found, setup the local server
		else 
			try {
				// Init the local listening socket
				ServerSocket socket = new ServerSocket(Integer.parseInt(this.mmp.conf[i][2]));
				while(true) {
					Socket s = socket.accept();
					
					// find the remote end's name
					InetAddress iaddr = s.getInetAddress();
					String ip = iaddr.getHostAddress();
					String port = "" + s.getPort();
					String remote_name = "";
					for(i = 0; i < this.mmp.max_vals; i++) 
						if(this.mmp.conf[i][1].equals(ip) && this.mmp.conf[i][2].equals(port)) {
							remote_name = this.mmp.conf[i][0];
							break;
						}
					
					// if remote client not found, ignore and continue
					if(remote_name.equals("")) {
						System.out.println("Denied a connection from a anonymous client.");
						continue;
					}
					
					// Put the new socket into mmp's ConnState
					ConnState conn_state = new ConnState(remote_name, s);
					conn_state.setObjectOutputStream(new ObjectOutputStream(s.getOutputStream()));
					conn_state.setObjectInputStream(new ObjectInputStream(s.getInputStream()));
					this.mmp.connections.put(remote_name, new ConnState(remote_name, s));
					
					// create a new thread to get input stream from this connection
					Runnable receiveRunnable = new ReceiveThread(remote_name);
					Thread receiveThread = new Thread(receiveRunnable);
					receiveThread.start();
									
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
	}
}

/*
 * Child thread created by the Server
 * Only keep track of the input stream of a given socket, and deal with the buffer issue
 * It also maintain some connection state information, like the counter of input message number
 */
class ReceiveThread implements Runnable {
	String remote_name;
	MessagePasser mmp;
	
	/*
	 * Constructor
	 */
	public ReceiveThread(String remote_name) {

		this.remote_name = remote_name;
		this.mmp = MessagePasser.getInstance();
		
	}
	
	/*
	 * This method do the real work when this thread is created
	 * It listens to the input stream of the socket
	 * The connection will never close until the application terminates
	 */
	public void run() {
		
		// get the connection state of this socket at first
		ConnState conn_state = this.mmp.connections.get(this.remote_name);
		ObjectInputStream ois = conn_state.getObjectInputStream();
		
		// Infinite loop: listen for input
		while(true) {
			try {
				// read one message from the socket at once 
				Message message = (Message)ois.readObject(); 
				
				// put it into the MessagePasser's rcv_buf
				// drop the message if the buffer is full
				if(!this.mmp.rcv_buf.nonblockingOffer(message)) 
					continue;
				
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
