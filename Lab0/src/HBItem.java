import java.util.*;

/*
 * The item stored in HBQ(HoldBackQueue) for multi-casting messages
 */
public class HBItem {
	
	String src;
	int mc_id;
	TimeStamp ts;
	TimeStampedMessage message = null;
	ArrayList<Boolean> ack_list;
	ArrayList<Integer> flags = new ArrayList<Integer>();
	long timestamp;
	long wait_interval = 30000 + ((new Random()).nextLong() % 2000);
	
	MessagePasser mp;
	
	public HBItem(TimeStampedMessage msg) {
		
		this.mp = MessagePasser.getInstance();
		
		this.ack_list = new ArrayList<Boolean>();
		for(int i = 0; i < this.mp.num_nodes; i++) {
			this.ack_list.add(false);
		}
		
		for(int i = 0; i < 5; i++)
			this.flags.add(0);
		
		this.timestamp = System.currentTimeMillis();
		
		/* get an original multicast message */
		if(msg.type.equals("multicast") && !msg.kind.equals("ack")) {
			this.message = msg;
			this.src = msg.src;
			this.mc_id = msg.mc_id;
			this.ts = msg.ts;
			
			this.tryAcceptAck(msg);
		}
		
		/* get an ACK */
		else if(msg.type.equals("multicast") && msg.kind.equals("ack")) {			
			String[] payload = ((String)msg.payload).split("\t");
			this.src = payload[0];
			this.mc_id = Integer.parseInt(payload[1]);
			this.ts = ClockService.getInstance("vector", this.mp.num_nodes).parseVTS(payload[2]);
			
			this.tryAcceptAck(msg);
		}
		
		/* get a retransmitted multicast message by someone else */
		else if(msg.type.equals("unicast") && msg.kind.equals("retransmit")) {
			this.message = (TimeStampedMessage)msg.payload;
			this.src = this.message.src;
			this.mc_id = this.message.mc_id;
			this.ts = this.message.ts;
			
			this.tryAcceptAck(msg);
		}
		
		/* else: the default case */
		else
			return;
		
//		
//		this.src = src;
//		this.seq_num = seq_num;
//		this.ts = ts;
//		
//		this.mp = MessagePasser.getInstance();
//		this.timestamp = System.currentTimeMillis();
//		
//		/* init ack_list, by given list length */
//		this.ack_list = new ArrayList<Boolean>();
//		for(int i = 0; i < this.mp.num_nodes; i++) 
//			this.ack_list.add(false);
//		this.ack_list.set(this.mp.names_index.get(src), true);
//		this.ack_list.set(this.mp.names_index.get(this.mp.local_name), true);
//		
//		/* init list of flags with arbitrary length */
//		for(int i = 0; i < 5; i++)
//			this.flags.add(0);
		
	}
	
	public void setMessage(TimeStampedMessage msg) {
		this.message = msg;
	}
	
	public void setAckBit(TimeStampedMessage ack_msg) {
		String ack_from = ack_msg.getOrigSrc();
		int index = this.mp.names_index.get(ack_from);
		this.ack_list.set(index, true);
	}
	
	/*
	 * Determine whether this message is ready based on seq# and acked nodes
	 */
	public boolean isReady() {
		
		boolean is_ready = true;
		
		/* check if it is acked by everyone */
		for(int i = 0; i < ack_list.size(); i++)
			if(ack_list.get(i).booleanValue() == false) {
				is_ready = false;
				break;
			}
		
		/* check if it is the next MC message in the sequence */
		if(this.mc_id != this.mp.mc_seqs.get(this.mp.names_index.get(this.src)) + 1)
			is_ready = false;
		
		return is_ready;
		
	}
	
	/*
	 * Ack something
	 */
	public void tryAcceptAck(TimeStampedMessage msg) {
		
		/* get an original multicast message */
		if(msg.type.equals("multicast") && !msg.kind.equals("ack")) {			
			if(this.message.isIdentical(msg)) {
				int index = this.mp.names_index.get(msg.src);
				this.ack_list.set(index, true);
			}			
		}
		
		/* get an multicast ack */
		else if(msg.type.equals("multicast") && msg.kind.equals("ack")) {
			String[] payload = ((String)msg.payload).split("\t");
			if(this.src.equals(payload[0]) && this.mc_id == Integer.parseInt(payload[1])) {
				int index = this.mp.names_index.get(this.src);
				this.ack_list.set(index, true);
			}			
		}
		
		/* get a retransmitted multicast message by someone else */
		else if(msg.type.equals("unicast") && msg.kind.equals("retransmit")) {			
			message = (TimeStampedMessage)msg.payload;
			if(this.message.src.equals(message.src) && this.message.mc_id == message.mc_id) {
				int index = this.mp.names_index.get(this.src);
				this.ack_list.set(index, true);
			}			
		}
		
		/* else: the default case */
		else
			return;
		
	}
	
	/*
	 * Determine whether time-out for resend
	 * and will update timestamp if timeout
	 */
	public boolean isTimeOut() {
		
		boolean is_timeout = false;
		
		long now = System.currentTimeMillis();
		if(this.timestamp + this.wait_interval > now) {
			is_timeout = true;
			timestamp = now;
		}
		
		return is_timeout;
		
	}
	  
	/*
	 * Return the nodes that need retransmission of the original MC message
	 */
	public ArrayList<String> nodesNeedResend() {
		  
		ArrayList<String> nodes = new ArrayList<String>();
		  
		for(int i = 0 ; i < this.ack_list.size(); i++) {
			if(this.ack_list.get(i) == false) {
				nodes.add(this.mp.getName(i));
			}
		}
		  
		return nodes;	  
	}
	
	  /*
	   * Compare the relationship between two messages
	   * Return:
	   * 	-1 : this happens before msg
	   * 	 0 : they are concurrent
	   *     1 : msg happens before this
	   */
	  public int compareOrder(HBItem hbi)
	  {
	    int order = 0;

	    if(this.ts.isLess(hbi.ts))
	    	order = -1;
	    else if(hbi.ts.isLess(this.ts))
	    	order = 1;
	    else
	    	order = 0;
	    
	    return order;
	  }

	  public String toString() {
		  
		  String print = "";
		  
		  print += "src=" + this.src;
		  print += ", mc_id=" + this.mc_id;
		  print += ", timestamp=" + this.ts.toString();
		  print += ", type=" + this.message.type;
		  print += ", kind=" + this.message.kind;
		  print += "\n\t\t\t" + "Acked list:";
		  for(int i = 0; i < this.ack_list.size(); i++) {
			  String name = this.mp.getName(i);
			  if(this.ack_list.get(i).booleanValue() == true)
				  print += name + "=true ";
			  else
				  print += name + "=false ";
		  }
		  
		  return print;
		  
	  }
	
}
