import java.io.Serializable;


public class Message implements Serializable {
	
	public Message(String asrc, String adest, String akind, Object adata) {
		/* Initialize all fields of the Message object. */
		//Not really sure what to do here...
		//HashMap header = new HashMap();
		
		String src = asrc;
		String dest = adest;
		String kind = akind;
		Object data = adata;
	}
	
	public String getVal(String field, Message message)
	{
		String val = "";
		
		return val;
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
