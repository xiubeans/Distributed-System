	import java.io.Console;
	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Scanner;
	
public class TestHarness {
	
		public static void gradingTests()
		{
			/*1. Set up 4 processes on at least two distinct machines and show that at least one 
			message can be passed to and from each process (i.e. P1 sends a message to P2, 
			P2 sends a message to P3, P3 sends a message to P4 and P4 sends a message to 
			P1)*/
			
			
			
			/*2. Show that a message with a particular id is dropped, delayed and duplicated (one at 
			a time) based on entries in the conﬁguration ﬁle. For instance, P2 sends a message 
			with id="6" to P4. The conﬁg ﬁle contains "SendRules: - Action : duplicate - ID: 6"  
			and you can show that P4 gets two copies of the message. Show that the ﬁltering 
			works at both the sender and the receiver.  Show that it works only at the sender or 
			receiver.*/
			
			/*3. Show that a message with a particular kind is dropped, delayed and duplicated (one 
			at a time) based on entries in the conﬁguration ﬁle. For instance, P2 sends a 
			message with kind="HTTP_REPLY" and id="7" to P4, followed by second message 
			with kind="IRRELEVANT" and id="8". The conﬁg ﬁle contains "SendRules: - Action : 
			Delay - Kind : HTTP_REPLY" and you can show that P4 receives msg id=8 before it 
			receives msg id=7.*/
			
			/*4. Make sure that reception can be interleaved from multiple sources.  For instance, P1 
			sends a message whose kind will get it delayed at the receiver, P2.  If the next 
			message is sent from P3 to P2, does P2 get it before the message from P1?*/
			
			/*5. Make sure that the Nth and EveryNth rules works properly. This is a bit complex, as 
			rule processing isn't explicitly visible to the user.  Using the example from the lab 
			handout might be good.  Send a series of messages from Charlie to Alice and see if 
			the third message gets duplicated.  Change the conﬁg ﬁle from Nth to EveryNth and 
			see if every third message gets duplicated.*/
		}
	
		/* If the src and dest match, kick it out as invalid input. Put this somewhere. */
		public static void main(String[] args)
		{
			Object data = null;
			Scanner cmd_line_input = new Scanner(System.in);
			String config_file = "";
			String local_name = "";
			String user_input = ""; //reusable temp var
			String src = "";
			String dest = "";
			String kind = "";
			int user_action = 0;
			int local_modification_time = -1;	// record the latest time we download the YAML file
			int global_modification_time = -1;  // record the latest time on servers
			int expectedNumArgs = 2;
			
			//check cmdLine input
			if (args.length == expectedNumArgs)
			{
				config_file = args[0];
				local_name = args[1];
				
				/* get a local copy of the config from AFS */
				SFTPConnection svr_conn = new SFTPConnection();
				svr_conn.connect("unix.andrew.cmu.edu", "dpearson");
		    	local_modification_time = svr_conn.getLastModificationTime(CONSTANTS.CONFIGFILE); // record the time-stamp of YAML file
		    	svr_conn.downloadFile(CONSTANTS.CONFIGFILE, CONSTANTS.LOCALPATH); // download the YAML file and put it where it's expected
				
				MessagePasser mp = MessagePasser.getInstance();
				mp.setConfigAndName(config_file, local_name);
				mp.parseConfig(config_file); //parse the config file
				mp.initSockets(); //setup sockets for all user connections from config file
				
				gradingTests();
				
				
				while(true)
				{
					/* In the interactive portion of this program, the user needs to follow a specific format
					 * when sending or receiving messages. For example, a send message is defined as:
					 * send <action> <src> <dest> <kind> <id> <Nth> <EveryNth> <data>
					 * Wildcards are handled using a special character (*) to denote having no desire to specify
					 * a particular choice for a field.
					 */
					
					//offer the user three choices, then from there give usage for the specific option chosen and wait for input.
					System.out.println("Choose your action (1 for send, 2 for receive, 3 for quit)");
					
					user_input = cmd_line_input.nextLine(); //get the input and check it
					user_action = mp.validOption(user_input);
					
					if(user_action == -1)
						continue;
					
					switch(user_action)
					{
						case 1: //send request
							System.out.println("Usage: send <dest> <kind>");
							//System.out.println("Usage: send <action> <src> <dest> <kind> <id> <Nth> <EveryNth> <data> (* is wildcard)");
							user_input = cmd_line_input.nextLine(); //get the input and check it (pass back out to user if garbage input)
							
							if(!mp.isNewestConfig(local_modification_time, global_modification_time, svr_conn)) //MAKE THIS transparent to user!
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
							data = null;
							Message newMsg = new Message(src, dest, kind, data);
							newMsg = newMsg.build_message(newMsg);
							mp.send(newMsg);
							break;
						case 2: //receive request
							System.out.println("Usage: receive");
							//System.out.println("Usage: receive <action> <src> <dest> <kind> <id> <Nth> <EveryNth> <data> (* is wildcard)");
							user_input = cmd_line_input.nextLine(); //get the input and check it (pass back out to user if garbage input)
							
							if(!mp.isNewestConfig(local_modification_time, global_modification_time, svr_conn)) //MAKE THIS transparent to user!
								mp.parseConfig(config_file);
							
							if(!mp.validateUserRequests(user_input, mp, local_name)) //check user input and create our message from within it --but this is a receive message....?
							{	
								System.out.println("Error: format of message not recognized.");
								continue;
							}
							mp.receive();
							System.out.println("Message is <something goes here?>"); //FIGURE THIS OUT!
							break;
						case 3: //quit the program
							cmd_line_input.close();
							svr_conn.disconnect(); // close the SFTP connection to AFS
							System.exit(1);
						default:
							System.out.println("Unrecognized input "+user_action+".");
							break;
					}			
				}
			}
			else
				System.out.println("Error: incorrect number of args: "+args.length+" (should be "+expectedNumArgs+")");
		}
	}