import java.util.concurrent.atomic.AtomicInteger;


public final class LogicalTimeStamp extends TimeStamp {

	/* fields */
	AtomicInteger ts;

	public LogicalTimeStamp(AtomicInteger t_stamp) //constructor
	{
		this.ts = t_stamp; 
	}
	
	/* methods */
	// a. constructor
	// b. getter and setter
	
	public AtomicInteger getTimeStamp(AtomicInteger t_stamp)
	{
		return this.ts; 
	}
	
	public void setTimeStamp(AtomicInteger t_stamp)
	{
		this.ts = t_stamp;
	}
}
