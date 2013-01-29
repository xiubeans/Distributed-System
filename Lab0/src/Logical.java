import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public final class Logical extends ClockService {

	/* fields */
	AtomicInteger timestamp_val;

	protected Logical()
	{
		this.ts = new TimeStamp(1);
	}
	
	
	public void incrementTimeStamp(){
		this.my_index = 0;
		this.ts.val.set(this.my_index, new AtomicInteger(((AtomicInteger)this.ts.val.get(this.my_index)).intValue()+1));
	}

	
	public TimeStampedMessage affixTimestamp(TimeStampedMessage message)
	{
		incrementTimeStamp();
		message.ts = this.ts.clone();
		return message;
	}
	
	public void updateTimestamp(TimeStampedMessage message)
	{
		/* Updates the receiver's timestamp based on the values
		 * coming in from external messages. */
		
		System.out.println("Message timestamp is "+message.ts.toString()+", while mine is "+this.ts.toString());
		
		if(!message.ts.isLess(getTimestamp()))
			this.set(message.ts.val);
		else
			this.incrementTimeStamp();
		System.out.println("Updated MY TIMESTAMP to "+this.ts.val.toString());
	}	
	
	
	 public void set(ArrayList<AtomicInteger> vector_vals)
	  {
		 if(vector_vals.get(0).intValue() >= getTimestamp().val.get(0).intValue())
				this.ts.val.set(0, new AtomicInteger(vector_vals.get(0).intValue() + 1));
	  }
}
