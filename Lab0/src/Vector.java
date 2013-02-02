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
		
		incrementTimeStamp();
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

	
	/* Miscellaneous Methods */
	
	public void incrementTimeStamp(){
		/* Monotonically increments the value of the current index.
		 * Gets appropriate index to update through TreeMap of names. */
		
		MessagePasser mp = MessagePasser.getInstance();
		this.my_index = ((Integer)mp.names_index.get(mp.local_name)).intValue();
		this.ts.val.set(this.my_index, new AtomicInteger(((AtomicInteger)this.ts.val.get(this.my_index)).intValue()+1));
	}
}
