import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public abstract class ClockService {

	TimeStamp ts = new TimeStamp();
	private static ClockService uniqInstance = null; 	
	
	
	public static synchronized ClockService getInstance(String clock_type)
	{		
		if(clock_type.equals("logical")){
			if(uniqInstance == null)
				uniqInstance = new Logical();
		}
		else if(clock_type.equals("vector")){
			if(uniqInstance == null)
				uniqInstance = new Vector();	
		}
		else
		{
			System.out.println("Unrecognized clock type "+clock_type+". Defaulting to vector.");
			if(uniqInstance == null)
				uniqInstance = new Vector();
		}		
		return uniqInstance;
	}

	
	public int getTimestamp()
	{
		System.out.println("In CS getTimeStamp call");
		return this.ts.val.intValue();
	}

	
	public int incrementTimeStamp(){
		System.out.println("In CS incrementTimeStamp call");
		return this.ts.val.incrementAndGet(); //just throw away the return
	}
		
	
	public void initTimestamp(){
		;
	}

	
	public void updateTimestamp(TimeStampedMessage message)
	{
		/* Updates the receiver's timestamp based on the values
		 * coming in from external messages. */
		
		System.out.println("Message timestamp is "+message.ts.val+", while mine is "+this.ts.val);
		
		if(message.ts.val.intValue() >= getTimestamp())
			this.ts.val.set(message.ts.val.intValue() + 1);
		
	}	
}
