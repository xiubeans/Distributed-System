
import com.jcraft.jsch.*;
import java.awt.*;
import javax.swing.*;

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
    public void connect(String host, String user) {
        JSch jsch = new JSch();
    	try {
	    	session = jsch.getSession(user, host, 22);
	        session.setConfig("StrictHostKeyChecking", "no");
	        
	        UserInfo ui=new MyUserInfo();
	        session.setUserInfo(ui);
	        
	        //session.setPassword(password);
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
    
    
    public static class MyUserInfo implements UserInfo, UIKeyboardInteractive{
        public String getPassword(){ return passwd; }
        public boolean promptYesNo(String str){
          Object[] options={ "yes", "no" };
          int foo=JOptionPane.showOptionDialog(null, 
                 str,
                 "Warning", 
                 JOptionPane.DEFAULT_OPTION, 
                 JOptionPane.WARNING_MESSAGE,
                 null, options, options[0]);
           return foo==0;
        }
     
        String passwd;
        JTextField passwordField=(JTextField)new JPasswordField(20);
      
        public String getPassphrase(){ return null; }
        public boolean promptPassphrase(String message){ return false; }
        public boolean promptPassword(String message){
          Object[] ob={passwordField}; 
          int result=
            JOptionPane.showConfirmDialog(null, ob, message,
                                          JOptionPane.OK_CANCEL_OPTION);
          if(result==JOptionPane.OK_OPTION){
            passwd=passwordField.getText();
            return true;
          }
          else{ 
            return false; 
          }
        }
        public void showMessage(String message){
          JOptionPane.showMessageDialog(null, message);
        }
     
        final GridBagConstraints gbc = 
          new GridBagConstraints(0,0,1,1,1,1,
                                 GridBagConstraints.NORTHWEST,
                                 GridBagConstraints.NONE,
                                 new Insets(0,0,0,0),0,0);
        private Container panel;
        public String[] promptKeyboardInteractive(String destination,
                                                  String name,
                                                  String instruction,
                                                  String[] prompt,
                                                  boolean[] echo){
    /*
    //System.out.println("promptKeyboardInteractive");
    System.out.println("destination: "+destination);
    System.out.println("name: "+name);
    System.out.println("instruction: "+instruction);
    System.out.println("prompt.length: "+prompt.length);
    System.out.println("prompt: "+prompt[0]);
    */
          panel = new JPanel();
          panel.setLayout(new GridBagLayout());
     
          gbc.weightx = 1.0;
          gbc.gridwidth = GridBagConstraints.REMAINDER;
          gbc.gridx = 0;
          panel.add(new JLabel(instruction), gbc);
          gbc.gridy++;
     
          gbc.gridwidth = GridBagConstraints.RELATIVE;
     
          JTextField[] texts=new JTextField[prompt.length];
          for(int i=0; i<prompt.length; i++){
            gbc.fill = GridBagConstraints.NONE;
            gbc.gridx = 0;
            gbc.weightx = 1;
            panel.add(new JLabel(prompt[i]),gbc);
     
            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weighty = 1;
            if(echo[i]){
              texts[i]=new JTextField(20);
            }
            else{
              texts[i]=new JPasswordField(20);
            }
            panel.add(texts[i], gbc);
            gbc.gridy++;
          }
     
          if(JOptionPane.showConfirmDialog(null, panel, 
                                           destination+": "+name,
                                           JOptionPane.OK_CANCEL_OPTION,
                                           JOptionPane.QUESTION_MESSAGE)
             ==JOptionPane.OK_OPTION){
            String[] response=new String[prompt.length];
            for(int i=0; i<prompt.length; i++){
              response[i]=texts[i].getText();
            }
    	return response;
          }
          else{
            return null;  // cancel
          }
        }
      }    
    
}

