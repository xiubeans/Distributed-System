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
		//LinkedHashMap<List<String>, List<String>> file_map = (LinkedHashMap<List<String>, List<String>>) data;
		
		String whole = "";
		String[] elements = new String[10];
		String[] pairs = new String[10];
		Set set = file_map.entrySet();
		Iterator i = set.iterator();
		HashMap configuration = new HashMap<String, String>();
		int j = 0;
		ArrayList names = new ArrayList();
		ArrayList ip_addys = new ArrayList();
		ArrayList ports = new ArrayList();
		
		while(i.hasNext())
		{
			Map.Entry me = (Map.Entry)i.next();
			System.out.print(me.getKey() + ": ");
			System.out.println(me.getValue());
			ArrayList<String> inner = new ArrayList<String>();
			inner.add(me.getValue().toString());
			whole = inner.toString();
			whole = whole.replaceAll("[\\[\\]\\{]", "");
			//System.out.println("Whole is: "+whole);
			elements = whole.split("\\},?");
			//System.out.println("element is: "+elements[0]);
		
			for(j=0; j<elements.length; j++) //fix this stuff, make it scalable, etc...
			{
				System.out.println("inner: "+elements[j]);
				pairs = elements[j].split(", ");
				pairs = pairs[0].split("=");
				System.out.println("Pairs: "+pairs[0]+" "+pairs[1]);
				if(pairs[0].toLowerCase().equals("name"))
				{
					System.out.println("In names, name is "+pairs[1]);
					names.add(pairs[1]);
				}
				else if(pairs[0].toLowerCase().equals("ip"))
					ip_addys.add(pairs[1]);
				else if(pairs[0].toLowerCase().equals("port"))
					ports.add(pairs[1]);
				//System.out.println("First of pair is "+pairs[0]);
				/*if(configuration.containsKey(pairs[0]))
					configuration.put(pairs[0], configuration.get(pairs[0])+" "+pairs[1]); //append to the value of that key
				configuration.put(pairs[0], pairs[1]);*/
			}
			configuration.put("name", names);
			configuration.put("ip", ip_addys);
			configuration.put("port", ports);
			
			System.out.println("Dictionary contains "+ configuration.keySet()+" and "+ configuration.values());
		}
		
		/*for(j=0; j<elements.length; j++)
		{
			System.out.println("Element: "+elements[j]+"\n");
		}*/
		
		
		try {
			yamlInput.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//TODO: Check to see if localName was found in the NAMES section of the config file; if not, return -1 ?
		
	}
}