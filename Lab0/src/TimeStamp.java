import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class TimeStamp implements Serializable {	
	
	AtomicInteger val = new AtomicInteger();
	
	protected TimeStamp(){
		this.val.set(0);
		System.out.println("In TS constructor, setting value of ts to "+this.val);
	}
	
//	public void setTimeStamp(AtomicInteger val)
//	{
//		this.val = val; 
//	}
	
	
	/* methods */
	// a. constructor
	// b. getter and setter
//	public ArrayList getTimeStamp()
//	{
//		ArrayList tmp = new ArrayList();
//		return tmp; 
//	}
//	
//	public void setTimeStamp(ArrayList in_ts)
//	{
//		; 
//	}
//	
//	public String toString()
//	{
//		String buf_string = "";
//		return buf_string;
//	}		
//	
	
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
