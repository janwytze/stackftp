package nl.stackftp.configuration;

import nl.stackftp.ftp.StackFileSystemFactory;
import nl.stackftp.ftp.StackUserManager;
import nl.stackftp.ftp.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
