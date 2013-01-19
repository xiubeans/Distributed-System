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
	HashMap configuration = new HashMap<String, String>();
	HashMap send_rules = new HashMap<String, String>();
	HashMap recv_rules = new HashMap<String, String>();	
	
	public MessagePasser(String configuration_filename, String local_name) {
		/* Create send and recv buffers */		
		String[] send_buf =  null; //where we'll store the send buffered messages per connection.
		String[] recv_buf = null;
		
		/* I think the creation of sockets should be a separate call either made by the user program or
		 * by us in a different location than the constructor. Feel free to argue otherwise. 
		 * */
		
	}
	
	void send(Message message) {
		/*Should keep track of IDs and make sure to send next unused ID (should be
		 *monotonically increasing). Need to write the code for set_id in the 
		 *Message class. ALSO, this should check the message against the send
		 *rules to handle the message appropriately. */
		
		int id = 0; //placeholder for now
		
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
			System.exit(-1); //probably want to just return -1
		}
		
		Object data = yaml.load(yamlInput);
		LinkedHashMap<String,String> file_map = (LinkedHashMap<String,String>) data;
		
		String whole = "";
		String[] elements = new String[10]; //figure out appropriate size for these
		String[] pairs = new String[10];
		String[] choices = new String[10];
		String file_part = "";
		
				
		Set set = file_map.entrySet();
		Iterator i = set.iterator();
		int j = 0;
		ArrayList names = new ArrayList<String>();
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
		ArrayList rr_every = new ArrayList();
		
		
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
			//System.out.println("Whole is: "+whole);
			elements = whole.split("\\},?");
			//System.out.println("element is: "+elements[0]);
			
			for(j=0; j<elements.length; j++) //fix this stuff, make it scalable???, etc...
			{
				pairs = elements[j].split(", ");
				
				if(file_part.equals("sendrules"))
				{
					//System.out.println("inner: "+elements[j]);
					//System.out.println(pairs.length+" pairs are: "+pairs[0]+" "+pairs[1]+" "+pairs[2]);
					for(int k=0; k<pairs.length; k++)
					{
						choices = pairs[k].split("=");
						choices[0] = choices[0].trim().toLowerCase();
						
						//System.out.println("Pairs: "+pairs[0].toLowerCase()+" "+pairs[1]);
						if(choices[0].equals("action"))
							sr_act.add(choices[1]);
						else if(choices[0].equals("src"))
							sr_src.add(choices[1]);
						else if(choices[0].equals("dst"))
							sr_dst.add(choices[1]);
						else if(choices[0].equals("kind"))
							sr_kind.add(choices[1]);
						else if(choices[0].equals("id"))
							sr_id.add(choices[1]);
						else if(choices[0].equals("nth"))
							sr_nth.add(choices[1]);
						else if(choices[0].equals("everynth"))
							sr_every.add(choices[1]);
						
						if(k == pairs.length-1) //make each of the lists the same length by adding wildcards to all empty fields
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
							//System.out.println("ID list length is "+sr_id.size()+" instead of "+sr_act.size());
						}
					}
				}
				else if(file_part.equals("receiverules"))
				{
					//System.out.println("inner: "+elements[j]);
					//System.out.println(pairs.length+" pairs are: "+pairs[0]+" "+pairs[1]+" "+pairs[2]);
					for(int k=0; k<pairs.length; k++)
					{
						choices = pairs[k].split("=");
						choices[0] = choices[0].trim().toLowerCase();
						
						//System.out.println("Pairs: "+pairs[0].toLowerCase()+" "+pairs[1]);
						if(choices[0].equals("action"))
							rr_act.add(choices[1]);
						else if(choices[0].equals("src"))
							rr_src.add(choices[1]);
						else if(choices[0].equals("dst"))
							rr_dst.add(choices[1]);
						else if(choices[0].equals("kind"))
							rr_kind.add(choices[1]);
						else if(choices[0].equals("id"))
							rr_id.add(choices[1]);
						else if(choices[0].equals("nth"))
							rr_nth.add(choices[1]);
						else if(choices[0].equals("everynth"))
							rr_every.add(choices[1]);
						//need to figure out how to make each of the lists the same length by adding wildcards to all empty fields in the right place
						//check pairs.length and once (pairs.length-1) is reached, fill in all empty fields.
						
						if(k == pairs.length-1)  //make each of the lists the same length by adding wildcards to all empty fields
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
							//System.out.println("ID list length is "+sr_id.size()+" instead of "+sr_act.size());
						}
					}
				}
				else if(file_part.equals("configuration")) //handle config third because it happens least often?
				{
					//System.out.println("inner: "+elements[j]);
					//System.out.println(pairs.length+" pairs are: "+pairs[0]+" "+pairs[1]+" "+pairs[2]);
					for(int k=0; k<pairs.length; k++)
					{
						choices = pairs[k].split("=");
						choices[0] = choices[0].trim();
						
						//System.out.println("Pairs: "+pairs[0].toLowerCase()+" "+pairs[1]);
						if(choices[0].toLowerCase().equals("name"))
							names.add(choices[1]);
						else if(choices[0].toLowerCase().equals("ip"))
							ip_addys.add(choices[1]);
						else if(choices[0].toLowerCase().equals("port"))
							ports.add(choices[1]);
						//System.out.println("First of pair is "+pairs[0]);
						/*if(configuration.containsKey(pairs[0]))
							configuration.put(pairs[0], configuration.get(pairs[0])+" "+pairs[1]); //append to the value of that key
						configuration.put(pairs[0], pairs[1]);*/
					}
				}
				else
				{
					System.out.println("Error!");
					break;
				}
			}
		}	
		send_rules.put("action", sr_act);
		send_rules.put("src", sr_src);
		send_rules.put("dst", sr_dst);
		send_rules.put("kind", sr_kind);
		send_rules.put("id", sr_id);
		send_rules.put("nth", sr_nth);
		send_rules.put("everynth", sr_every);
		
		recv_rules.put("action", rr_act);
		recv_rules.put("src", rr_src);
		recv_rules.put("dst", rr_dst);
		recv_rules.put("kind", rr_kind);
		recv_rules.put("id", rr_id);
		recv_rules.put("nth", rr_nth);
		recv_rules.put("everynth", rr_every);
		
		configuration.put("name", names);
		configuration.put("ip", ip_addys);
		configuration.put("port", ports);
		
		//Just temporary print statements for reference
		System.out.println("Config Dict contains "+ configuration.keySet()+" and "+ configuration.values());
		System.out.println("Send Dict contains "+ send_rules.keySet()+" and "+ send_rules.values());
		System.out.println("Recv Dict contains "+ recv_rules.keySet()+" and "+ recv_rules.values());
		
		try {
			yamlInput.close();
		} catch (IOException e) {
			System.out.println("Could not close configuration file\n");
		}
		
		//TODO: Check to see if localName was found in the NAMES section of the config file; if not, return -1 ?
		
	}
	
	String[] getField(String field){
		/* Accessor to return any field desired by the user
		 * program. 
		 * */
		
		String tmp = "";
		
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
		return values;
	}
	
	public String[][] populateOptions(MessagePasser mp, String user_input, int max_fields, int max_options)
	{
		/*Stores all of the possible options for each field of a message from
		 *the lab information (such as for the message action) and from the 
		 *configuration file (in the case of names, for example) into a 
		 *two-dimensional array.
		 */
		
		String[][] all_fields = new String[max_fields][max_options]; 
		String[] names = null;
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
				case 1: //action taken on message
					all_fields[ctr][0] = "drop";
					all_fields[ctr][1] = "delay";
					all_fields[ctr][2] = "duplicate";			
					break;
				case 2: //combine this with case 3
					names = mp.getField("name");
				case 3:
					for(int i=0; i<names.length; i++)
					{
						//System.out.println("Assigned "+src_names[i]+" to all_fields["+ctr+"]["+i+"]");
						all_fields[ctr][i] = names[i]; 
					}
					break;
				/*case 3:
					for(int i=0; i<names.length; i++)
					{
						//System.out.println("Assigned "+src_names[i]+" to all_fields["+ctr+"]["+i+"]");
						all_fields[ctr][i] = names[i]; 
					}
					break; 
				*/
				case 4: //what kind of message
					all_fields[ctr][0] = "ack";
					all_fields[ctr][1] = "lookup";
					break;
				case 5: //the ID mentioned in the config file
					String[] ids = mp.getField("id");
					
					for(int i=0; i<ids.length; i++)
					{
						//System.out.println("Assigned "+src_names[i]+" to all_fields["+ctr+"]["+i+"]");
						all_fields[ctr][i] = ids[i];
						//REREAD config file example with ID to understand how to handle this.
					}
					break; //need to get all IDs already used to make sure we don't reuse if in same name
				case 6: //Nth specifications
					String[] nth = mp.getField("nth"); //do we need source names here or all names?
					
					for(int i=0; i<nth.length; i++)
					{
						//System.out.println("Assigned "+src_names[i]+" to all_fields["+ctr+"]["+i+"]");
						all_fields[ctr][i] = nth[i]; 
					}
					break; //this needs to be in the same order as the source/dest names...how to do this?
				case 7: //EveryNth specifications
					String[] every = mp.getField("everynth"); //do we need source names here or all names?
					
					for(int i=0; i<every.length; i++)
					{
						//System.out.println("Assigned "+src_names[i]+" to all_fields["+ctr+"]["+i+"]");
						all_fields[ctr][i] = every[i]; 
					}
					break; //this needs to be in the same order as the source/dest names...how to do this?
					//just grab them and keep them in order, then it should all match up
			}
		}
		return all_fields;
	}
	
	
	public boolean validateUserRequests(String user_input, MessagePasser mp)
	{
		/* Determines whether the user has followed the usage
		 * guidelines. A send or receive rule can have up to
		 * nine elements:
		 * <receive | send> <action> <src> <dest> <kind> <ID> <Nth> <EveryNth> <data>
		 * 
		 * Return: false (no) or true (yes)
		 *  */
		
		String[] user_options = new String[10];
		
		int ctr = 0;
		int max_fields = 8;
		int max_options = 10;
		ArrayList options = new ArrayList();
		String[][] all_fields = new String[max_fields][max_options]; /*2D array; outside is all possible fields,
																	   inner is all possible options*/
		
		all_fields = populateOptions(mp, user_input, max_fields, max_options);	
		user_options = user_input.trim().split("\\s");

		for (ctr = 0; ctr<max_fields; ctr++) //verify user entered valid options 
		{
			for(int j=0; j<max_options; j++)
			{
				//System.out.println("Currently checking "+all_fields[ctr][j]+" against "+user_options[ctr].toString());
				if(!(all_fields[ctr][j].equals("")) && j != max_options-1)
				{
					if((all_fields[ctr][j].equalsIgnoreCase((user_options[ctr].toString()))))
						break; //found a match for that field
				}
				else //went through all options available for given field
					return false;
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
    		return true;
    	}
    	return false;
	}
}
