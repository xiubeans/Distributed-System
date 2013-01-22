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

/* MessagePasser is responsible for keeping track of all IDs assigned and for
 * ensuring monotonicity and uniqueness. The source:ID pair must be unique, but
 * IDs can be reused across different nodes. */

public class MessagePasser {
	int max_vals = 7; //max number of fields in config file for a rule
	int max_config_items = 100; //this is an arbitrary number to take place of hard-coded values
	String[][] conf = new String[max_vals][max_config_items];
	String[][] send_rules = new String[max_vals][max_config_items]; 
	String[][] recv_rules = new String[max_vals][max_config_items]; 
	String[] conf_headers = {"name", "ip", "port"};
	String[] send_recv_headers = {"action", "src", "dest", "kind", "id", "nth", "everynth"};
	
	// IMPORTANT !!!
	// new fields from Jasper: 
	private static MessagePasser uniqInstance = null;
	static ReentrantLock globalLock = new ReentrantLock(); // may be used to synchronize 
	String config_file;
	String local_name;
	AtomicInteger message_id = new AtomicInteger(0); // atomic message id counter
	Hashtable<String, ConnState> connections = new Hashtable<String, ConnState>(); // maintain all connection state information
	MessageBuffer send_buf;
	MessageBuffer rcv_buf;
	MessageBuffer rcv_delayed_buf;
	AtomicInteger rcv_delay_buf_ready = new AtomicInteger(0);
	
	/*
	 * Constructor: private
	 * IMPORTANT: make it singleton
	 */
	private MessagePasser() {		
		// IMPORTANT !!!
		// Smart part
		this.send_buf = new MessageBuffer(1000);
		this.rcv_buf = new MessageBuffer(1000);
		this.rcv_delayed_buf = new MessageBuffer(1000);
	}
	
	/*
	 * The way how other can get the singleton instance of this class
	 */
	public static synchronized MessagePasser getInstance() {
		if (uniqInstance == null) {
			uniqInstance = new MessagePasser();
		}
		
//		try {
//			Thread.sleep(1000);
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
		return uniqInstance;
	}
	
	/*
	 * Remember to call it after we firstly getInstance() in our application
	 */
	public void setConfigAndName(String configuration_filename, String local_name) {
		this.config_file = configuration_filename;
		this.local_name = local_name;		
	}
	
	public void runServer() {
		// Init the local server which waits for incoming connection
		Runnable runnableServer = new ServerThread();
		Thread threadServer = new Thread(runnableServer);
		threadServer.start();
	}
	
	public void buildRule(HashMap rule, int ctr, String type)
	{
		/* Builds the rule in an easy to use format for user
		 * examination.
		 * Return: void */
		
		int header = 0;
		
		if(type.equals("send"))
		{
			for(int i=0; i<send_rules.length; i++)
				rule.put(send_rules[i][header], send_rules[i][ctr]);
		}
		else if(type.equals("receive"))
		{
			for(int i=0; i<recv_rules.length; i++)
				rule.put(recv_rules[i][header], recv_rules[i][ctr]);
		}
	}
	
	
	public HashMap matchRules(String type, Message message)
	{
		/*Returns the first rule matched so appropriate actions
		 *can be taken. 
		 *Return: HashMap 
		 */
		
		int header = 0;
		//String field_name = "";
		HashMap rule = new HashMap();
		
		String[][] tmp = null;
		
		if(type.equals("send"))
		{
			tmp = new String[send_rules.length][];
			for (int i = 0; i < send_rules.length; i++) 
				tmp[i] = Arrays.copyOf(send_rules[i], send_rules[i].length);
	    }
		else if(type.equals("receive"))
		{
			tmp = new String[recv_rules.length][];
			for (int i = 0; i < recv_rules.length; i++) 
				tmp[i] = Arrays.copyOf(recv_rules[i], recv_rules[i].length);
	    }
		
		for(int i=0; i<tmp.length; i++)
		{
			rule = new HashMap();
			buildRule(rule, i, type); //builds the current rule
			/* Grab each rule that we build and check its fields against those in the message */
			
			for(int j=0; j<send_recv_headers.length; j++)
			{
				//iterate through each of the fields and check if the rule value and the message vals match.
				int ctr;
				String field_name = send_recv_headers[j]; //the current field we're trying to check
				String vals = rule.values().toString().replaceAll("[\\[,\\] ]", "");
				
				if(vals.matches("^\\**$")) //and all fields were wildcards, including action (thus not a real rule)
				{					
					rule = null;
					System.out.println("Rule matching FAILED on "+field_name);
					break; //it didn't match
				}
				else if(field_name.equals("action")) //we don't act on this here
						continue;
				else if(field_name.equals("id")) //we have to access this specially
				{
					ctr = this.message_id.get();
					System.out.println("Trying to match "+field_name+" "+rule.get(field_name)+" with "+ctr);
					if(!rule.get(field_name).equals(ctr+"") && !rule.get(field_name).equals("*"))
					{
						rule = null;
						System.out.println("Rule matching FAILED on "+field_name);
						break; //it didn't match
					}
					else
						System.out.println("We matched "+field_name);
				}
				else if(field_name.equals("nth")) //we have to access this specially
				{
					if(type.equals("send"))
						ctr = this.connections.get(message.getVal("dest", message)).getOutMessageCounter();
					else //receive 
						ctr = this.connections.get(message.getVal("dest", message)).getInMessageCounter();
					System.out.println("Trying to match "+field_name+" "+rule.get(field_name)+" with "+ctr);
					if(!rule.get(field_name).equals("*") && !rule.get(field_name).equals(ctr+""))
					{
						rule = null;
						System.out.println("Rule matching FAILED on "+field_name);
						break; //it didn't match
					}
					else
						System.out.println("We matched "+field_name);
				}
				else if(field_name.equals("everynth")) //we have to access this specially
				{
					if(type.equals("send"))
						ctr = this.connections.get(message.getVal("dest", message)).getOutMessageCounter();
					else//receive
						ctr = this.connections.get(message.getVal("dest", message)).getInMessageCounter();
					System.out.println("Trying to match "+field_name+" "+rule.get(field_name)+" with "+ctr);
					if(!rule.get(field_name).equals("*") && !rule.get(field_name).equals(ctr % Integer.parseInt((String) rule.get(field_name))))
					{
						rule = null;
						System.out.println("Rule matching FAILED on "+field_name);
						break; //it didn't match
					}
					else
						System.out.println("We matched "+field_name);
				}
				else
				{
					System.out.println("Trying to match "+field_name+" "+rule.get(field_name)+" with "+message.getVal(field_name, message));
					if(!rule.get(field_name).toString().equalsIgnoreCase(message.getVal(field_name, message)) && !rule.get(field_name).equals("*"))
					{
						rule = null;
						System.out.println("Rule matching FAILED on "+field_name);
						break; //it didn't match
					}
					else
						System.out.println("We matched "+field_name);
				}
			}
		}
		return rule; //at this point, we can safely return based on the above declarations of rule
	}

	
	/*
	 * Send
	 */
	public void send(Message message) {
		
		// return if message is null
		if(message == null) 
			return;
		
		// get the output stream
		ObjectOutputStream oos = null;
		
		try{
			
			// get the connection state information
			ConnState conn = this.connections.get(message.dest);
			
			// if connection has not been established yet, set it up
			if(conn == null) {
							
				// get the meta information of the remote host
				String remote_addr = "";
				int port = 0;
				String remote_name = message.dest;

				for(int i = 1; i < 10; i++) {
					if(this.conf[0][i].equals(remote_name)) {
						remote_addr = conf[1][i];
						port = Integer.parseInt(conf[2][i]);
						break;
					}
				}
				
				
				// remote host not found 
				if(remote_addr.equals(""))
					return;
				
				// prepare remote ip_addr and port number
				String[] addr = remote_addr.split("\\.");	
				byte[] iaddr = {(byte)Integer.parseInt(addr[0]), (byte)Integer.parseInt(addr[1]), 
						(byte)Integer.parseInt(addr[2]), (byte)Integer.parseInt(addr[3])};
				InetAddress ia = InetAddress.getByAddress(iaddr);
				
				
				//HashMap rule = this.matchRules("send", message); //TEMPORARILY MOVING UP FOR TESTING; delete after done
				
				System.out.println("Sending to"+ia.getHostAddress()+":"+port);
				// create the socket, and connect to the other side
				Socket s = new Socket(ia, port);	
				System.out.println("Connected");
				// after connected init the connection state information				
				conn = new ConnState(message.dest, s);
				//conn.setObjectInputStream(new ObjectInputStream(s.getInputStream()));
				conn.setObjectOutputStream(new ObjectOutputStream(s.getOutputStream()));
				conn.setObjectInputStream(new ObjectInputStream(s.getInputStream()));
				this.connections.put(remote_name, conn);
				
				// send a LOGIN message immediately to notify the other side who am I
				oos = this.connections.get(remote_name).getObjectOutputStream();
				
				oos.writeObject(new Message(this.local_name, "", "LOGIN", null));
				oos.flush();
				//conn.setObjectInputStream(new ObjectInputStream(s.getInputStream()));
			}
			// else the connection is set up
			else
				oos = this.connections.get(message.dest).getObjectOutputStream();

			// get the first rule matched against this outgoing message
			HashMap rule = this.matchRules("send", message);
			
			// check against the send rules, and follow the first rule matched
			if(rule != null) {
				
				// 3 actions: duplicate, drop, and delay
				String action = rule.get("action").toString();
				
				// action: drop -- simply return
				if(action.equals("drop")) {
					
					message.set_id(this.message_id.getAndIncrement());
					
					System.out.println("**************************************************************************");
					System.out.println("send: src=" + this.local_name + ", dest=" + message.dest);
					System.out.println("rule: drop");
					System.out.println("**************************************************************************");
					
					return;
					
				}
				
				// action: duplicate -- send two identical messages, but with different message id
				else if(action.equals("duplicate")) {

					// step 1: send two identical messages, with same message_id
					message.set_id(this.message_id.getAndIncrement());
					oos.writeObject(message);
					oos.flush();
					conn.getAndIncrementOutMessageCounter();
					
					System.out.println("**************************************************************************");
					System.out.println("send: src=" + this.local_name + ", dest=" + message.dest);
					System.out.println("rule: duplicate");
					System.out.println("**************************************************************************");
					
					oos.writeObject(message);
					oos.flush();
					conn.getAndIncrementOutMessageCounter();

					System.out.println("**************************************************************************");
					System.out.println("send: src=" + this.local_name + ", dest=" + message.dest + ", id=" + message.id);
					System.out.println("rule: duplicated message");
					System.out.println("**************************************************************************");
					
					
					// step 2: flush send buffer
					ArrayList<Message> delayed_messages = this.send_buf.nonblockingTakeAll();
					while(!delayed_messages.isEmpty()) {
						
						Message dl_message = delayed_messages.remove(0);
						
						dl_message.set_id(this.message_id.getAndIncrement());
						oos.writeObject(dl_message);
						oos.flush();
						conn.getAndIncrementOutMessageCounter();

						System.out.println("******************************************************************");
						System.out.println("send: src=" + this.local_name + ", dest=" + dl_message.dest + ", id=" + dl_message.id);
						System.out.println("rule: delayed message released");
						System.out.println("******************************************************************");
					}
				}
				
				// action: delay -- put the message in the send_buf
				else {
					
					this.send_buf.nonblockingOffer(message);

					System.out.println("******************************************************************");
					System.out.println("send: src=" + this.local_name + ", dest=" + message.dest);
					System.out.println("rule: delay");
					System.out.println("******************************************************************");
				}
			}
			
			// no rule matched
			else {
				
				try {
					
					// step 1: write this object to the socket
					message.set_id(this.message_id.getAndIncrement());
					oos.writeObject(message);
					oos.flush();
					conn.getAndIncrementOutMessageCounter();
					
					System.out.println("**************************************************************************");
					System.out.println("send: src=" + this.local_name + ", dest=" + message.dest + ", id=" + message.id);
					System.out.println("rule: n/a");
					System.out.println("**************************************************************************");
										
					// step 2: send all delayed messages
					// TEST
					System.out.println("There are " + this.send_buf.size() + " message in the send_buf");					
					ArrayList<Message> delayed_messages = this.send_buf.nonblockingTakeAll();
					while(!delayed_messages.isEmpty()) {
						
						// send single delayed message at once
						Message dl_message = delayed_messages.remove(0);
						dl_message.set_id(this.message_id.getAndIncrement());
						oos.writeObject(dl_message);
						oos.flush();
						conn.getAndIncrementOutMessageCounter();
						
						System.out.println("**************************************************************************");
						System.out.println("send: src=" + this.local_name + ", dest=" + dl_message.dest + ", id=" + dl_message.id);
						System.out.println("rule: delayed message released");
						System.out.println("**************************************************************************");
						
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
		System.out.println("Got to receive!");
		// retrieve from the rcv_delay_buf if it is ready
		if(!this.rcv_delay_buf_ready.equals(0)) {
			System.out.println("The buffer is ready to take from");
			this.rcv_delay_buf_ready.decrementAndGet();
			return this.rcv_buf.blockingTake();
			
		}
		System.out.println("at blocking");
		// get blocked here until one message comes
		Message message = this.rcv_buf.blockingTake();
		System.out.println("Got to message matching");
		// check against receive rules
		HashMap rule = this.matchRules("receive", message);
		
		try {
			
			// get the connection state information
			ConnState conn = this.connections.get(message.src);

			// single rule matched
			if(rule != null) {
				
				// 3 actions: duplicate, drop, and delay
				String action = rule.get("action").toString();
				
				// 1: drop -- drop the message and return null
				if(action.equals("drop")) {
					
					System.out.println("******************************************************************");
					System.out.println("receive: src=" + message.src + ", dest=" + local_name + ", id=" + message.id);
					System.out.println("rule: drop");
					System.out.println("******************************************************************");
					
					// it will not increment the incoming message counter
					return null;
				}
				
				// 2: duplicate 
				else if(action.equals("duplicate")) {
					
					// step 1: duplicate another message, and append it to the tail of the rcv_delayed_buf
					this.rcv_delayed_buf.nonblockingOffer(message);
					
					System.out.println("******************************************************************");
					System.out.println("receive: src=" + message.src + ", dest=" + local_name + ", id=" + message.id);
					System.out.println("rule: duplicate");
					System.out.println("******************************************************************");

					// step 3: return message
					System.out.println("******************************************************************");
					System.out.println("receive: src=" + message.src + ", dest=" + this.local_name + ", id=" + message.id);
					System.out.println("rule: n/a");
					System.out.println("******************************************************************");
					
					// set the delay buffer as ready
					this.rcv_delay_buf_ready.set(this.rcv_delayed_buf.size());
					
					conn.getAndIncrementInMessageCounter();					
					return message;
				}
				// 3: delay -- put it to the rcv_delay_buf and return null
				else {
					
					this.rcv_delayed_buf.nonblockingOffer(message);
					
					System.out.println("******************************************************************");
					System.out.println("receive: src=" + message.src + ", dest=" + this.local_name + ", id=" + message.id);
					System.out.println("rule: delay");
					System.out.println("******************************************************************");
					
					return null;
				}
			}
			
			// no rule matched
			else {
			
				// step 2: return message
				System.out.println("******************************************************************");
				System.out.println("receive: src=" + message.src + ", dest=" + this.local_name + ", id=" + message.id);
				System.out.println("rule: n/a");
				System.out.println("******************************************************************");
				
				// set delay buffer as ready
				this.rcv_delay_buf_ready.set(this.rcv_delayed_buf.size());
				
				conn.getAndIncrementInMessageCounter();
				return message;
			}
						
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return message;
	}
	
	
	void initHeaders()
	{
		/* Statically defines the headers for each of the arrays */
		
		for(int i=0; i<conf_headers.length; i++)
			conf[i][0] = conf_headers[i];
		
		for(int j=0; j<send_recv_headers.length; j++)
		{
			send_rules[j][0] = send_recv_headers[j];
			recv_rules[j][0] = send_recv_headers[j];
		}
	}
	
	
	void parseConfig(String fname) {
		/*Parses the configuration file and stores all of the sections into
		 *their own 2D arrays. Any field not present is stored as "*" to 
		 *denote a wildcard functionality. 
		 */
		
		InputStream yamlInput = null;
		Yaml yaml = new Yaml();
		String config = "";
		
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
		String file_part = "";	
		
		for(int ctr=0; ctr<max_vals; ctr++) //quickly initialize all elements
		{
			for(int j=1; j<10; j++) //because the headers are already initialized by initHeaders
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
			//System.out.println("\nRaw parsed data --> "+file_part+": ");
			//System.out.println(me.getValue());
			ArrayList<String> inner = new ArrayList<String>();
			inner.add(me.getValue().toString());
			whole = inner.toString();
			whole = whole.replaceAll("[\\[\\]\\{]", "");
			elements = whole.split("\\},?");
			
			for(j=0; j<elements.length; j++) //the number of rules in this section of the config (here 3)
			{				
				pairs = elements[j].split(", "); //the number of elements in a particular rule (here 5, 2, and 4)
				
				if(file_part.equals("sendrules"))
					fillLoop(send_rules, pairs, j+1); //pass in 2D array, all of rule's elements in key-val form, and rule number
				else if(file_part.equals("receiverules"))
					fillLoop(recv_rules, pairs, j+1);
				else if(file_part.equals("configuration")) //handle config third because it happens only once
					fillLoop(conf, pairs, j+1);
				else
				{
					System.out.println("Error parsing configuration file");
					break;
				}
			}
		}	
				
		try {
			yamlInput.close();
		} catch (IOException e) {
			System.out.println("Could not close configuration file\n");
		}
	}
	
	//pass in 2D array, all of rule's elements in key-val form, and rule number
	void fillLoop(String[][] arr, String[] pairs, int rule_num)//, int val, String field)
	{
		/* Populates all of the configuration file options into a 2D array. */
		//System.out.println("have pairs of "+pairs.length+" like"+pairs[0].toString());
		int key=0;
		int val=1;
		
		for(int i=0; i<pairs.length; i++)
		{
			String[] choices = pairs[i].split("=");
			choices[key] = choices[key].trim().toString().toLowerCase();	//grab heading
			//System.out.println("have "+choices[key]);
			for(int j=0; j<arr.length; j++) //loop through 2D array's headers and put the values where they belong
			{
				//System.out.println("arr["+j+"]["+key+"] is "+arr[j][key]);
				if(arr[j][key].equals(choices[key])) //we found the correct heading
				{
					//System.out.println("Stored "+choices[val]+" in arr"+j+" "+rule_num);
					arr[j][rule_num] = choices[val]; //only set the ones with real values; all the rest stay as a wildcard
					break; //go to next pair instead of continuing through loop needlessly
				}
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
		
		for (ctr = 0; ctr<max_fields; ctr++) 
		{
			switch(ctr)
			{
				case 0: //type of message
					all_fields[ctr][1] = "send";
					all_fields[ctr][2] = "receive";
					break;
				case 1:
					String[] names = mp.getField("name");
					int i;
					for(i=0; i<names.length; i++)
					{
						if(!names[i].equals("*"))
							all_fields[ctr][i] = names[i];
					}
					all_fields[ctr][i] = "*";
					break;
				case 2: //what kind of message
					String[] kind = mp.getField("kind");
					for(i=0; i<kind.length; i++)
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
		 * guidelines. A send rule can have three elements:
		 * send <dest> <kind>
		 * A receive rule can have one element:
		 * receive
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
		
		for (ctr = 0; ctr<max_fields; ctr++) //verify user entered valid options 
		{
			for(int j=1; j<max_options; j++)
			{
				if((all_fields[ctr][j].equalsIgnoreCase((user_options[ctr].toString()))))
					break; //found a match for that field
				else if(all_fields[ctr][0].equalsIgnoreCase("kind") && all_fields[ctr][j].equals("*")) //because it can be anything really
				{
					break;
				}
				else
				{
					if(j == max_options-1)
					{
						if(all_fields[ctr][j].equals(""))
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
    	global_modification_time = svr_conn.getLastModificationTime(CONSTANTS.CONFIGFILE); // record the time-stamp of YAML file
    
    	if(global_modification_time != local_modification_time)
    	{
    		svr_conn.downloadFile(CONSTANTS.CONFIGFILE, CONSTANTS.LOCALPATH); // download the YAML file
    		clearCounters(); //get rid of the Nth and EveryNth counters upon new config file, as per lab specs.
    		return false;
    	}
    	return true;
	}
	
	
	public void clearCounters()
	{
		Set<String> keys = this.connections.keySet();
		Iterator<String> itr = keys.iterator();
		
		// reset all the in/out message counters 
		if(itr.hasNext()) {
			String key = itr.next();
			ConnState conn = this.connections.get(key);
			conn.in_messsage_counter.set(0);
			conn.out_message_counter.set(0);
		}
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
		for(i = 0; i < 10; i++) {
			if(this.mmp.conf[0][i].equals(mmp.local_name))
				break;
		}
		
		// if no such local name, terminate the application
		if(i == this.mmp.max_vals) {
			System.out.println("No such name: " + mmp.local_name);
			System.exit(0);
		} 
		
		// local name found, setup the local server
		else 
			try {
				System.out.println("We're TRYING TO listen on port "+this.mmp.conf[2][i]);
				// Init the local listening socket
				ServerSocket socket = new ServerSocket(Integer.parseInt(this.mmp.conf[2][i]));
				System.out.println("We do listen here");
				// keep listening on the WELL-KNOWN port
				while(true) {
					Socket s = socket.accept();
					System.out.println("We're listening on port "+this.mmp.conf[2][i]);
					ObjectOutputStream oos_tmp = new ObjectOutputStream(s.getOutputStream()); //my code
					ObjectInputStream ois_tmp = new ObjectInputStream(s.getInputStream());
					
					Message login_msg = (Message)ois_tmp.readObject();
					String remote_name = login_msg.src;
					
//					// find the remote end's name
//					InetAddress iaddr = s.getInetAddress();
//					String ip = iaddr.getHostAddress();
//					String port = "" + s.getPort();
//					String remote_name = "";
//					for(i = 0; i < 10; i++) 
//						if(this.mmp.conf[1][i].equals(ip)) { // && this.mmp.conf[2][i].equals(port)) {
//							remote_name = this.mmp.conf[0][i];
//							break;
//						}
//					
//					// if remote client not found, ignore and continue
//					if(remote_name.equals("")) {
//						System.out.println("Denied a connection from an anonymous client.");
//						continue;
//					}
					System.out.println("Got a connection from "+remote_name);
					// Put the new socket into mmp's ConnState
					ConnState conn_state = new ConnState(remote_name, s);
					System.out.println("Still good");
					conn_state.setObjectOutputStream(oos_tmp);//new ObjectOutputStream(s.getOutputStream()));
					conn_state.setObjectInputStream(ois_tmp);
					this.mmp.connections.put(remote_name, conn_state);
					// conn_state.setObjectInputStream(new ObjectInputStream(s.getInputStream()));
					System.out.println("connections: "+this.mmp.connections.keys().toString());
					// create a new thread to get input stream from this connection
					Runnable receiveRunnable = new ReceiveThread(remote_name);
					System.out.println("And now we explode after this line"); //maybe close ois_tmp?
					//conn_state.setObjectInputStream(new ObjectInputStream(s.getInputStream()));

					// TEST
					//Thread.sleep(5000);
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
				// TEST
				if(ois == null) {
					System.out.println("Oh no NULL !");
					Thread.sleep(2000);
				}
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
