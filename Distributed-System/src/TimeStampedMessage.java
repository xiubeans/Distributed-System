import java.io.Serializable;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;


public final class TimeStampedMessage extends Message implements Serializable {
	  TimeStamp ts;

	  public TimeStampedMessage(TimeStamp t_stamp, String src, String dest, String kind, String type, Object payload)
	  {
	    super(src, dest, kind, type, payload);
	    this.ts = t_stamp;
	  }

	  public String toString()
	  {
		String str = "Message -- src=" + this.src + " dest=" + this.dest + " kind=" + this.kind + " ts=" + this.ts;  
	    return str;
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
	   * Return true if this MC message is ideantical to msg
	   */
	  public boolean isIdentical(TimeStampedMessage msg) {
		  
		  boolean is_identical = false;
		  
		  if(this.src.equals(msg.src) &&
				  this.mc_id == msg.mc_id)
			  is_identical = true;
		  
		  return is_identical;
		  
	  }
	  
	  /*
	   * If this is an ACK, return true if this matches MC message "msg"
	   */
	  public boolean isAckMatched(TimeStampedMessage msg) {
		  
		  boolean is_matched = false;
		  
		  if(this.payload != null) {
			  String[] payload = ((String)this.payload).split("\t");
			  if(payload[0].equals(msg.src) && Integer.parseInt(payload[1]) == msg.mc_id)
				  is_matched = true;
		  }
		  
		  return is_matched;
		  
	  }
	  
	  public boolean logicalLessThan(TimeStampedMessage other_msg) {
		  
		  boolean result = false;
		  
		  if(this.ts.val.get(0).intValue() < other_msg.ts.val.get(0).intValue()
				  || ((this.ts.val.get(0).intValue() == other_msg.ts.val.get(0).intValue()) && this.src.compareTo(other_msg.src) < 0))
			  result = true;
		  		  
		  return result;
	  }
 
	  /*
	   * Deep clone a TimeStampedMessage
	   */
	  public TimeStampedMessage clone() {
		  
		  // Attention: we don't deep clone the playload here !!!
		  TimeStampedMessage new_tsmsg = new TimeStampedMessage(
				  (TimeStamp)this.ts.clone(), this.src, this.dest, this.kind, this.type, this.payload);
		  
		  return new_tsmsg;
	  }
}
