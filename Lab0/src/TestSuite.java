import java.io.*;

//import org.yaml.snakeyaml.Yaml;

public class TestSuite {

	public static void main(String[] args)
	{
		String configFile = "";
		String localName = "";
		//test program
		System.out.println("testing");
		//check cmdLine input
		if (args.length == 2)
		{
			configFile = args[0];
			localName = args[1];
			
			MessagePasser myMsg = new MessagePasser(configFile, localName); 
		
			while(1) //this will keep the program running (need to offer choices to quit/send/recv more messages)
			{
				/* In the interactive portion of this program, make it such that the user needs to follow
				 * a specific format when sending or receiving messages. For example, a send message might be:
				 * send <src> <dest> <action> ...
				 * It is also important to make sure that we determine how wildcards are handled, such as using
				 * a special character (*?) to denote having no choice for that specific field.
				 * Currently not sure where that goes w.r.t. the program setup. */
				
				//offer the user three choices, then from there give usage for the specific option chosen and wait for input.
				System.out.println("Choose your action (1 for send, 2 for receive, 3 for quit)\n");
				
				String input = System.out.read(); //get the input and check it (pass back out to user if garbage input)
				
				switch(input)
				{
				case 1:
				{
					System.out.println("Usage: send <src> <dest> <action> <blahblahblah>\nEnter * for wildcard.");
					if(validateInput()) //check user input.
						Message newMsg = new Message(String src, String dest, String kind, Object data);
					newMsg = newMsg.build_message(newMsg);
					myMsg.send(newMsg);
				}
					
				case 2:
				{
					System.out.println("Usage: receive <src> <dest> <action> <blahblahblah>\nEnter * for wildcard.");
					if(validateInput()) //check user input and create our message from within it.
						myMsg.receive();
						System.out.println("Message is <something goes here?>\n");
				}
					
				case 3:
				{
					System.exit(1);
				}
				default:
				{
					System.out.println("Unrecognized input "+input+".\n");
				}
				
				}
				
				
				
			
				myMsg.parseConfig(configFile); //parse the config file
			}
		}
		else
		{
			System.out.println("Number of args is "+args.length);
		}
	}
}
