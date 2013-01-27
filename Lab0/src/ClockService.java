
public class ClockService {

	TimeStamp ts;
	private static ClockService uniqInstance = null; 	
	
	protected ClockService(String clock_type) {
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
	}
	
	public static synchronized ClockService getInstance(String clock_type)
	{
		if(uniqInstance == null)
			uniqInstance = new ClockService(clock_type);
		return uniqInstance;
	}
	
	/* method */
	// this method get and increment the clock; be careful about concurrency issue
	// case 1: given an empty ts
	// case 2: given a non-empty ts
	public TimeStamp getTimestamp(TimeStamp ts)
	{
		return this.ts;
	}

	
}
