import java.io.Serializable;


public class Message implements Serializable {
	
	/* Fields */
	int id;
	int mc_id; //the multicast ID
	String src;
	String dest;
	String kind;
	String type;
	Object payload;
	
	
	/* Constructor */
	public Message(String src, String dest, String kind, String type, Object payload) {
		this.src = src;
		this.dest = dest;
		this.kind = kind;
		this.type = type;
		this.payload = payload;
	}

	
	/* Accessors */
	
	public int get_id() {
		/* Get the message ID. */
		return this.id;
	}
	
	
	public int get_mcast_id() {
		/* Get the multicast message ID. */
		return this.mc_id;
	}

	
	public String getVal(String field, Message message)
	{
		/* Generic accessor for multiple fields
		 * of a message. */
		
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
	
	
	/* Mutators */
	
	public void set_id(int id) 
	{ 
		// used by MessagePasser.send, not your app
		this.id = id;
	}
	
	
	public void set_mcast_id(int mid) 
	{ 
		this.mc_id = mid;
	}
	
	
	/* Miscellaneous Methods */
	
	@Override
	public String toString() {
		return "@ " + "Message: src = " + this.src + ", id = " + this.id + "multicast id = " + this.mc_id + ", dest = " + this.dest + ", kind = " + this.kind + ", type = " + this.type;
	}
	
	
	public void print() {
		System.out.println(this.toString());
	}
}
