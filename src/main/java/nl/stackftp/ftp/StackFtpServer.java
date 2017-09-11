package nl.stackftp.ftp;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfiguration;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

@Component
public class StackFtpServer {

    /**
     * The application context.
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Is ssl enabled.
     */
    @Value("#{environment.FTP_SSL?:false}")
    private boolean enableSsl;

    /**
     * The ftp ssl keystore file.
     */
    @Value("#{environment.FTP_KEYSTORE}")
    private String sslKeystore;

    /**
     * The ftp ssl keystore password.
     */
    @Value("#{environment.FTP_KEYSTORE_PASSWORD}")
    private String sslKeystorePassword;

    /**
     * The ftp address.
     */
    @Value("#{environment.FTP_ADDRESS?:'127.0.0.1'}")
    private String serverAddress;

    /**
     * The ftp port.
     */
    @Value("#{environment.FTP_PORT?:21}")
    private int port;

    /**
     * Start the ftp server.
     *
     * @throws FtpException Thrown when server can't start.
     */
    @PostConstruct
    public void init() throws FtpException {
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory listenerFactory = new ListenerFactory();

        // Enable ssl when configured.
        if (this.enableSsl) {
            listenerFactory.setSslConfiguration(this.getSslConfiguration());
            listenerFactory.setImplicitSsl(true);
        }

        listenerFactory.setServerAddress(this.serverAddress);
        listenerFactory.setPort(this.port);
        serverFactory.addListener("default", listenerFactory.createListener());

        serverFactory.setUserManager(this.applicationContext.getBean(StackUserManager.class));
        serverFactory.setFileSystem(this.applicationContext.getBean(StackFileSystemFactory.class));

        FtpServer ftpServer = serverFactory.createServer();

        ftpServer.start();
    }

    /**
     * Load the SslConfiguration for the application properties.
     *
     * @return The SslConfiguration
     */
    protected SslConfiguration getSslConfiguration() {
        SslConfigurationFactory sslConfigurationFactory = new SslConfigurationFactory();
        sslConfigurationFactory.setKeystoreFile(new File(this.sslKeystore));
        sslConfigurationFactory.setKeystorePassword(this.sslKeystorePassword);

        return sslConfigurationFactory.createSslConfiguration();
    }
}
