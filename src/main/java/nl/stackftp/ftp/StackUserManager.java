package nl.stackftp.ftp;

import nl.stackftp.webdav.WebdavClient;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;

public class StackUserManager implements UserManager {

    /**
     * Get an user by name.
     *
     * @param name The user name.
     * @return Always null because there are no real users.
     */
    public User getUserByName(String name) throws FtpException
    {
        return null;
    }

    /**
     * Get a list of all user names.
     *
     * @throws FtpException Always thrown because there are no real users.
     */
    public String[] getAllUserNames() throws FtpException
    {
        throw new FtpException("No ftp users");
    }

    /**
     * Delete an user.
     *
     * @param name The user name
     * @throws FtpException Always thrown because there are no real users.
     */
    public void delete(String name) throws FtpException
    {
        throw new FtpException("Can't delete user");
    }

    /**
     * Save an user.
     *
     * @param name The user name.
     * @throws FtpException Always thrown because there are no real users.
     */
    public void save(User name) throws FtpException
    {
        throw new FtpException("Can't save user");
    }

    /**
     * Does an user exists?
     * Always true because this can't be checked.
     *
     * @param name The user name.
     * @return Always true.
     */
    public boolean doesExist(String name) throws FtpException
    {
        return true;
    }

    /**
     * Authenticate an user by name and password.
     *
     * @param authentication The name and password.
     * @return The user when credentials are correct.
     * @throws AuthenticationFailedException Thrown when credentials are wrong or Webdav server connection failed.
     */
    public User authenticate(Authentication authentication) throws AuthenticationFailedException
    {
        // Cast the object so the username and password are readable.
        UsernamePasswordAuthentication userAuthentication = (UsernamePasswordAuthentication) authentication;

        WebdavClient webdavClient = new WebdavClient(
                userAuthentication.getUsername(),
                userAuthentication.getPassword());

        if (webdavClient.authenticate()) {
            return new StackUser(userAuthentication.getUsername(), userAuthentication.getPassword());
        }

        throw new AuthenticationFailedException("Username or password wrong");
    }

    /**
     * Get the admin name.
     *
     * @throws FtpException Always thrown because there is no admin.
     */
    public String getAdminName() throws FtpException
    {
        throw new FtpException("No server admin");
    }

    public boolean isAdmin(String name) throws FtpException
    {
        return false;
    }
}