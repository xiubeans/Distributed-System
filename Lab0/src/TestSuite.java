import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class TestSuite {	
	
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
		
		//check cmdLine input
		if (args.length == 2)
		{
			config_file = args[0];
			local_name = args[1];
			
			/* get a local copy of the config from AFS */
			SFTPConnection svr_conn = new SFTPConnection();
			svr_conn.connect("unix.andrew.cmu.edu", "dpearson");
	    	local_modification_time = svr_conn.getLastModificationTime(CONSTANTS.CONFIGFILE); // record the time-stamp of YAML file
	    	svr_conn.downloadFile(CONSTANTS.CONFIGFILE, CONSTANTS.LOCALPATH); // download the YAML file and put it where it's expected
			
			MessagePasser mp = new MessagePasser(config_file, local_name);
			mp.parseConfig(config_file); //parse the config file
			mp.initSockets(); //setup sockets for all user connections from config file
			
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
						System.out.println("Usage: send <action> <src> <dest> <kind> <id> <Nth> <EveryNth> <data> (* is wildcard)");
						user_input = cmd_line_input.nextLine(); //get the input and check it (pass back out to user if garbage input)
						
						if(!mp.isNewestConfig(local_modification_time, global_modification_time, svr_conn)) //MAKE THIS transparent to user!
							mp.parseConfig(config_file);
						
						if(!mp.validateUserRequests(user_input, mp)) //check user input
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
					case 2: //receive request
						System.out.println("Usage: receive <action> <src> <dest> <kind> <id> <Nth> <EveryNth> (* is wildcard)");
						user_input = cmd_line_input.nextLine(); //get the input and check it (pass back out to user if garbage input)
						
						if(!mp.isNewestConfig(local_modification_time, global_modification_time, svr_conn)) //MAKE THIS transparent to user!
							mp.parseConfig(config_file);
						
						if(!mp.validateUserRequests(user_input, mp)) //check user input and create our message from within it --but this is a receive message....?
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
		{
			System.out.println("Error: incorrect number of args: "+args.length+" (should be 2)");
		}
	}
}
