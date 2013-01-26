import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public final class Logical extends ClockService {

	/* fields */
	AtomicInteger timestamp;

	protected Logical()
	{
		this.timestamp = new AtomicInteger(); //is this right?
	}
	
	@Override
	public TimeStamp getTimestamp(TimeStamp ts) {		
		return null;
	}

	/* methods */
	// a. the constructor
	// b. implement abstract method getTimeStamp(TimeStampedMessage tsmsg)
	
}
