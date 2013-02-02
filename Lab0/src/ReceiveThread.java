import java.io.EOFException;
import java.io.ObjectInputStream;

/*
 * Child thread created by the Server
 * Only keep track of the input stream of a given socket, and deal with the buffer issue
 * It also maintain some connection state information, like the counter of input message number
 */
class ReceiveThread implements Runnable {
	
	/* Fields */
	String remote_name;
	MessagePasser mmp;
	
	
	/* Constructor */
	public ReceiveThread(String remote_name) {
		this.remote_name = remote_name;
		this.mmp = MessagePasser.getInstance();
	}
	
	
	public void run() {
		/* This method does the real work when this thread
		 * is created. It listens to the input stream of 
		 * the socket. The connection will never close until
		 * the application terminates. */
		
		// get the connection state of this socket at first
		ConnState conn_state = this.mmp.connections.get(this.remote_name);
		ObjectInputStream ois = conn_state.getObjectInputStream();
		
		// Infinite loop: listen for input
		try {
			try {
				while(true) {
						Message message = (Message)ois.readObject(); 
						
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