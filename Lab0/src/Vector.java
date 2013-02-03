import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public final class Vector extends ClockService {

	/* Fields */
	ArrayList<AtomicInteger> vector;

	
	/* Constructor */
	public Vector(int num_users)
	{
		this.ts = new TimeStamp(num_users);
	}


	/* Mutators */
	
	public TimeStampedMessage affixTimestamp(TimeStampedMessage message)
	{
		/* Adds the appropriate timestamp to a message. Performs
		 * a clone operation to disconnect the tie to this original
		 * timestamp instance. */
		
		MessagePasser tmpMP = MessagePasser.getInstance();
		
		if(message.type.equals("multicast") && message.dest.equals(tmpMP.names_index.firstKey()))
			incrementTimeStamp(); //this allows all multicast messages (in one series) to have the same timestamp
		else if(message.type.equals("unicast"))
			incrementTimeStamp(); //we still want to timestamp all unicast messages
		message.ts = this.ts.clone();
		return message;
	}
	
	public void updateTimestamp(TimeStampedMessage message)
	{
		/* Updates the receiver's timestamp based on the values
		 * coming in from external messages. */
				
		for(int i=0; i< this.ts.val.size(); i++)
		{
			if(this.ts.val.get(i).get() <= message.ts.val.get(i).get())
				this.ts.val.set(i, new AtomicInteger(message.ts.val.get(i).get()));
		}
		this.incrementTimeStamp();
		System.out.println("Updated MY TIMESTAMP to "+this.ts.val.toString());
	}	

	
	 public void fabricate(String[] ts_vals, TimeStamp ts)
	 {
		 /* Sets all fields of a timestamp to values coming in from 
		  * a parseTS call. */
		 
		for(int i=0; i<ts_vals.length; i++)
		{
			ts.val.set(i, new AtomicInteger(Integer.parseInt(ts_vals[i])));
		}
		return; 
	 }
	
	
	/* Miscellaneous Methods */
	
	public void incrementTimeStamp(){
		/* Monotonically increments the value of the current index.
		 * Gets appropriate index to update through TreeMap of names. */
		
		MessagePasser mp = MessagePasser.getInstance();
		this.my_index = ((Integer)mp.names_index.get(mp.local_name)).intValue();
		this.ts.val.set(this.my_index, new AtomicInteger(((AtomicInteger)this.ts.val.get(this.my_index)).intValue()+1));
	}
	
	
	public void decrementTimeStamp(){
		/* Monotonically decrements the value of the current index.
		 * Gets appropriate index to update through TreeMap of names.
		 * This is used ONLY to allow correct timestamping of duplicate
		 * messages in multicast, and does not actually decrement a 
		 * timestamp within the system (which would obviously be wrong). */
		
		MessagePasser mp = MessagePasser.getInstance();
		this.my_index = ((Integer)mp.names_index.get(mp.local_name)).intValue();
		this.ts.val.set(this.my_index, new AtomicInteger(((AtomicInteger)this.ts.val.get(this.my_index)).intValue()-1));
	}
	
	
	public TimeStamp parseTS(String ts)
	{
		/* Takes a string representation of a timestamp
		 * and returns a valid timestamp object. */
		
		String[] ts_vals = null;
		  
		ts.replaceAll("[\\[\\]]", "").trim(); //remove brackets
		ts_vals = ts.split(",");
		
		MessagePasser tmpMP = MessagePasser.getInstance();
		TimeStamp ts_obj = new TimeStamp(tmpMP.getVectorSize()); 
		fabricate(ts_vals, ts_obj);
		
		return ts_obj;
	}	
}
