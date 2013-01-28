import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;


public final class TimeStampedMessage extends Message {
	
	/* field */
	TimeStamp ts;

	public TimeStampedMessage(TimeStamp t_stamp, String src, String dest, String kind, Object payload)
	{		
		super(src, dest, kind, payload);
		this.ts = t_stamp;
	}
	
//	/* methods */
//	public void setTimestamp(AtomicInteger val)
//	{
//		this.ts.val = val;
//	}
	
//	public TimeStamp getTimestamp()
//	{
//		return this.ts;
//	}

	public String toString() 
	{
		return this.ts.toString();
	}

	
	public boolean happenBefore(TimeStampedMessage timeStampedMessage) {
		// TODO Auto-generated method stub
		System.out.println("In happenedBefore with timestamp value of "+timeStampedMessage.ts.toString());
		return false;
	}
	
}
