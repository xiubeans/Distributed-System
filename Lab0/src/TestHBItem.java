
public class TestHBItem {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
		TimeStampedMessage msg = new TimeStampedMessage(null, "alice", "bob", "multicast", "init", null);
		msg.type = "multicast";
		msg.kind = "ack";
		
		HBItem hbi = new HBItem(msg);
		if(hbi.message == null)
			System.out.println("Shit!");
		
		System.out.println(hbi.toString());

		Thread.sleep(1000);
		
		System.out.println(hbi.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}

}
