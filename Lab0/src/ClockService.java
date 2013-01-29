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
		;
	}

	
	public TimeStampedMessage affixTimestamp(TimeStampedMessage message)
	{
		return message;
	}
	
	public void updateTimestamp(TimeStampedMessage message)
	{
		;		
	}	
	
	  
	public void set(ArrayList<AtomicInteger> vector_vals)
	{
	    ;
	}
}
