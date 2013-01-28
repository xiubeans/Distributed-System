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
	
	public String getVal(String field, Message message)
	{
		if(field.equals("id"))
			return "" + this.id;
		else if(field.equals("src"))
			return this.src;
		else if(field.equals("kind"))
			return this.kind;
		else if(field.equals("dest"))
			return this.dest;
		else 
			return "";
			
	}
	
	public Object getPayload() {
		return this.payload;
	}
	

	
	@Override
	public String toString() {
		return "@ " + "Message: src = " + this.src + ", id = " + this.id + ", dest = " + this.dest + ", kind = " + this.kind;
	}
	
	public void print() {
		System.out.println(this.toString());
	}
}
