import java.io.*;
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
	//public MessagePasser(String configuration_filename, String local_name); //constructor 
	public MessagePasser(String configuration_filename, String local_name) {
		/* Create send and recv buffers of 1 MB each */
		String[] send_buf = null; //where we'll store the send buffered messages per connection.
		String[] recv_buf = null;
		
		//create (from the ArrayList class in Java) sockets for the nodes.
		
		//Using TCP sockets, so keep the connection alive for more than just the send/receive of a message.
		
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
	
	void parseConfig(String fname) {
		/*Parses the configuration file. Not sure where to put this. ALSO, not finished.
		 * Taken from a Yaml tutorial on SnakeYaml's Google code page.
		 */
		
		InputStream yamlInput = null;
		Yaml yaml = new Yaml();
		String config = "";
		String[] conf2 = null;
		System.out.println("got here");
		try {
			yamlInput = new FileInputStream(new File(fname));			
		} catch (FileNotFoundException e) { 
			// TODO Auto-generated catch block to error out if not found (also need to check if readable)...
			e.printStackTrace();
		}
		
		for (Object data : yaml.loadAll(yamlInput)) {
			//assertNotNull(data);
			//assertTrue(data.toString().length() > 1);
			System.out.println("data is "+data.toString());
			config = data.toString();
		}
		conf2 = config.split("\\][,|\\}]"); //extract each of the types of things (config, send rules, receive rules)
		System.out.println("config: "+conf2[0]); //should only reference this in constructor
		System.out.println("send rules: "+conf2[1]); //these can be referenced at all times
		System.out.println("receive rules: "+conf2[2]); //these can be referenced at all times
		
		try {
			yamlInput.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}