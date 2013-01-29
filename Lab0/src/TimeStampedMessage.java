import java.io.Serializable;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;


public final class TimeStampedMessage extends Message implements Serializable {
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

	  /*
	   * Compare the relationship between two messages
	   * Return:
	   * 	-1 : this happens before msg
	   * 	 0 : they are concurrent
	   *     1 : msg happens before this
	   */
	  public int compareOrder(TimeStampedMessage msg)
	  {
	    int order = 0;

	    if(this.ts.isLess(msg.ts))
	    	order = -1;
	    else if(msg.ts.isLess(this.ts))
	    	order = 1;
	    else
	    	order = 0;
	    
	    return order;
	  }
	  
	  /*
	   * Deep clone a TimeStampedMessage
	   */
	  public TimeStampedMessage clone() {
		  
		  // Attention: we don't deep clone the playload here !!!
		  TimeStampedMessage new_tsmsg = new TimeStampedMessage(
				  (TimeStamp)this.ts.clone(), this.src, this.dest, this.kind, this.payload);
		  
		  return new_tsmsg;
	  }
}
