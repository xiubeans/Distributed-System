import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public final class VectorTimeStamp extends TimeStamp  {
	
	/* fields */
	ArrayList<AtomicInteger> ts;

	public VectorTimeStamp(ArrayList vec_t_stamp) //constructor
	{
		this.ts = vec_t_stamp; 
	}
	
	/* methods */
	// a. constructor
	// b. getter and setter
	public ArrayList getTimeStamp()
	{
		return this.ts; 
	}
	
	public void setTimeStamp(ArrayList in_ts)
	{
		this.ts = in_ts; 
	}
		
}
