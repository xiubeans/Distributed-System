import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public abstract class ClockService {

	/* Fields */
	TimeStamp ts;
	private static ClockService uniqInstance = null; 	
	protected int my_index = 0;
	
	
	public static synchronized ClockService getInstance(String clock_type, int num_users)
	{
		/* Allows a singleton to be created. */
		
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

	
	/* Accessors */
	
	public TimeStamp getTimestamp()
	{
		return this.ts.clone();
	}

	
	/* Mutators */
	
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
	
	
	public void fabricate(String[] vals, TimeStamp ts)
	{
		;
	}
	
	/* Miscellaneous Methods */
	
	public void incrementTimeStamp(){
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
