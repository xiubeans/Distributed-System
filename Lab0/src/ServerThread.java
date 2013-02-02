import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


class ServerThread implements Runnable {
	/* Created as a new thread to wait for incoming connections. */
	
	// get the singleton
	MessagePasser mmp;
	
	/* Constructor: just get the singleton */
	public ServerThread() {
		mmp = MessagePasser.getInstance();
	}
	
	
	public void run() {
		/* Gets the configuration of the local server. */
		int i;
		for(i = 0; i < 10; i++) {
			if(this.mmp.conf[0][i].equals(mmp.local_name))
				break;
		}
		
		// if no such local name, terminate the application
		if(i == this.mmp.max_vals) {
			System.out.println("No such name: " + mmp.local_name);
			System.exit(0);
		} 
		else //local name found, setup the local server 
			try {
				
				// Init the local listening socket
				ServerSocket socket = new ServerSocket(Integer.parseInt(this.mmp.conf[2][i]));

				// keep listening on the WELL-KNOWN port
				while(true) {
					Socket s = socket.accept();
					ObjectOutputStream oos_tmp = new ObjectOutputStream(s.getOutputStream());
					ObjectInputStream ois_tmp = new ObjectInputStream(s.getInputStream());
	
					Message login_msg = (Message)ois_tmp.readObject();
					String remote_name = login_msg.src;

					// Put the new socket into mmp's connections
					ConnState conn_state = new ConnState(remote_name, s);					
					conn_state.setObjectOutputStream(oos_tmp);
					conn_state.setObjectInputStream(ois_tmp);
					
					this.mmp.connections.put(remote_name, conn_state);					
					//this.mmp.printConnections();
					
					// create and run the ReceiveThread
					Runnable receiveRunnable = new ReceiveThread(remote_name);
					Thread receiveThread = new Thread(receiveRunnable);
					receiveThread.start();				
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
	}
}