
public abstract class ClockService {

	public static ClockService getInstance(String clock_type)
	{		
		if(clock_type.equals("logical"))
			return new Logical();
		else if (clock_type.equals("vector"))
			return new Vector();
		else
		{
			System.out.println("Unrecognized clock type "+clock_type+". Defaulting to vector.");
			return new Vector();
		}
	}
	
	
	/* method */
	// this method get and increment the clock; be careful about concurrency issue
	// case 1: given an empty ts
	// case 2: given a non-empty ts
	public TimeStamp getTimestamp(TimeStamp ts)
	{
		return ts; //no idea here
	}

	
}
