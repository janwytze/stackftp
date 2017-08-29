package nl.stackftp.ftp;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfiguration;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class StackFtpServer {

    /**
     * The ftp port.
     */
    @Value("${ftp.port}")
    private int port;

    /**
     * Is ssl enabled.
     */
    @Value("${ftp.ssl}")
    private boolean enableSsl = false;

    /**
     * The ftp ssl keystore file.
     */
    @Value("${ftp.ssl.keystore}")
    private String sslKeystore;

    /**
     * The ftp ssl keystore password.
     */
    @Value("${ftp.ssl.keystore.password}")
    private String sslKeystorePassword;

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
        System.out.println(this.port);
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory listenerFactory = new ListenerFactory();

        // Enable ssl when configured.
        if (this.enableSsl) {
            listenerFactory.setSslConfiguration(this.getSslConfiguration());
            listenerFactory.setImplicitSsl(true);
        }

        listenerFactory.setPort(this.port);
        serverFactory.addListener("default", listenerFactory.createListener());

        serverFactory.setUserManager(new StackUserManager());
        serverFactory.setFileSystem(new StackFileSystemFactory());

        this.ftpServer = serverFactory.createServer();

        this.ftpServer.start();
    }

    /**
     * Load the SslConfiguration for the application properties.
     *
     * @return The SslConfiguration
     */
    protected SslConfiguration getSslConfiguration()
    {
        SslConfigurationFactory sslConfigurationFactory = new SslConfigurationFactory();
        sslConfigurationFactory.setKeystoreFile(new File(this.sslKeystore));
        sslConfigurationFactory.setKeystorePassword(this.sslKeystorePassword);

        return sslConfigurationFactory.createSslConfiguration();
    }
}
