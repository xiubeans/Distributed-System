import java.io.*;
import java.util.ArrayList;

//import org.yaml.snakeyaml.Yaml;

public class TestSuite {

	public static boolean validateInput(String user_input)
	{
		/* Determines whether the user has followed the usage
		 * guidelines. A send or receive rule can have up to
		 * eight elements:
		 * <receive | send> <action> <src> <dest> <kind> <ID> <Nth> <EveryNth>
		 * 
		 * Return: false (no) or true (yes)
		 *  */
		
		String[] fields = new String[8];
		
		int ctr = 0;
		int max_fields = 8;
		ArrayList<ArrayList<String>> all_fields = new ArrayList<ArrayList<String>>();
		ArrayList<String> options = new ArrayList<String>();
		
		/*build a 2D arrayList. Outside contains all fields,
		 *  inside contains options of each field. Refactor as
		 *  a function at some point.
		 */
		for (ctr = 0; ctr<max_fields; ctr++) 
		{
			switch(ctr)
			{
			case 0:
			{
				options.add("send");
				options.add("receive");
				all_fields.add(options);
				break;
			}
			case 1:
			{
				options.add("drop");
				options.add("delay");
				options.add("duplicate");
				all_fields.add(options);
				break;
			}
			case 2:
			{
				all_fields.add(options);
				break; //need to get source names from the config file and add them here
			}
			case 3:
			{
				all_fields.add(options);
				break; //need to get dest names from the config file and add them here
			}
			case 4:
			{
				options.add("ack");
				options.add("lookup");
				all_fields.add(options);
				break;//others????
			}
			case 5:
			{
				all_fields.add(options);
				break; //need to get all IDs already used to make sure we don't reuse if in same name
			}
			case 6:
			{
				all_fields.add(options);
				break; //this needs to be in the same order as the source/dest names...how to do this?
			}
			case 7:
			{
				all_fields.add(options);
				break; //this needs to be in the same order as the source/dest names...how to do this?
			}
			}
		}
		
				
		//trim whitespace, remove extra whitespace between fields, then do split on " " to get all fields. From there, check all things.
		user_input = user_input.trim();
		
		for (String field : user_input.split("\\s"))
		{
			fields[ctr] = field;
			ctr++;
			if (ctr > max_fields-1)
				return false;
		}
		
		//createOptions(); //Move 2D array to this later
		
		for (ctr = 0; ctr<max_fields; ctr++) //verify the fields exist 
		{
			if(!(all_fields.get(ctr).contains(fields[ctr].toString().toLowerCase())))
				return false;
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
		Object data = null;
		
		//test program
		System.out.println("testing");
		//check cmdLine input
		if (args.length == 2)
		{
			config_file = args[0];
			local_name = args[1];
			
			MessagePasser mp = new MessagePasser(config_file, local_name); 
			mp.parseConfig(config_file); //parse the config file

			
//			while(true) //this will keep the program running (need to offer choices to quit/send/recv more messages)
//			{
//				/* In the interactive portion of this program, make it such that the user needs to follow
//				 * a specific format when sending or receiving messages. For example, a send message might be:
//				 * send <src> <dest> <action> ...
//				 * It is also important to make sure that we determine how wildcards are handled, such as using
//				 * a special character (*?) to denote having no choice for that specific field.
//				 * Currently not sure where that goes w.r.t. the program setup. */
//				
//				//offer the user three choices, then from there give usage for the specific option chosen and wait for input.
//				System.out.println("Choose your action (1 for send, 2 for receive, 3 for quit)\n");
//				
//				user_input = System.in.toString(); //get the input and check it (pass back out to user if garbage input)
//				
//				if(user_input.length() > 1)
//				{
//					System.out.println("Unrecognized option "+user_input+". Choices are 1, 2, and 3.\n");
//					continue;
//				}
//				
//				try {
//					Integer.parseInt(user_input);
//				} catch(NumberFormatException e) {
//					System.out.println(user_input+" is not an integer.\n");
//				}
//				
//				user_action = Integer.parseInt(user_input);
//				
//				switch(user_action)
//				{
//				case 1:
//				{
//					System.out.println("Usage: send <src> <dest> <action> <blahblahblah>\nEnter * for wildcard.");
//					user_input = System.in.toString(); //get the input and check it (pass back out to user if garbage input)
//					if(!validateInput(user_input)) //check user input.
//					{	
//						System.out.println("Error: format of message not recognized.\n");
//						continue;
//					}
//					Message newMsg = new Message(src, dest, kind, data);
//					newMsg = newMsg.build_message(newMsg);
//					mp.send(newMsg);
//					break;
//				}
//					
//				case 2:
//				{
//					System.out.println("Usage: receive <src> <dest> <action> <blahblahblah>\nEnter * for wildcard.");
//					user_input = System.in.toString(); //get the input and check it (pass back out to user if garbage input)
//					if(!validateInput(user_input)) //check user input and create our message from within it.
//					{	
//						System.out.println("Error: format of message not recognized.\n");
//						continue;
//					}
//					mp.receive();
//					System.out.println("Message is <something goes here?>\n");
//					break;
//				}
//					
//				case 3:
//				{
//					System.exit(1);
//				}
//				default:
//				{
//					System.out.println("Unrecognized input "+user_action+".\n");
//					break;
//				}
//				
//				}			
//			}
		}
		else
		{
			System.out.println("Error: incorrect number of args: "+args.length+" (should be 2)\n");
		}
	}
}
