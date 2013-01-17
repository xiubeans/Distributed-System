import com.jcraft.jsch.*;

/*
 * This is a class for testing JSch lib
 * After specifying User, PWD and the file, it will download the file
 * In our program, we will call getMTime() at first to check out the last modified time of this file before downloading it
 */
public class JschTest {
    public static void main(String args[]) {
        JSch jsch = new JSch();
        Session session = null;
        try {
        	// Set the Andrew ID
            session = jsch.getSession("zhechen", "unix.andrew.cmu.edu", 22);
            session.setConfig("StrictHostKeyChecking", "no");
            // Set the Andrew password in clear text; it must have non-clear version, and I will look into that...
            session.setPassword("");
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            // Specify the file
            sftpChannel.get("/afs/andrew.cmu.edu/usr3/zhechen/private/F11/15415/proj1/format.c", "format.c");
            sftpChannel.exit();
            session.disconnect();
        } catch (JSchException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }
}