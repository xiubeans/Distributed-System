
import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TestSuiteMulticast {

	static int local_modification_time = -1;	// record the latest time we download the YAML file
	
	/* If the src and dest match, kick it out as invalid input. Put this somewhere. */
	public static void main(String[] args)
	{
		Object data = null;
		TimeStamp tstmp = null; //new TimeStamp();
		Scanner cmd_line_input = new Scanner(System.in);
		String config_file = "";
		String local_name = "";
		String user_input = ""; //reusable temp var
		String clock_type = ""; //holds user input for logical or vector clock
		String src = "";
		String dest = "";
		String kind = "";
		String type = ""; //whether it is a multicast or unicast message
		int user_action = 0;
		int global_modification_time = -1;  // record the latest time on servers
		int expectedNumArgs = 3;
		
		//check cmdLine input
		if (args.length == expectedNumArgs)
		{
			config_file = args[0];
			local_name = args[1];
			clock_type = args[2].toString().toLowerCase();
			
			if(clock_type.equalsIgnoreCase("logical"))
			{
				System.out.println("Logical clocks are not available for multicast service. Defaulting to vector.");
				clock_type = "vector";
			}
			
			/* get a local copy of the config from AFS */
			SFTPConnection svr_conn = new SFTPConnection();
			svr_conn.connect("unix.andrew.cmu.edu", "dpearson");
	    	TestSuite.local_modification_time = svr_conn.getLastModificationTime(CONSTANTS.CONFIGFILE); // record the time-stamp of YAML file
	    	svr_conn.downloadFile(CONSTANTS.CONFIGFILE, CONSTANTS.LOCALPATH); // download the YAML file and put it where it's expected	    	
			MessagePasser mp = MessagePasser.getInstance();		
			
			mp.setConfigAndName(config_file, local_name);
			mp.initHeaders();
			mp.parseConfig(config_file); //parse the config file
			mp.runServer();
			mp.listNodes();
			ClockService clock = ClockService.getInstance(clock_type, mp.getVectorSize()); //here is where we will instantiate the clock via the object factory			
						
			while(true)
			{
				/* In the interactive portion of this program, the user needs to follow a specific format
				 * when sending or receiving messages. For example, a send message is defined as:
				 * send <action> <src> <dest> <kind> <id> <Nth> <EveryNth> <data>
				 * Wildcards are handled using a special character (*) to denote having no desire to specify
				 * a particular choice for a field.
				 */
				
				//offer the user three choices, then from there give usage for the specific option chosen and wait for input.
				System.out.println("Choose your action (1 for send, 2 for receive, 3 for quit, 4 for send multicast)");
				
				user_input = cmd_line_input.nextLine(); //get the input and check it
				user_action = mp.validOption(user_input);
				
				if(user_action == -1)
					continue;
				
				switch(user_action)
				{				
					case 1: //send request
						System.out.println("Usage: send <dest> <kind>");
						user_input = cmd_line_input.nextLine(); //get the input and check it (pass back out to user if garbage input)

						if(!mp.isNewestConfig(svr_conn))
							mp.parseConfig(config_file);
											
						if(!mp.validateUserRequests(user_input, mp, local_name)) //check user input
						{	
							System.out.println("Error: format of message not recognized.");
							continue;
						}
						
						String[] fields = user_input.trim().split("\\s");
						src = local_name;
						dest = fields[1];
						kind = fields[2];
						type = "unicast";
						data = null;
						TimeStampedMessage newMsg = new TimeStampedMessage(tstmp, src, dest, kind, type, data);
						mp.send(newMsg, clock);
						break;
					case 2: //receive request
						System.out.println("Usage: receive");
						user_input = cmd_line_input.nextLine(); //get the input and check it (pass back out to user if garbage input)

						if(!mp.isNewestConfig(svr_conn)) //MAKE THIS transparent to user!
							mp.parseConfig(config_file);

						if(!mp.validateUserRequests(user_input, mp, local_name)) //check user input and create our message from within it
						{	
							System.out.println("Error: format of message not recognized.");
							continue;
						}
						mp.receive(clock);
						mp.print();
						break;
					case 3: //quit the program
						cmd_line_input.close();
						svr_conn.disconnect(); //close the SFTP connection to AFS
						mp.closeConnections(); //close sockets 
						System.exit(1);
					case 4: //multicast send request
						System.out.println("Usage: send multicast <kind>");
						user_input = cmd_line_input.nextLine(); //get the input and check it (pass back out to user if garbage input)

						if(!mp.isNewestConfig(svr_conn))
							mp.parseConfig(config_file);
											
						if(!mp.validateUserRequests(user_input, mp, local_name)) //check user input
						{	
							System.out.println("Error: format of message not recognized.");
							continue;
						}
						
						fields = user_input.trim().split("\\s");
						
						//System.out.println("Names are: "+mp.names_index.keySet());
						
						/*multicast loop placed out here to make integration with 
						 * current code easiest; no changes to MessagePasser needed!*/
						
						for (Map.Entry entry : mp.names_index.entrySet()) 
					    {
							String name = (String)entry.getKey();
							src = local_name;
							dest = name;
							kind = fields[2];
							type = "multicast";
							data = null;
							
							newMsg = new TimeStampedMessage(tstmp, src, dest, kind, type, data);
							//System.out.println("About to send "+kind+" message from "+src+" to "+dest);
							mp.send(newMsg, clock);
					    }
					
						break;
					default:
						System.out.println("Unrecognized input "+user_action+".");
						break;
				}
//				System.out.println("Jumped out of TSM case statement");
//				mp.print();
			}
		}
		else
			System.out.println("Error: incorrect number of args: "+args.length+" (should be "+expectedNumArgs+")");
	}
}
