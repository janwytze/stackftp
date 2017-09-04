package nl.stackftp.configuration;

import nl.stackftp.ftp.StackFileSystemFactory;
import nl.stackftp.ftp.StackUser;
import nl.stackftp.ftp.StackUserManager;
import nl.stackftp.ftp.UserService;
import org.apache.ftpserver.ftplet.FtpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AppConfiguration {

    /**
     * Create a spring managed instance of StackUserManager.
     *
     * @return A StackUserManager bean.
     */
    @Bean
    @Autowired
    public StackUserManager stackUserManager(UserService userService)
    {
        return new StackUserManager(userService);
    }

    /**
     * Create a spring managed instance of StackFileSystemFactory.
     *
     * @return A stackFileSystemFactory bean.
     */
    @Bean
    public StackFileSystemFactory stackFileSystemFactory()
    {
        return new StackFileSystemFactory();
    }

    @Bean
    @Scope("prototype")
    public StackUser stackUser(String username, String password) throws FtpException
    {
        return new StackUser(username, password);
    }
}
