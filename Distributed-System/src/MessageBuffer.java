import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.*;

/*
 * The class of either send buffer or receive buffer
 * Methods include blocking/non-blocking models
 * Methods may not include all helper functions
 */
public class MessageBuffer {
	int max_size;				// the max number of Message the buffer can hold
	BlockingQueue<Message> buf;	// the buffer reference
	ReentrantLock buf_lock; 	// for concurrency
	
	/*
	 * Constructor
	 * Parameter:
	 * 	max_size: the max number of Message the buffer can hold
	 */
	public MessageBuffer(int max_size) {
		this.max_size = max_size;
		this.buf = new LinkedBlockingQueue<Message>();
		this.buf_lock = new ReentrantLock();
	}
	
	/*
	 * Insert the element at the tail of the buffer
	 * Return: false if full; true if success
	 */
	public boolean nonblockingOffer(Message message) {
		boolean done = true;
		
		// lock the buffer
		this.buf_lock.lock();
		try {
			// return false when hitting the upper bound
			if(this.max_size == this.buf.size())
				done = false;
			// return true if success; otherwise return false
			else 
				done = this.buf.offer((TimeStampedMessage)message);
		} finally {
			// unlock the buffer
			this.buf_lock.unlock();
		}
		
		return done;
	}
	
	/*
	 * Insert the elements at the head of the buffer
	 * Return: false if full; true if success
	 */
	public boolean nonblockingOfferAtHead(Message message) {
		
		boolean done = true;
		
		// lock the buffer
		this.buf_lock.lock();
		try {
			
			// return false when hitting the upper bound
			if(this.max_size == this.buf.size())
				done = false;
			// return true if success; otherwise return false
			else {
				
				// dump all existing messages into the arraylist
				ArrayList<Message> existing_messages = new ArrayList<Message>();;				
				this.buf.drainTo(existing_messages);
				
				// add the message to the head of the list, and put all the list back to the buffer
				existing_messages.add(0, (TimeStampedMessage)message);
				done = this.buf.addAll(existing_messages);
				
			}
		} finally {
			// unlock the buffer
			this.buf_lock.unlock();
		}
		
		return done;
		
	}
	
	/*
	 * Retrieve single message from the head of the buffer
	 * Get blocked when buffer is empty
	 */
	public Message blockingTake() {
		Message message = null;
		
		// get blocked here until the buffer is not empty
		try {
			message = (TimeStampedMessage)this.buf.take();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return message;
	}
	
	/*
	 * Retrieve all Messages in the buffer
	 * Non-blocking model
	 */
	public ArrayList<Message> nonblockingTakeAll() {
		ArrayList<Message> messages = new ArrayList<Message>();
		
		// System.out.println("Sys Now: " + this.buf.size());

		// lock the buffer 
		this.buf_lock.lock();
		try {
			// retrieve all messages in the buffer
			while(this.buf.size() != 0) 
				messages.add(this.buf.poll());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// unlock the buffer
			this.buf_lock.unlock();
		}
				
		return messages;
	}
	
	/*
	 * Retrieve single message from the head of the buffer
	 * Non-blocking model
	 */
	public Message nonblockingTake() {
		// add here, probably
		return null;
	}
	
	/*
	 * Return the current number of objects in the buffer
	 */
	public int size() {
		return this.buf.size();
	}
	
	@Override
	public String toString() {
		
		String buf_string = "--> The message buffer has " + this.size() + " messages:\n";
		
		Iterator<Message> itr = this.buf.iterator();
		// print all messages in the buffer
		while(itr.hasNext()) {
			Message one_msg = (Message)itr.next();
			buf_string += "\t" + one_msg.toString() + "\n";
		}
		
		return buf_string;
	}
	
	
	public void print() {
		System.out.print(this.toString());
	}
	
}
