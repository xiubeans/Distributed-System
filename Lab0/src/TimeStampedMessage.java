
public final class TimeStampedMessage extends Message {
	
	/* field */
	TimeStamp ts;

	public TimeStampedMessage(TimeStamp t_stamp, String src, String dest, String kind, Object payload)
	{		
		super(src, dest, kind, payload);
		this.ts = t_stamp;
	}
	
	/* methods */
	public void setTimestamp(TimeStamp t_stamp)
	{
		this.ts = t_stamp;
	}
	
	public TimeStamp getTimestamp()
	{
		return this.ts;
	}

}
