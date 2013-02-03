import java.io.EOFException;
import java.io.ObjectInputStream;

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
		
		// get the connection state of this socket at first
		ConnState conn_state = this.mmp.connections.get(this.remote_name);
		ObjectInputStream ois = conn_state.getObjectInputStream();
		
		// Infinite loop: listen for input
		try {
			
			try {
				while(true) {
		
					TimeStampedMessage message = (TimeStampedMessage)ois.readObject(); 
						
					/* get a multicast message */
					if(message.type.equals("multicast") && !message.kind.equals("ack")) {
						if(!this.mmp.isUsefulMessage(message)) {
							continue;
						}
						else {
							if(!this.mmp.isInHBQ(message))
								this.mmp.insertToHBQ(new HBItem(message));
							else {
								HBItem this_item = this.mmp.getHBItem(message.src, message.mc_id);
								if(this_item.message == null)
									this_item.message = message;
							}
							this.mmp.tryAcceptAck(message);
							this.mmp.mcAck(message);
						}
					}
						
					/* get a ACK message */
					else if(message.type.equals("multicast") && message.kind.equals("ack")) {							
						if(!this.mmp.isUsefulMessage(message)) {
							continue;
						}
						else {
							if(!this.mmp.isInHBQ(message))
								this.mmp.insertToHBQ(new HBItem(message));
							this.mmp.tryAcceptAck(message);
						}
					}
						
					/* get a retransmit kind message */
					else if(message.kind.equals("retransmit")) {
						TimeStampedMessage msg = (TimeStampedMessage)message.payload;
						if(!this.mmp.isUsefulMessage(msg)) {
							continue;
						}
						else {
							if(!this.mmp.isInHBQ(msg))
								this.mmp.insertToHBQ(new HBItem(msg));
							else {
								HBItem this_item = this.mmp.getHBItem(msg.src, msg.mc_id);
								if(this_item.message == null)
									this_item.message = msg;
							}
							this.mmp.tryAcceptAck(message);
							this.mmp.tryAcceptAck(msg);
							this.mmp.mcAck(message);
						}
					}
						
					/* get a regular message */
					// put it into the MessagePasser's rcv_buf
					// drop the message if the buffer is full
					if(!this.mmp.rcv_buf.nonblockingOffer(message)) {
						continue;
					}
				}
			} finally {
				
				conn_state.getObjectInputStream().close();
				conn_state.getObjectOutputStream().close();
				conn_state.local_socket.close();
				this.mmp.connections.remove(remote_name);
			}
		} catch (Exception e){
			if(e instanceof EOFException) {
				System.out.println("Connection to " + remote_name + " is disconnected");
			}
			return;
		}
	}
}
