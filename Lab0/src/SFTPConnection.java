
import com.jcraft.jsch.*;

/*
 * This is a wrapper class based on JSch library
 * After specifying User, PWD, HOST, CONFIGFILE, and LOCALFILE in CONSTANTS.java, we can call any of the six interfaces in SFTPConnection
 * In our program, we will call getMTime() at first to check out the last modified time of this file before downloading it
 */
public class SFTPConnection {
	static Session session = null;
	
    /**
     * Initialize an SFTP session to the server
     */
    public void connect(String host, String user, String password) {
        JSch jsch = new JSch();
    	try {
	    	session = jsch.getSession(user, host, 22);
	        session.setConfig("StrictHostKeyChecking", "no");
	        session.setPassword(password);
	        session.connect();        	
    	} catch (JSchException e) {
            e.printStackTrace();  
        } 
    }

    /**
     * Determine if the session is still alive
     * @return
     */
    public boolean isConnected() {
    	return session.isConnected();
    }
    
    /**
     * Returns last modification time of a remote file.
     */
    public int getLastModificationTime(String remote_path) {
    	int time = 0;
        try{
	        ChannelSftp chan = (ChannelSftp) session.openChannel("sftp");
	        chan.connect();
	        SftpATTRS attrs = chan.lstat(remote_path);
	        time = attrs.getMTime();
	        chan.disconnect();
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        }
        return time;
    }
    
    /**
     * Download the file
     */
    public void downloadFile(String remote_path, String local_path) {
    	ChannelSftp sftpChannel = null;
        try {
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
            sftpChannel.get(remote_path, local_path);
            sftpChannel.disconnect();
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    /**
     * Disconnect the session
     */
    public void disconnect() {
    	session.disconnect();
    }
    
}

