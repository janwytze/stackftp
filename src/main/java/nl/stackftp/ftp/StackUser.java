package nl.stackftp.ftp;

import nl.stackftp.webdav.WebdavClient;
import org.apache.ftpserver.ftplet.*;
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
     * The Webdav url.
     */
    protected String url;

    /**
     * The user name.
     */
    protected String name;

    /**
     * The user password.
     */
    protected String password;

    /**
     * The user's Webdav client.
     */
    protected WebdavClient webdavClient;

    /**
     * The StackUser constructor.
     *
     * @param name The user name. Also contains url.
     * @param password The user's password.
     * @throws FtpException Thrown when the name isn't valid.
     */
    public StackUser(String name, String password) throws FtpException
    {
        if (!this.checkName(name)) {
            throw new FtpException("Name not correct");
        }

        int separatorIndex = name.lastIndexOf('@');

        this.name = name.substring(0, separatorIndex);
        this.url = name.substring((separatorIndex + 1), name.length());
        this.password = password;
        this.webdavClient = new WebdavClient(this);
    }

    /**
     * Get the user's Webdav url.
     *
     * @return The user's Webdav url.
     */
    public String getUrl()
    {
        return this.url;
    }

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
        return this.webdavClient;
    }

    /**
     * Check if a login name is correct.
     * The name is correct when it contains an @ with an username before and an url after.
     *
     * @param name The login name.
     * @return True when valid.
     */
    protected boolean checkName(String name)
    {
        int separatorIndex = name.lastIndexOf('@');

        return separatorIndex != -1 // It must contain the separator character.
                && separatorIndex != 0 // The separator character can't be the first character.
                && separatorIndex != (name.length() - 1); // The separator character can't be the last character.
    }
}
