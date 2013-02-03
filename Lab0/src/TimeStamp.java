import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class TimeStamp implements Serializable {	
	
	  /* Fields */
	  ArrayList<AtomicInteger> val;

	  
	  /* Constructor */
	  public TimeStamp(int num_users) 
	  {
	    AtomicInteger init_val = new AtomicInteger(0);
	    this.val = new ArrayList();

	    for (int i = 0; i < num_users; i++)
	      this.val.add(init_val);
	  }

	  
	  /* Comparison Methods */
	  
	  public boolean isEqual(TimeStamp ts) {
		  /* Determines if one timestamp is
		   * equal to another. */
		  
		  boolean is_equal = true;
		  
		  for(int i = 0; i < this.val.size(); i++) {
			  if(this.val.get(i).get() != ts.val.get(i).get()) {
				  is_equal = false;
				  break;
			  }
		  }
		  return is_equal;  
	  }
	  
	  
	  public boolean isLessOrEqual(TimeStamp ts) {
		  /* Determines if one timestamp is less than
		   * OR equal to another. */
		  
		  boolean is_less_or_equal = true;
		  
		  for(int i = 0; i < this.val.size(); i++) {
			  if(this.val.get(i).get() > ts.val.get(i).get()) {
				  is_less_or_equal = false;
				  break;
			  }
		  }
		  return is_less_or_equal;  
	  }
	  
	  
	  public boolean isLess(TimeStamp ts) {
		  /* Determines if one timestamp is less than
		   * another. */
		  
		  boolean is_less = false;
		  
		  if(this.isLessOrEqual(ts) && !this.isEqual(ts))
			  is_less = true;  
		  return is_less;
	  }
	  
	  
	  /* Miscellaneous Methods */
	  
	  public TimeStamp clone()
	  {
		  /* Create a cloned copy of the timestamp
		   * arrayList. */
		  
	    TimeStamp vts = new TimeStamp(this.val.size());

	    for (int i = 0; i < this.val.size(); i++) {
	      vts.val.set(i, (AtomicInteger)this.val.get(i));
	    }
	    return vts;
	  }
	  

	  public String toString()
	  {
	    String buf_string = "[";

	    for (int i = 0; i < this.val.size(); i++)
	    {
	    	buf_string = buf_string + ((AtomicInteger)this.val.get(i)).toString();
	    	if(i != this.val.size()-1)
	    		buf_string +=",";
	    }
	    
	    buf_string += "]";
	    return buf_string;
	    
	  }
}
