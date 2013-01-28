import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class TestSuiteLogger {	
	static int local_modification_time = -1;	// record the latest time we download the YAML file
	
	/* If the src and dest match, kick it out as invalid input. Put this somewhere. */
	public static void main(String[] args)
	{
		Object data = null;
		Scanner cmd_line_input = new Scanner(System.in);
		String config_file = "";
		String local_name = "logger";
		String user_input = ""; //reusable temp var
		String clock_type = ""; //holds user input for logical or vector clock
		String src = "";
		String dest = "";
		String kind = "";
		int user_action = 0;
		int global_modification_time = -1;  // record the latest time on servers
		int expectedNumArgs = 1;
		
		//check cmdLine input
		if (args.length == expectedNumArgs)
		{
			config_file = args[0];
			
			/* get a local copy of the config from AFS */
			SFTPConnection svr_conn = new SFTPConnection();
			svr_conn.connect("unix.andrew.cmu.edu", "zhechen");
	    	TestSuite.local_modification_time = svr_conn.getLastModificationTime(CONSTANTS.CONFIGFILE); // record the time-stamp of YAML file
	    	svr_conn.downloadFile(CONSTANTS.CONFIGFILE, CONSTANTS.LOCALPATH); // download the YAML file and put it where it's expected	    	
			Logger logger = Logger.getInstance();
			
			logger.setConfigAndName(config_file, local_name);
			logger.initHeaders();
			logger.parseConfig(config_file); //parse the config file
			logger.runServer();
			
			while(true)
			{
				/* In the interactive portion of this program, the user needs to follow a specific format
				 * when sending or receiving messages. For example, a send message is defined as:
				 * send <action> <src> <dest> <kind> <id> <Nth> <EveryNth> <data>
				 * Wildcards are handled using a special character (*) to denote having no desire to specify
				 * a particular choice for a field.
				 */
				
				//offer the user three choices, then from there give usage for the specific option chosen and wait for input.
				System.out.println("Choose your action (1 for Print Order, 2 for Clear Messages, 3 for quit)");
				
				user_input = cmd_line_input.nextLine(); //get the input and check it
				user_action = logger.validOption(user_input);
				
				if(user_action == -1)
					continue;
				
				switch(user_action)
				{
					case 1: // print request
						// put code here
						System.out.println("Have a print request, but it currently does nothing...");
						break;
					case 2: // clear messages request
						logger.clearAllQueues();
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
