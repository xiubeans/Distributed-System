import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public final class Logical extends ClockService {

	/* fields */
	AtomicInteger timestamp_val;

	protected Logical()
	{
		this.ts = new TimeStamp(1);
	}
	
	
	public void initTimestamp(){
		
		AtomicInteger init_val = new AtomicInteger();
		init_val.set(0);
		System.out.println("Setting up logical timestamp");
	}
}
