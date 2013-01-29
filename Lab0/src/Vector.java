import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public final class Vector extends ClockService {

	/* fields */
	ArrayList<AtomicInteger> vector;

	public Vector(int num_users)
	{
		this.ts = new TimeStamp(num_users);
	}


	public void setTimestamp(TimeStamp ts, ArrayList vals){
		for(int i=0; i<this.vector.size(); i++)
			;//this.vector.set(i, 0); //probably going to have to separate this as an INIT function instead of a set function...
	}
}
