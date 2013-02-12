import java.io.EOFException;
import java.io.ObjectInputStream;
import java.util.*;

/*
 * Child thread created by the Server
 * Only keep track of the input stream of a given socket, and deal with the buffer issue
 * It also maintain some connection state information, like the counter of input message number
 */
class ReceiveThread implements Runnable {
	String remote_name;
	MessagePasser mmp;
	
	/*
	 * Constructor
	 */
	public ReceiveThread(String remote_name) {

		this.remote_name = remote_name;
		this.mmp = MessagePasser.getInstance();
		
	}
	
	/*
	 * This method do the real work when this thread is created
	 * It listens to the input stream of the socket
	 * The connection will never close until the application terminates
	 */
	public void run() {
		
		/* initialize this thread */
		ObjectInputStream ois;
		ConnState conn_state;
		if(this.remote_name.equals(this.mmp.local_name)) {
			conn_state = this.mmp.self_conn;
			ois = conn_state.getObjectInputStream();
		}
		else {
			// get the connection state of this socket at first
			conn_state = this.mmp.connections.get(this.remote_name);
			ois = conn_state.getObjectInputStream();
			boolean stored = true; //must be true by default to catch ACK of non-new messages			
		}
		
//		System.out.println("In ReceiveThread $$ I am in connection with " + this.remote_name + 
//				" Thread ID: " + Thread.currentThread().getId());
		
		// Infinite loop: listen for input
		try {
			try {
				while(true) {
					
					/* get the message from socket */
					TimeStampedMessage message = (TimeStampedMessage)ois.readObject(); 
						
					/* get the rule */
					HashMap rule = null;
					rule = mmp.matchRules("receive", message);
					rule = mmp.checkNth(rule, "receive", message);
					
					if(rule != null) {
						
						String action = rule.get("action").toString();
						
						/* case 1: duplicate
						 * don't duplicate; just handle it as regular message
						 *  */
						if(action.equals("duplicate")) {
													
							/* step 1: handle this message */
							System.out.println("***********************************************************");
							System.out.println("Receive Thread: in receive(): src: " + message.src + " dest: " + message.dest);
							System.out.println(" ID: " + message.id + " kind: " + message.kind + " type: " + message.type + 
											   " timestamp: " + ((TimeStampedMessage)message).ts.toString());
							System.out.println("rule: duplicate, but just ignore this rule");
							System.out.println("***********************************************************");
							
							this.mmp.handleCSMessage(message);
							
							/* step 2: handle all delayed messages in the rcv_delayed_buf */
							ArrayList<Message> delayed_messages = mmp.rcv_delayed_buf.nonblockingTakeAll();
							while(!delayed_messages.isEmpty()) {							
								TimeStampedMessage dl_message = (TimeStampedMessage)delayed_messages.remove(0);	
								System.out.println("***********************************************************");
								System.out.println("Receive Thread: in receive(): src: " + dl_message.src + " dest: " + dl_message.dest);
								System.out.println(" ID: " + message.id + " kind: " + dl_message.kind + " type: " + dl_message.type + 
												   " timestamp: " + ((TimeStampedMessage)dl_message).ts.toString());
								System.out.println("rule: delayed message got released");
								System.out.println("***********************************************************");	
								
								mmp.handleCSMessage(dl_message);
							}
							
						}
						
						/* case 2: delay 
						 * put it into rcv_delayed_buf
						 * */
						else if(action.equals("delay")) {
							/* step 1: handle this message */
							System.out.println("***********************************************************");
							System.out.println("Receive Thread: in receive(): src: " + message.src + " dest: " + message.dest);
							System.out.println(" ID: " + message.id + " kind: " + message.kind + " type: " + message.type + 
											   " timestamp: " + ((TimeStampedMessage)message).ts.toString());
							System.out.println("rule: delay");
							System.out.println("***********************************************************");	
							
							this.mmp.rcv_delayed_buf.nonblockingOffer(message);
						}
						else
							;
						
					}
					
					/* get a regular message */
					else {
						
						/* step 1: handle this message */
						System.out.println("***********************************************************");
						System.out.println("Receive Thread: in receive(): src: " + message.src + " dest: " + message.dest);
						System.out.println(" ID: " + message.id + " kind: " + message.kind + " type: " + message.type + 
										   " timestamp: " + ((TimeStampedMessage)message).ts.toString());
						System.out.println("rule: N/A");
						System.out.println("***********************************************************");				
						this.mmp.handleCSMessage(message);
						
						/* step 2: handle all delayed messages in the rcv_delayed_buf */
						ArrayList<Message> delayed_messages = mmp.rcv_delayed_buf.nonblockingTakeAll();
						while(!delayed_messages.isEmpty()) {							
							TimeStampedMessage dl_message = (TimeStampedMessage)delayed_messages.remove(0);	
							System.out.println("***********************************************************");
							System.out.println("Receive Thread: in receive(): src: " + dl_message.src + " dest: " + dl_message.dest);
							System.out.println(" ID: " + message.id + " kind: " + dl_message.kind + " type: " + dl_message.type + 
											   " timestamp: " + ((TimeStampedMessage)dl_message).ts.toString());
							System.out.println("rule: delayed message got released");
							System.out.println("***********************************************************");	
							mmp.handleCSMessage(dl_message);
						}
					}
					
					/* print out rcv_delayed_buf */
					this.mmp.printCSReceiveBuffer();
					
				}
			} finally {
				
				conn_state.getObjectInputStream().close();
				conn_state.getObjectOutputStream().close();
				conn_state.local_socket.close();
				this.mmp.connections.remove(remote_name);
				//this.mmp.globalLock.unlock();
			}
		} catch (Exception e){
			//this.mmp.globalLock.unlock();
			if(e instanceof EOFException) {
				System.out.println("Connection to " + remote_name + " is disconnected");
			}
			System.out.println("Exception is "+e.toString());
			e.printStackTrace();
			return;
		}
	}
}
