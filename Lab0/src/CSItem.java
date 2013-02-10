/*
 * CS Item in the CS queue
 */

import java.util.*;

public class CSItem {
	
	/* id */
	String src = "";
	TimeStamp ts = new TimeStamp(1);
	
	/* not used for now */
	TimeStampedMessage message = null;
	/* don't need to pass */
	Hashtable<String, String> replied_members = new Hashtable<String, String>();

}
