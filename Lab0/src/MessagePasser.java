import java.io.*;
import java.security.AccessControlException;
import java.security.AccessController;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.*;
import org.yaml.snakeyaml.Yaml;

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
	
	public MessagePasser(String configuration_filename, String local_name) {
		/* Create send and recv buffers */		
		String[] send_buf =  null; //where we'll store the send buffered messages per connection.
		String[] recv_buf = null;
		
		/* I think the creation of sockets should be a separate call either made by the user program or
		 * by us in a different location than the constructor. Feel free to argue otherwise. 
		 * */
		
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
	
	
	void send(Message message) {
		/*Should keep track of IDs and make sure to send next unused ID (should be
		 *monotonically increasing). Need to write the code for set_id in the 
		 *Message class. ALSO, this should check the message against the send
		 *rules to handle the message appropriately. */
		
		int id = 0; //placeholder for now
		message.set_id(id);
		HashMap matched = matchRules("send", message); //not sure what is returned if it doesn't match...
		//pay attention to the ACTION of the returned rule and stuff here
		
		/* Upon sending of a message, check the size and then free that amount from the buffer. */
	}
	
	
	Message receive( ) {
		/*check if anything in the receive buffer to be processed according to the rules.
		 * If above check is passed (ie. a message should be delivered) we should get the 
		 * front-most msg in the recv queue. As of 01/15/13, we plan to make the receiver
		 * block.
		 */
		 
		/* Upon receiving a message, check the size and then remove that amount from the buffer's free space. */
		
		//String[] matched = matchRules("receive", message); //needs to be written correctly
		//pay attention to the ACTION of the returned rule and stuff here
		
		return null;
	}  // may block
	
	
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
