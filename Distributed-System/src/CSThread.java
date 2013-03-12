/*
 * The class dealing with requesting the CS
 */
public class CSThread implements Runnable{

	MessagePasser mp;
	
	public CSThread() {
		this.mp = MessagePasser.getInstance();
	}
	
	/*
	 * Launch this thread after user says "request cs"
	 * Spin until get replies from all others in the CS group 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		
		try {
						
			// TEST
			//System.out.println("In CSThread $$ just come in");
			
			/* clear all replied members */
			mp.replied_members.clear();
			
			/* set the state */
			mp.state = "wanted";				
		
			/* multicast the cs request */
			mp.multicastCSRequest();
			
			/* spin here until get all replies */
			while(mp.group_members.size() != mp.replied_members.size()) {
				Thread.sleep(300);
				//mp.printCSSendBuffer();
				//mp.printRepliedNodes();

			}
			
			/* change state */
			mp.state = "held";
			
			//System.out.println("In CSThread $$ got it, about to terminate thread. mp.state=" + mp.state);

			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
