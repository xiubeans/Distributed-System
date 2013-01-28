import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public final class Logical extends ClockService {

	/* fields */
	AtomicInteger timestamp_val;

	protected Logical()
	{
		//super("logical");
		this.ts.val.set(0);
		//this.timestamp_val = new AtomicInteger();
	}
	
//	@Override
//	public TimeStamp getTimestamp(TimeStamp ts) {		
//		return null;
//	}

//	public void setTimestamp(TimeStamp ts, AtomicInteger val){
//		this.ts.val.incrementAndGet();
////		this.timestamp = val;
//	}
//	
//	public void incrementTimeStamp(){
//		;
//	}
	
	public void initTimestamp(){
		
		AtomicInteger init_val = new AtomicInteger();
		init_val.set(0);
		System.out.println("Setting up logical timestamp");
		//this.timestamp_val = init_val;
	}
}
