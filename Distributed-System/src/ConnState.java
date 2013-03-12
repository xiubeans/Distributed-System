import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

class ConnState {
	/* Describes the connection state with another remote end.
	 * The MessagePasser should keep a set of ConnStates. */
	
	/* Fields */
	String remote_name;	// the remote end
	Socket local_socket; // the local socket used to connect to remote end
	AtomicInteger in_messsage_counter = new AtomicInteger(0); // the number of incoming messages to the remote end
	AtomicInteger out_message_counter = new AtomicInteger(0); // the number of outgoing messages to the remote end
	HashMap special_rules = new HashMap(); //contains rule num as key and number of times seen as value
	ObjectOutputStream oos;
	ObjectInputStream ois;

	
	/* Constructor */
	public ConnState(String remote_name, Socket local_socket) {
		this.remote_name = remote_name;
		this.local_socket = local_socket;
	}
	
	
	/* Atomic Accessors and Mutators */
	
	public int getInMessageCounter() {
		return this.in_messsage_counter.get();
	}
	
	public int getAndIncrementInMessageCounter() {
		return this.in_messsage_counter.getAndIncrement();
	}
	
	public void resetInMessageCounter() {
		this.in_messsage_counter.set(0);
	}
	
	public int getOutMessageCounter() {
		return this.out_message_counter.get();
	}
	
	public int getAndIncrementOutMessageCounter() {
		return this.out_message_counter.getAndIncrement();
	}
	
	public void resetOutMessageCounter() {
		this.out_message_counter.set(0);
	}
	
	public int getTimesRuleSeen(int rule_num) {
		return (Integer) this.special_rules.get(rule_num);
	}
	
	public void setTimesRuleSeen(int rule_num, int times) {
		this.special_rules.put(rule_num, times);
	}
	
	public void setObjectOutputStream(ObjectOutputStream oos) {
		this.oos = oos; 
	}
	
	public ObjectOutputStream getObjectOutputStream() {
		return oos;
	}

	public void setObjectInputStream(ObjectInputStream ois) {
		this.ois = ois; 
	}
	
	public ObjectInputStream getObjectInputStream() {
		return ois;
	}
}