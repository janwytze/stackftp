package nl.stackftp.ftp;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class StackFtpServer {

    /**
     * The application context.
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * The ftp address.
     */
    @Value("${ftp.address}")
    private String serverAddress;

    /**
     * The ftp port.
     */
    @Value("${ftp.port}")
    private int port;

    /**
     * Start the ftp server.
     *
     * @throws FtpException Thrown when server can't start.
     */
    @PostConstruct
    public void init() throws FtpException
    {
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory listenerFactory = new ListenerFactory();

        listenerFactory.setServerAddress(this.serverAddress);
        listenerFactory.setPort(this.port);
        serverFactory.addListener("default", listenerFactory.createListener());

        serverFactory.setUserManager(applicationContext.getBean(StackUserManager.class));
        serverFactory.setFileSystem(applicationContext.getBean(StackFileSystemFactory.class));

        FtpServer ftpServer = serverFactory.createServer();

        ftpServer.start();
    }
}
