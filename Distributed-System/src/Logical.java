import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public final class Logical extends ClockService {

	/* Fields */
	AtomicInteger timestamp_val;

	
	/* Constructor */
	protected Logical()
	{
		this.ts = new TimeStamp(1);
	}
	

	/* Accessors */
	
	public TimeStampedMessage affixTimestamp(TimeStampedMessage message)
	{
		/* Adds the appropriate timestamp to a message. Performs
		 * a clone operation to disconnect the tie to this original
		 * timestamp instance. */
		
		incrementTimeStamp();
		message.ts = this.ts.clone();
		return message;
	}

	
	/* Mutators */
	
	public void updateTimestamp(TimeStampedMessage message)
	{
		/* Updates the receiver's timestamp based on the values
		 * coming in from external messages. */
		
		if(!message.ts.isLess(getTimestamp()))
			this.set(message.ts.val);
		else
			this.incrementTimeStamp();
		System.out.println("Updated MY TIMESTAMP to "+this.ts.val.toString());
	}	
	
	
	 public void set(ArrayList<AtomicInteger> vector_vals)
	  {
		 /* Properly sets the logical timestamp to based on 
		  * Max(current TS, incoming TS) + 1. */
		 
		 if(vector_vals.get(0).intValue() >= getTimestamp().val.get(0).intValue())
				this.ts.val.set(0, new AtomicInteger(vector_vals.get(0).intValue() + 1));
	  }

	 
	 public void fabricate(String[] ts_vals, TimeStamp ts)
	 {
		; 
	 }
	 
	 
	 /* Miscellaneous Methods */
		
	public void incrementTimeStamp(){
		/* Monotonically increments the value of the current index.
		 * Since this is a logical timestamp, the index is always 0. */
		
		this.my_index = 0;
		this.ts.val.set(this.my_index, new AtomicInteger(((AtomicInteger)this.ts.val.get(this.my_index)).intValue()+1));
	}
	
	
	public void decrementTimeStamp(){
		;
	}
	
	
	public TimeStamp parseTS(String ts)
	{
		/* Takes a string representation of a timestamp
		 * and returns a valid timestamp object. */
		
		MessagePasser tmpMP = MessagePasser.getInstance();
		TimeStamp ts_obj = new TimeStamp(tmpMP.getVectorSize()); 
		
		return ts_obj;
	}	
}
