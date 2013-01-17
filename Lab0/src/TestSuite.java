import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class TestSuite {	
	
	public static boolean validateInput(String user_input, MessagePasser mp)
	{
		/* Determines whether the user has followed the usage
		 * guidelines. A send or receive rule can have up to
		 * eight elements:
		 * <receive | send> <action> <src> <dest> <kind> <ID> <Nth> <EveryNth>
		 * 
		 * Return: false (no) or true (yes)
		 *  */
		
		String[] fields = new String[10];
		
		int ctr = 0;
		int max_fields = 8;
		int max_options = 10;
		ArrayList options = new ArrayList();
		//List<String[]> all_fields_holder = new ArrayList<String[]>();
		//String[] all_fields = new String
		String[][] all_fields = new String[max_fields][max_options]; 
		
		//TODO: figure out how to grab the source names from below such that they can be stored appropriately in my data structure
		
		/*build a 2D array. Outside contains all fields,
		 *  inside contains options of each field. Refactor as
		 *  a function at some point.
		 */
		String[] names = null;
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
				case 2:
					names = mp.getField("name"); //do we need source names here or all names?
					
					for(int i=0; i<names.length; i++)
					{
						//System.out.println("Assigned "+src_names[i]+" to all_fields["+ctr+"]["+i+"]");
						all_fields[ctr][i] = names[i]; 
					}
					break; //need to get source names from the config file and add them here
				case 3:	//do we need dest names here or all names (currently using ALL names)?
					for(int i=0; i<names.length; i++)
					{
						//System.out.println("Assigned "+src_names[i]+" to all_fields["+ctr+"]["+i+"]");
						all_fields[ctr][i] = names[i]; 
					}
					break; 

				case 4: //what kind of message
					all_fields[ctr][0] = "ack";
					all_fields[ctr][1] = "lookup";
					break;
				case 5: //the ID mentioned in the config file
					String[] ids = mp.getField("id"); //do we need source names here or all names?
					
					for(int i=0; i<ids.length; i++)
					{
						//System.out.println("Assigned "+src_names[i]+" to all_fields["+ctr+"]["+i+"]");
						all_fields[ctr][i] = ids[i]; 
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
		
		fields = user_input.trim().split("\\s");
		
		//for(ctr=0;ctr<fields.length; ctr++){ System.out.println("fields["+ctr+"] is "+fields[ctr]);}
		
		//createOptions(); //Move 2D array to this later

		for (ctr = 0; ctr<max_fields; ctr++) //verify the fields exist 
		{
			//if(!(all_fields.get(ctr).contains(fields[ctr].toString().toLowerCase())))
			for(int j=0; j<max_options; j++)
			{
				System.out.println("Currently checking "+all_fields[ctr][j]+" against"+fields[ctr].toString());
				if(all_fields[ctr][j].equals(null))
					return false; //gone through all of the options already
				else if((all_fields[ctr][j].equalsIgnoreCase((fields[ctr].toString()))))
					break; //we found a match in that field
				if(j == max_options-1) //got through the loop without finding a match
					return false;
			}
		}
		
		return true;
	}
	
	public static void main(String[] args)
	{
		String config_file = "";
		String local_name = "";
		String user_input = ""; //reusable temp var
		int user_action = 0;
		String src = "";
		String dest = "";
		String kind = "";
		//String data = "";
		Object data = null;
		Scanner cmd_line_input = new Scanner(System.in);
		int local_modification_time = -1;	// record the latest time we download the YAML file
		int global_modification_time = -1;  // record teh latest time on servers
		
		//test program
		System.out.println("testing");
		
		//check cmdLine input
		if (args.length == 2)
		{
			config_file = args[0];
			local_name = args[1];
			/* setup the connection to AFS
			 * please setup the fields in CONSTANTS.java at first */
			SFTPConnection svr_conn = new SFTPConnection();
			svr_conn.connect("unix.andrew.cmu.edu", "dpearson");
			//SFTPConnection conn = new SFTPConnection();
	    	//conn.connect(CONSTANTS.HOST, CONSTANTS.USER, CONSTANTS.PWD);
	    	local_modification_time = svr_conn.getLastModificationTime(CONSTANTS.CONFIGFILE);	// record the time-stamp of YAML file
	    	svr_conn.downloadFile(CONSTANTS.CONFIGFILE, CONSTANTS.LOCALPATH);	// download the YAML file
			
			MessagePasser mp = new MessagePasser(config_file, local_name);
			
			mp.parseConfig(config_file); //parse the config file
			mp.initSockets(); //setup sockets here (see comment in MessagePasser constructor)
			
			
			while(true)
			{
				/* In the interactive portion of this program, make it such that the user needs to follow
				 * a specific format when sending or receiving messages. For example, a send message might be:
				 * send <src> <dest> <action> ...
				 * It is also important to make sure that we determine how wildcards are handled, such as using
				 * a special character (*?) to denote having no choice for that specific field.
				 * Currently not sure where that goes w.r.t. the program setup. */
				
				//offer the user three choices, then from there give usage for the specific option chosen and wait for input.
				System.out.println("Choose your action (1 for send, 2 for receive, 3 for quit)");
				user_input = cmd_line_input.nextLine(); //get the input and check it (pass back out to user if garbage input)
				
				if(user_input.length() > 1)
				{
					System.out.println("Unrecognized option "+user_input+". Choices are 1, 2, and 3.");
					continue;
				}
				
				try {
					user_action = Integer.parseInt(user_input);
				} catch(NumberFormatException e) {
					System.out.println(user_input+" is not an integer.");
					continue;
				}
				
				// if the user is going to send/receive, try to get the latest YAML file at first
				if(user_action == 1 || user_action == 2){
					// get the YAML file at first
					if(!svr_conn.isConnected())
						svr_conn.connect(CONSTANTS.HOST, CONSTANTS.USER);//, CONSTANTS.PWD);
			    	global_modification_time = svr_conn.getLastModificationTime(CONSTANTS.CONFIGFILE);	// record the time-stamp of YAML file
			    	if(global_modification_time != local_modification_time)
			    		svr_conn.downloadFile(CONSTANTS.CONFIGFILE, CONSTANTS.LOCALPATH);	// download the YAML file
				}
				
				switch(user_action)
				{
					case 1:
						System.out.println("Usage: send <action> <src> <dest> <kind> <id> <Nth> <EveryNth> <data> (* is wildcard)");
						user_input = cmd_line_input.nextLine(); //get the input and check it (pass back out to user if garbage input)
						if(!validateInput(user_input, mp)) //check user input.
						{	
							System.out.println("Error: format of message not recognized.");
							continue;
						}
						String[] fields = user_input.trim().split("\\s");
						src = fields[2];
						dest = fields[3];
						kind = fields[4];
						data = fields[8];
						Message newMsg = new Message(src, dest, kind, data);
						newMsg = newMsg.build_message(newMsg);
						mp.send(newMsg);
						break;
					case 2:
						System.out.println("Usage: receive <action> <src> <dest> <kind> <id> <Nth> <EveryNth> (* is wildcard)");
						user_input = cmd_line_input.nextLine(); //get the input and check it (pass back out to user if garbage input)
						if(!validateInput(user_input, mp)) //check user input and create our message from within it.
						{	
							System.out.println("Error: format of message not recognized.");
							continue;
						}
						mp.receive();
						System.out.println("Message is <something goes here?>");
						break;
					case 3:
						cmd_line_input.close();
						svr_conn.disconnect();		// close the SFTP connection to AFS
						System.exit(1);
					default:
						System.out.println("Unrecognized input "+user_action+".");
						break;
				}			
			}
		}
		else
		{
			System.out.println("Error: incorrect number of args: "+args.length+" (should be 2)");
		}
		
		
	}
}
