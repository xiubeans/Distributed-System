import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public final class Vector extends ClockService {

	/* fields */
	ArrayList<AtomicInteger> vector;

	public Vector()
	{
		//super("vector");
		this.ts.val.set(0);
		this.vector = new ArrayList(); //is this right?
	}

	//@Override
//	public TimeStamp getTimestamp(TimeStamp ts) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
	public void setTimestamp(TimeStamp ts, ArrayList vals){
		for(int i=0; i<this.vector.size(); i++)
			;//this.vector.set(i, 0); //probably going to have to separate this as an INIT function instead of a set function...
	}
//	
//	public void incrementTimeStamp(){
//		;
//	}
	
	public void initTimestamp(){
		
		AtomicInteger init_val = new AtomicInteger();
		init_val.set(0);
		System.out.println("Setting up vector timestamp");
		for(int i=0; i<this.vector.size(); i++)
			this.vector.set(i, init_val); //probably going to have to separate this as an INIT function instead of a set function...
	}
}
