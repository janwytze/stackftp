package nl.stackftp.ftp;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StackFtpServer {

    /**
     * The ftp address.
     */
    @Value("${ftp.address}")
    private String serverAddress = "127.0.0.1";

    /**
     * The ftp port.
     */
    @Value("${ftp.port}")
    private int port = 2221;

    /**
     * The ftp server.
     */
    private FtpServer ftpServer;

    /**
     * The ftp server constructor.
     * Server will be started here.
     * Because this is a service the server will start at application start.
     *
     * @throws FtpException Thrown when server can't start.
     */
    public StackFtpServer() throws FtpException
    {
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory listenerFactory = new ListenerFactory();

        listenerFactory.setServerAddress(this.serverAddress);
        listenerFactory.setPort(this.port);
        serverFactory.addListener("default", listenerFactory.createListener());

        serverFactory.setUserManager(new StackUserManager());
        serverFactory.setFileSystem(new StackFileSystemFactory());

        this.ftpServer = serverFactory.createServer();

        this.ftpServer.start();
    }
}
