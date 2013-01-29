import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;


public final class TimeStampedMessage extends Message 
{
	  TimeStamp ts;

	  public TimeStampedMessage(TimeStamp t_stamp, String src, String dest, String kind, Object payload)
	  {
	    super(src, dest, kind, payload);
	    this.ts = t_stamp;
	  }

	  public String toString()
	  {
	    return this.ts.toString();
	  }

	  public boolean happenBefore(TimeStampedMessage timeStampedMessage)
	  {
	    System.out.println("In happenedBefore with timestamp value of " + timeStampedMessage.ts.toString());
	    return false;
	  }

	  public int compareOrder(TimeStampedMessage msg)
	  {
	    int order = 0;

	    return order;
	  }
}
