import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;


public final class VectorTimeStamp extends TimeStamp<ArrayList>  {
	
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
	
	public String toString()
	{
		String buf_string = "";
		
		// print all messages in the buffer
		for(int i=0; i<this.ts.size(); i++)
			buf_string += "\t" + this.ts.get(i).toString() + "\n";
		return buf_string;
	}		
}
