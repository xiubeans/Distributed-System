import java.io.Serializable;


public class Message implements Serializable {
	
	int id;
	String src;
	String dest;
	String kind;
	Object payload;
	
	
	public Message(String src, String dest, String kind, Object payload) {
		/* Initialize all fields of the Message object. */
		
		this.src = src;
		this.dest = dest;
		this.kind = kind;
		this.payload = payload;
		
	}

	public void set_id(int id) 
	{ 
		// used by MessagePasser.send, not your app
		//set original values???
		this.id = id;
		
	}
	
	public int get_id() {
		return this.id;
	}
	
	public Message build_message(Message message) {
	/* This will get the message contents from the application program, as
	 * per lab specs. */
		
		return message;
	}
	
	/*
	 * Am not so sure !!!!!!!!!
	 */
	public String getVal(String field, Message message)
	{
		String val = "";
		
		return val;
	}
	
	public Object getPayload() {
		return this.payload;
	}
	
	// other accessors, toString, etc as needed
	
}
