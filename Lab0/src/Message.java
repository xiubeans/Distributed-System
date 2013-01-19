import java.io.Serializable;


public class Message implements Serializable {
	
	public Message(String src, String dest, String kind, Object data) {
		/* Initialize all fields of the Message object. */
		//Not really sure what to do here...
		//HashMap header = new HashMap();
		
		src = src;
		dest = dest;
		kind = kind;
		data = data;//null;
	}

	public void set_id(int id) 
	{ 
		// used by MessagePasser.send, not your app
		
	}
	public Message build_message(Message message) {
	/* This will get the message contents from the application program, as
	 * per lab specs. */
		
		return message;
	}
	// other accessors, toString, etc as needed
}
