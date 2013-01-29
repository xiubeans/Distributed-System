import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public abstract class ClockService {

	TimeStamp ts;
	private static ClockService uniqInstance = null; 	
	protected int my_index = 0;
	
	public static synchronized ClockService getInstance(String clock_type, int num_users)
	{		
		if(clock_type.equals("logical")){
			if(uniqInstance == null)
				uniqInstance = new Logical();
		}
		else if(clock_type.equals("vector")){
			if(uniqInstance == null)
				uniqInstance = new Vector(num_users);	
		}
		else
		{
			System.out.println("Unrecognized clock type "+clock_type+". Defaulting to vector.");
			if(uniqInstance == null)
				uniqInstance = new Vector(num_users);
		}		
		return uniqInstance;
	}

	
	public TimeStamp getTimestamp()
	{
		System.out.println("In CS getTimeStamp call");
		return this.ts.clone();
	}

	
	public void incrementTimeStamp(){
		System.out.println("In CS incrementTimeStamp call");
		if((this instanceof Logical))
			this.my_index = 0;
		else
		{
			MessagePasser mp = MessagePasser.getInstance();
			this.my_index = ((Integer)mp.names_index.get(mp.local_name)).intValue();
		}
		this.ts.val.set(this.my_index, new AtomicInteger(((AtomicInteger)this.ts.val.get(this.my_index)).intValue()+1));
		//this.ts.val.set(this.my_index, new AtomicInteger(((AtomicInteger)this.ts.val.get(this.my_index)).getAndIncrement()));
	}
		
	
	public void initTimestamp(){
		;
	}

	
	public TimeStampedMessage affixTimestamp(TimeStampedMessage message)
	{
		message.ts = this.ts.clone();
		incrementTimeStamp();
		message.ts.clone();
		return message;
	}
	
	public void updateTimestamp(TimeStampedMessage message)
	{
		/* Updates the receiver's timestamp based on the values
		 * coming in from external messages. */
		
		System.out.println("Message timestamp is "+message.ts.toString()+", while mine is "+this.ts.toString());
		
		if(!message.ts.isLess(getTimestamp()));
//		if(message.ts.val.intValue() >= getTimestamp())
//			this.ts.val.set(message.ts.val.intValue() + 1);
		
	}	
}
