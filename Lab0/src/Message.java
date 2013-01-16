import java.io.Serializable;


public class Message implements Serializable {
	
	public Message(String src, String dest, String kind, Object data) {
		/* Initialize all fields of the Message object. */
		
		src = "";
		dest = "";
		kind = "";
		data = null;
	}

	public void set_id(int id) 
	{ 
		// used by MessagePasser.send, not your app
		//set original values???
		
	}
	public Message build_message(Message message) {
	/* This will get the message contents from the application program, as
	 * per lab specs. */
		
		return message;
	}
	// other accessors, toString, etc as needed
}
