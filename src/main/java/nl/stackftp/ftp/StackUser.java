package nl.stackftp.ftp;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import nl.stackftp.webdav.WebdavClient;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.AuthorizationRequest;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.User;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

public class StackUser implements User {

    /**
     * The maximum idle time of this user.
     */
    @Value("${ftp.idleTime}")
    protected int maxIdleTime;

    /**
     * The user name.
     */
    protected String name;

    /**
     * The user password.
     */
    protected String password;

    /**
     * Get the user's name.
     *
     * @return The user's name.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Get the user's password.
     *
     * @return The user's password.
     */
    public String getPassword()
    {
        return this.password;
    }

    /**
     * The StackUser constructor.
     *
     * @param name The user's name.
     * @param password The user's password.
     */
    public StackUser(String name, String password)
    {
        this.name = name;
        this.password = password;
    }

    /**
     * Get a list of all user authorities.
     *
     * @return List of authorities.
     */
    public List<? extends Authority> getAuthorities()
    {
        return new ArrayList<>();
    }

    /**
     * Get a list of all user authorities.
     *
     * @param authority The authority.
     * @return List of authorities.
     */
    public List<? extends Authority> getAuthorities(Class<? extends Authority> authority)
    {
        return new ArrayList<>();
    }

    /**
     * Check if the user is authorized.
     *
     * @param authorizationRequest The authorizationRequest.
     * @return Return the authorizationRequest to let the user pass.
     */
    public AuthorizationRequest authorize(AuthorizationRequest authorizationRequest)
    {
        return authorizationRequest;
    }

    /**
     * Get the maximum idle time of an user.
     *
     * @return The max idle time is seconds.
     */
    public int getMaxIdleTime()
    {
        return this.maxIdleTime;
    }

    /**
     * Check if this user is enabled.
     * Always true.
     *
     * @return True when enabled.
     */
    public boolean getEnabled()
    {
        return true;
    }

    /**
     * Get the home directory of this user.
     * Always / because the user isn't accessing a real folder.
     *
     * @return The home directory.
     */
    public String getHomeDirectory()
    {
        return "/";
    }

    /**
     * Get the FileSystemView of this user.
     * @return
     */
    public FileSystemView getFileSystemView()
    {
        return new StackFileSystemView(this);
    }

    /**
     * Get a Webdav client with the credentials of this user.
     *
     * @return The Webdav client.
     */
    public WebdavClient getWebdavClient()
    {
        return new WebdavClient(this.name, this.password);
    }
}
