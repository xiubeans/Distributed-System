import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public final class Vector extends ClockService {

	/* fields */
	ArrayList<AtomicInteger> vector;

	public Vector()
	{
		super("vector");
		this.vector = new ArrayList(); //is this right?
	}

	@Override
	public TimeStamp getTimestamp(TimeStamp ts) {
		// TODO Auto-generated method stub
		return null;
	}

	/* methods */
	// a. the constructor
	// b. implement abstract method getTimeStamp(TimeStampedMessage tsmsg)
}
