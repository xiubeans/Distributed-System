import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public final class Vector extends ClockService {

	/* fields */
	ArrayList<AtomicInteger> vector;

	public Vector(int num_users)
	{
		this.ts = new TimeStamp(num_users);
	}

	
	public void incrementTimeStamp(){
			MessagePasser mp = MessagePasser.getInstance();
			this.my_index = ((Integer)mp.names_index.get(mp.local_name)).intValue();
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
				
		for(int i=0; i< this.ts.val.size(); i++)
		{
			if(this.ts.val.get(i).get() <= message.ts.val.get(i).get())
				this.ts.val.set(i, new AtomicInteger(message.ts.val.get(i).get()));
		}
		this.incrementTimeStamp();
		System.out.println("Updated MY TIMESTAMP to "+this.ts.val.toString());
	}	
}
