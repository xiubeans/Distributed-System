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
	/*HashMap configuration = new HashMap<String, String>();
	HashMap send_rules = new HashMap<String, String>();
	HashMap recv_rules = new HashMap<String, String>();*/
	int max_vals = 8;
	String[][] conf = new String[max_vals][10]; //temporary holders
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
	
	
	/*public String[] matchRules(String type, Message message)
	{
		// Returns the first rule matched. 
		
		String[] rule = new String[10];
		System.out.println("In matchrules");
		//iterate through the message's fields and see which rule is matched for the right HashMap (send or recv)
		if(type.equals("send"))
		{
			String[] keys = new String[send_rules.size()];
			send_rules.keySet().toArray(keys);
			for(int i=0; i<keys.length; i++)
			{
				String[] vals = new String[send_rules.size()];
				vals = send_rules.get(keys[i]).toString().replaceAll("[\\[\\]]","").split(", ");
				//System.out.println("Outside: "+keys[i]);
				for(int j=0; j<vals.length; j++)
				{
					//if(message.getSrc().equalsIgnoreCase(vals[j])) //need to write getSrc and other accessors...
						//we matched something, so continue trying to match that rule first...
					//System.out.println("Inside: "+vals[j]);
					continue;
				}
			}
		}
		else if(type.equals("receive"))
		{
			String[] keys = new String[recv_rules.size()];
			recv_rules.keySet().toArray(keys);
			for(int i=0; i<keys.length; i++)
			{
				String[] vals = new String[recv_rules.size()];
				vals = recv_rules.get(keys[i]).toString().replaceAll("[\\[\\]]","").split(", ");
				//System.out.println("Outside: "+keys[i]);
				for(int j=0; j<vals.length; j++)
				{
					//if(message.getSrc().equalsIgnoreCase(vals[j])) //need to write getSrc and other accessors...
						//we matched something, so continue trying to match that rule first...
					//System.out.println("Inside: "+vals[j]);
					continue;
				}
			}
		}
		return rule; //return the actual string representation of it to use for handling
	}
	*/
	
	void send(Message message) {
		/*Should keep track of IDs and make sure to send next unused ID (should be
		 *monotonically increasing). Need to write the code for set_id in the 
		 *Message class. ALSO, this should check the message against the send
		 *rules to handle the message appropriately. */
		
		int id = 0; //placeholder for now
		
		//String[] matched = matchRules("send", message); //needs to be written correctly
		
		message.set_id(id);
		
		/* Upon sending of a message, check the size and then free that amount from the buffer. */
	}
	
	
	Message receive( ) {
		/*check if anything in the receive buffer to be processed according to the rules.
		 * If above check is passed (ie. a message should be delivered) we should get the 
		 * front-most msg in the recv queue. As of 01/15/13, we plan to make the receiver
		 * block.
		 */
		 
		/* Upon receiving a message, check the size and then remove that amount from the buffer's free space. */
		
		//String[] matched = matchRules("receive", Message message); //needs to be written correctly
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
		/*ArrayList names = new ArrayList<String>();
		ArrayList ip_addys = new ArrayList();
		ArrayList ports = new ArrayList();
		ArrayList sr_act = new ArrayList<String>();
		ArrayList sr_src = new ArrayList();
		ArrayList sr_dst = new ArrayList();
		ArrayList sr_kind = new ArrayList<String>();
		ArrayList sr_id = new ArrayList();
		ArrayList sr_nth = new ArrayList();
		ArrayList sr_every = new ArrayList();
		ArrayList rr_act = new ArrayList<String>();
		ArrayList rr_src = new ArrayList();
		ArrayList rr_dst = new ArrayList();
		ArrayList rr_kind = new ArrayList<String>();
		ArrayList rr_id = new ArrayList();
		ArrayList rr_nth = new ArrayList();
		ArrayList rr_every = new ArrayList();*/
		
		while(i.hasNext())
		{
			Map.Entry me = (Map.Entry)i.next();
			file_part = me.getKey().toString().toLowerCase();
			System.out.print("\nRaw parsed data --> "+file_part + ": ");
			System.out.println(me.getValue());
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
						/*if(choices[key].equals("action"))
							fillLoop(send_rules, choices, k, val);//sr_act.add(choices[val]);
						else if(choices[key].equals("src"))
							fillLoop(send_rules, choices, k, val);//sr_src.add(choices[val]);
						else if(choices[key].equals("dest"))
							fillLoop(send_rules, choices, k, val);//sr_dst.add(choices[val]);
						else if(choices[key].equals("kind"))
							fillLoop(send_rules, choices, k, val);//sr_kind.add(choices[val]);
						else if(choices[key].equals("id"))
							fillLoop(send_rules, choices, k, val);//sr_id.add(choices[val]);
						else if(choices[key].equals("nth"))
							fillLoop(send_rules, choices, k, val);//sr_nth.add(choices[val]);
						else if(choices[key].equals("everynth"))
							fillLoop(send_rules, choices, k, val);//sr_every.add(choices[val]);
						*/
						/*if(k == pairs.length-1) //make each of the lists the same length by adding wildcards to all empty fields
						{
							int l_len = sr_act.size();
							if(sr_src.size() < l_len)
								sr_src.add("*");
							if(sr_dst.size() < l_len)
								sr_dst.add("*");
							if(sr_kind.size() < l_len)
								sr_kind.add("*");
							if(sr_id.size() < l_len)
								sr_id.add("*");
							if(sr_nth.size() < l_len)
								sr_nth.add("*");
							if(sr_every.size() < l_len)
								sr_every.add("*");
						}*/
					}
				}
				else if(file_part.equals("receiverules"))
				{
					for(int k=0; k<pairs.length; k++)
					{
						choices = pairs[k].split("=");
						choices[key] = choices[key].trim().toLowerCase();
						fillLoop(recv_rules, choices, k, val, choices[key]);
						
						/*if(choices[key].equals("action"))
							fillLoop(recv_rules, choices, k, val);//rr_act.add(choices[val]);
						else if(choices[key].equals("src"))
							fillLoop(recv_rules, choices, k, val);//rr_src.add(choices[val]);
						else if(choices[key].equals("dest"))
							fillLoop(recv_rules, choices, k, val);//rr_dst.add(choices[val]);
						else if(choices[key].equals("kind"))
							fillLoop(recv_rules, choices, k, val);//rr_kind.add(choices[val]);
						else if(choices[key].equals("id"))
							fillLoop(recv_rules, choices, k, val);//rr_id.add(choices[val]);
						else if(choices[key].equals("nth"))
							fillLoop(recv_rules, choices, k, val);//rr_nth.add(choices[val]);
						else if(choices[key].equals("everynth"))
							fillLoop(recv_rules, choices, k, val);//rr_every.add(choices[val]);
						*/
						/*if(k == pairs.length-1)  //make each of the lists the same length by adding wildcards to all empty fields
						{
							int l_len = rr_act.size();
							if(rr_src.size() < l_len)
								rr_src.add("*");
							if(rr_dst.size() < l_len)
								rr_dst.add("*");
							if(rr_kind.size() < l_len)
								rr_kind.add("*");
							if(rr_id.size() < l_len)
								rr_id.add("*");
							if(rr_nth.size() < l_len)
								rr_nth.add("*");
							if(rr_every.size() < l_len)
								rr_every.add("*");
						}*/
					}
				}
				else if(file_part.equals("configuration")) //handle config third because it happens only once
				{
					System.out.println("inner: "+elements[j]);
					System.out.println(pairs.length+" pairs are: "+pairs[0]+" "+pairs[1]+" "+pairs[2]);
					for(int k=0; k<pairs.length; k++)
					{
						choices = pairs[k].split("=");
						choices[key] = choices[key].trim();
						fillLoop(conf, choices, k, val, choices[key]);
						
						/*System.out.println("Pairs: "+pairs[0].toLowerCase()+" "+pairs[1]);
						System.out.println("Choices key: "+choices[key]);
						System.out.println("Choices val: "+choices[val]);
						if(choices[key].toLowerCase().equals("name"))
						{
							fillLoop(conf, choices, k, val);
							//System.out.println("Adding "+choices[val]+" to position "+k+" in array index for "+choices[key]);
							//names.add(choices[val]);
						}
						else if(choices[key].toLowerCase().equals("ip"))
						{
							fillLoop(conf, choices, k, val);
							//ip_addys.add(choices[val]);
							//System.out.println("Adding "+choices[val]+" to position "+k+" in array index for "+choices[key]);
						}
						else if(choices[key].toLowerCase().equals("port"))
						{
							fillLoop(conf, choices, k, val);
							//ports.add(choices[val]);
						}*/
					}
				}
				else
				{
					System.out.println("Error!");
					break;
				}
			}
		}	
		/*
		send_rules.put("action", sr_act);
		send_rules.put("src", sr_src);
		send_rules.put("dest", sr_dst);
		send_rules.put("kind", sr_kind);
		send_rules.put("id", sr_id);
		send_rules.put("nth", sr_nth);
		send_rules.put("everynth", sr_every);
		
		recv_rules.put("action", rr_act);
		recv_rules.put("src", rr_src);
		recv_rules.put("dest", rr_dst);
		recv_rules.put("kind", rr_kind);
		recv_rules.put("id", rr_id);
		recv_rules.put("nth", rr_nth);
		recv_rules.put("everynth", rr_every);
		configuration.put("name", names);
		configuration.put("ip", ip_addys);
		configuration.put("port", ports);*/
		
		//Just temporary print statements for reference --REMOVE BEFORE SUBMISSION
		//System.out.println("Config Dict contains "+ configuration.keySet()+" and "+ configuration.values());
//		System.out.println("New dict:");
//		for(int g=0; g<8; g++)
//		{
//			for(int l=0; l<10; l++)
//			{
//				System.out.println(conf[g][l]);
//			}
//		}
		//System.out.println("Send Dict contains "+ send_rules.keySet()+" and "+ send_rules.values());
		//System.out.println("Recv Dict contains "+ recv_rules.keySet()+" and "+ recv_rules.values());
		
		try {
			yamlInput.close();
		} catch (IOException e) {
			System.out.println("Could not close configuration file\n");
		}
		
		//TODO: Check to see if localName was found in the NAMES section of the config file; if not, return -1 ?
	}
	
	
	void fillLoop(String[][] arr, String[] choices, int k, int val, String field)
	{
		arr[k][0] = field;
		for(int f=1; f<arr[k].length; f++)
		{
			//System.out.println("tmp["+k+"]["+f+"] is "+tmp[k][f]+".");
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
			//System.out.println("currently looking at "+send_rules[i][0]);
			for(j=0; j<send_rules[i].length; j++)
			{
				if(send_rules[i][j].equals("*"))
					break;
			}
			if(send_rules[i][0].equalsIgnoreCase(field))
			{
				tmp = Arrays.copyOfRange(send_rules[i], 0, j);
				return tmp; //send_rules[i];
			}
		}
		
		for(i=0; i<recv_rules.length; i++)
		{
			//System.out.println("currently looking at "+recv_rules[i][0]);
			for(j=0; j<recv_rules[i].length; j++)
			{
				if(recv_rules[i][j].equals("*"))
					break;
			}
			if(recv_rules[i][0].equalsIgnoreCase(field))
			{
				tmp = Arrays.copyOfRange(recv_rules[i], 0, j);
				return tmp; //send_rules[i];
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
				return tmp; //send_rules[i];
			}
		}
		System.out.println("Could not match "+field+" to any field.");
		return tmp;
		/*
		if(send_rules.keySet().contains(field))
			tmp = send_rules.get(field).toString();
		else if(recv_rules.keySet().contains(field))
			tmp = recv_rules.get(field).toString();
		else if(configuration.keySet().contains(field))
			tmp = configuration.get(field).toString();
		else
			System.out.println("Could not match "+field+" to any field.");
		tmp = tmp.replaceAll("[\\[\\]]", "");
		String[] values = tmp.split(", ");
		return values;*/
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
				/*case 1: //action taken on message
					all_fields[ctr][0] = "drop";
					all_fields[ctr][1] = "delay";
					all_fields[ctr][2] = "duplicate";
					break;
					
				case 2: //combined with case 3 because they are very similar
					names = mp.getField("name");
					*/
				case 1:
					String[] names = mp.getField("name");
					int i;
					for(i=1; i<names.length; i++)
					{
						if(!names[i].equals("*"))
						{	
							System.out.println("Name: "+names[i]);
							all_fields[ctr][i] = names[i];
						} 
					}
					all_fields[ctr][i] = "*";
					break;
				case 2: //what kind of message
					String[] kind = mp.getField("kind");
					for(i=1; i<kind.length; i++)
						if(!kind[i].equals("*"))
						{
							all_fields[ctr][i] = kind[i];
							all_fields[ctr][i] = "*"; //since it's possible to have a message with only an action specified
						}
					break;
					/*
				case 5: //the ID mentioned in the config file
					String[] ids = mp.getField("id");
					for(int i=0; i<ids.length; i++)
						all_fields[ctr][i] = ids[i];
					all_fields[ctr][ids.length] = "*";
					break;
				case 6: //Nth specifications
					String[] nth = mp.getField("nth");
					for(int i=0; i<nth.length; i++)
						all_fields[ctr][i] = nth[i];
					all_fields[ctr][nth.length] = "*";
					break;
				case 7: //EveryNth specifications
					String[] every = mp.getField("everynth");
					for(int i=0; i<every.length; i++)
						all_fields[ctr][i] = every[i];
					all_fields[ctr][every.length] = "*";
					break;
					*/
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
		System.out.println("Name length: "+names.length);
		System.out.println("kind length: "+kind.length);
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
				
		if(user_options[dest].equalsIgnoreCase(local_name)) //same src and dest
		{
			System.out.println("Error - same src and dest "+local_name+". No loopback functionality offered.");
			return false;
		}
		
		all_fields = populateOptions(mp, user_input, max_fields, max_options);	
		
		System.out.println("All fields:");
		for(ctr=0; ctr<max_fields; ctr++)
		{
			for(int a=0; a<max_options; a++)
				System.out.println(all_fields[ctr][a]);
		}
		
		for (ctr = 0; ctr<max_fields; ctr++) //verify user entered valid options 
		{
			for(int j=0; j<max_options; j++)
			{
				System.out.println("Currently checking "+all_fields[ctr][j]+" against "+user_options[ctr].toString());
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
					{	
						continue;
					}
				}
				break;
				/*if(!(all_fields[ctr][j].equals("")) && j != max_options-1)
				{
					if((all_fields[ctr][j].equalsIgnoreCase((user_options[ctr].toString()))))
						break; //found a match for that field
				}
				else //went through all options available for given field
					return false;*/
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
