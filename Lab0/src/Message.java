import java.io.Serializable;


public class Message implements Serializable {
	
	int id;
	int mc_id; //the multicast ID
	String src;
	String dest;
	String kind;
	String type;
	Object payload;
	
	
	public Message(String src, String dest, String kind, String type, Object payload) {
		/* Initialize all fields of the Message object. */
		
		this.src = src;
		this.dest = dest;
		this.kind = kind;
		this.type = type;
		this.payload = payload;
	}

	public void set_id(int id) 
	{ 
		// used by MessagePasser.send, not your app
		this.id = id;
		
	}
	
	
	public void set_mcast_id(int mid) 
	{ 
		// used by MessagePasser.send, not your app
		//Only set this IF it is a multicast message (otherwise set to an empty string)
		this.mc_id = mid;
		
	}
	
	
	public int get_id() {
		return this.id;
	}
	
	
	public int get_mcast_id() {
		return this.mc_id;
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
		else if(field.equals("mc_id"))
			return "" + this.mc_id;
		else if(field.equals("src"))
			return this.src;
		else if(field.equals("kind"))
			return this.kind;
		else if(field.equals("dest"))
			return this.dest;
		else if(field.equals("type"))
			return this.type;
		else 
			return "";
			
	}
	
	public Object getPayload() {
		return this.payload;
	}
	

	
	@Override
	public String toString() {
		return "@ " + "Message: src = " + this.src + ", id = " + this.id + "multicast id = " + this.mc_id + ", dest = " + this.dest + ", kind = " + this.kind + ", type = " + this.type;
	}
	
	public void print() {
		System.out.println(this.toString());
	}
}
