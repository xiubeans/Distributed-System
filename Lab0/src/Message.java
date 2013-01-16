import java.io.Serializable;


public class Message implements Serializable {
	
	public Message(String src, String dest, String kind, Object data) {
		/* Not sure, but probably where the message itself is built. */
		
		src = "";
		dest = "";
		kind = "";
		data = null;
	}

	public void set_id(int id) { // used by MessagePasser.send, not your app
		//set original values???
		
	public void build_message(Object message) {
	/* This will get the message contents from the application program, as
	 * per lab specs. */
		
	}
	// other accessors, toString, etc as needed
}
