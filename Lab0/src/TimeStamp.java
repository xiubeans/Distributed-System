import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class TimeStamp implements Serializable {	
	
	  ArrayList<AtomicInteger> val;

	  public TimeStamp(int num_users)
	  {
	    AtomicInteger init_val = new AtomicInteger(0);
	    this.val = new ArrayList();

	    for (int i = 0; i < num_users; i++)
	      this.val.add(init_val);
	  }

	  
	  public TimeStamp clone()
	  {
	    TimeStamp vts = new TimeStamp(this.val.size());

	    for (int i = 0; i < this.val.size(); i++) {
	      vts.val.set(i, (AtomicInteger)this.val.get(i));
	    }
	    return vts;
	  }
	  
//	  
//	  public void set(ArrayList<AtomicInteger> vector_vals)
//	  {
//	    System.out.println("Need to do comparisons here");
//	    for (int i = 0; i < this.val.size(); i++)
//	    {
//	    	//if an element is less than the message's element, update it
//	    	if(this.val.get(i) <= )
//	    	this.val.set(i, (AtomicInteger)vector_vals.get(i));	
//	    }
//	  }

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
	  
	  /*
	   * The following three methods are added by Jaz
	   * They only apply to Vector timestamp
	   */
	  public boolean isEqual(TimeStamp ts) {
		  
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
		  
		  boolean is_less = false;
		  
		  if(this.isLessOrEqual(ts) && !this.isEqual(ts))
			  is_less = true;
		  
		  return is_less;
		  
	  }
	  
}
