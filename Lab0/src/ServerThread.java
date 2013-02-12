import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


/*
 * This class will be created as a new thread, to wait for incoming connections
 */
class ServerThread implements Runnable {
	
	// get the singleton
	MessagePasser mmp;
	
	/*
	 * Constructor: just get the singleton
	 */
	public ServerThread() {
		mmp = MessagePasser.getInstance();
	}
	
	
	public void run() {
		
		// Get the configuration of local server
		int i;
		for(i = 0; i < 10; i++) {
			if(this.mmp.conf[0][i].equals(mmp.local_name))
				break;
		}
		
		// if no such local name, terminate the application
		if(i == 10) {
			System.out.println("No such name: " + mmp.local_name);
			System.exit(0);
		} 
		
		// local name found, setup the local server
		else 
			try {
				
				// Init the local listening socket
				System.out.println("I am listening on port = " + Integer.parseInt(this.mmp.conf[2][i]));
				ServerSocket socket = new ServerSocket(Integer.parseInt(this.mmp.conf[2][i]));

				// keep listening on the WELL-KNOWN port
				while(true) {
					Socket s = socket.accept();
					ObjectOutputStream oos_tmp = new ObjectOutputStream(s.getOutputStream());
					ObjectInputStream ois_tmp = new ObjectInputStream(s.getInputStream());
					
					//oos_tmp.defaultWriteObject();
					//ois_tmp.defaultReadObject();
	
					System.out.println("In ServerThread $$ about to receive LOGIN");
					Message login_msg = (Message)ois_tmp.readObject();
					String remote_name = login_msg.src;
					System.out.println("In ServerThread $$ just received LOGIN from: " + remote_name);

					ConnState conn_state = new ConnState(remote_name, s);					
					conn_state.setObjectOutputStream(oos_tmp);
					conn_state.setObjectInputStream(ois_tmp);
					// if connector is myself 
					if(remote_name.equals(this.mmp.local_name)) {
						this.mmp.self_conn = conn_state;
					}
					else {
						this.mmp.connections.put(remote_name, conn_state);	
					}
					//this.mmp.printConnectsions();
					
					// create and run the ReceiveThread
					System.out.println("In ServerThread $$ about to launch a new receive thread...");
					Runnable receiveRunnable = new ReceiveThread(remote_name);
					Thread receiveThread = new Thread(receiveRunnable);
					receiveThread.start();
					System.out.println("In ServerThread $$ just launched a new receive thread...");

									
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
	}
}
