import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class TimeStamp implements Serializable {	
	
	  ArrayList<AtomicInteger> val;

	  public TimeStamp(int num_users)
	  {
	    AtomicInteger init_val = new AtomicInteger(0);
	    this.val = new ArrayList();

	    System.out.println("Size of arraylist is " + this.val.size());
	    for (int i = 0; i < num_users; i++)
	      this.val.add(init_val);
	    System.out.println("Size of arraylist is " + this.val.size());
	  }

	  public TimeStamp clone()
	  {
	    TimeStamp vts = new TimeStamp(this.val.size());

	    for (int i = 0; i < this.val.size(); i++) {
	      vts.val.add((AtomicInteger)this.val.get(i));
	    }
	    return vts;
	  }

	  public void set(ArrayList<AtomicInteger> vector_vals)
	  {
	    System.out.println("Need to do comparisons here");
	    for (int i = 0; i < this.val.size(); i++)
	      this.val.set(i, (AtomicInteger)vector_vals.get(i));
	  }

	  public boolean isEqual(TimeStamp ts)
	  {
	    boolean equal = true;

	    for (int i = 0; i < this.val.size(); i++)
	    {
	      if (((AtomicInteger)this.val.get(i)).get() != ((AtomicInteger)ts.val.get(i)).get())
	      {
	        equal = false;
	        break;
	      }

	    }

	    return equal;
	  }

	  public boolean isLessOrEqual(TimeStamp ts)
	  {
	    boolean less_or_equal = true;

	    for (int i = 0; i < this.val.size(); i++)
	    {
	      if (((AtomicInteger)this.val.get(i)).get() > ((AtomicInteger)ts.val.get(i)).get())
	      {
	        less_or_equal = false;
	        break;
	      }

	    }

	    return less_or_equal;
	  }

	  public boolean isLess(TimeStamp ts)
	  {
	    if ((isLessOrEqual(ts)) && (!isEqual(ts))) {
	      return true;
	    }
	    return false;
	  }

	  public String toString()
	  {
	    String buf_string = "";

	    for (int i = 0; i < this.val.size(); i++)
	      buf_string = buf_string + "\t" + ((AtomicInteger)this.val.get(i)).toString();
	    return buf_string;
	  }
}
