import java.util.ArrayList;


public abstract class TimeStamp<T>{	
	/* methods */
	// a. constructor
	// b. getter and setter
	public ArrayList getTimeStamp()
	{
		ArrayList tmp = new ArrayList();
		return tmp; 
	}
	
	public void setTimeStamp(ArrayList in_ts)
	{
		; 
	}
	
	public String toString()
	{
		String buf_string = "";
		return buf_string;
	}		
	
	
	/* - Timestamps will need to be able to be inspected and compared.  Your application code 
	needs to be able to determine from timestamps if an event “happened before” or is 
	“concurrent with” some other event.  Note carefully the limits on Logical clocks in this 
	regard.*/		
	
	public void compareTimeStamps()
	{
		/* Rough attempt at implementing the rules found in the lecture notes. 
		 * Needs to have the format of TimeStampedMessage first (as well as how
		 * we're going to store and pass in all of the TimeStampedMessage objects)
		 * before doing this coding. 
		 * */
		return;
	}
	
}
