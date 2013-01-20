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
	 * 	max_size: the max numbder of Message the buffer can hold
	 */
	public MessageBuffer(int max_size) {
		this.max_size = max_size;
		this.buf = new LinkedBlockingQueue<Message>();
		this.buf_lock = new ReentrantLock();
	}
	
	/*
	 * Insert the element into the buffer
	 * Return: false if full; true upon success
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
				done = this.buf.offer(message);
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
			message = this.buf.take();
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
	
}
