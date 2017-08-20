package nl.stackftp.webdav;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import nl.stackftp.ftp.StackFile;
import nl.stackftp.ftp.StackUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebdavClient {

    /**
     * The user's name.
     */
    protected String name;

    /**
     * The user's password.
     */
    protected String password;

    /**
     * The actual webdav client.
     */
    protected Sardine sardine;

    /**
     * The WebdavClient constructor.
     *
     * @param stackUser The StackUser.
     */
    public WebdavClient(StackUser stackUser)
    {
        this(stackUser.getName(), stackUser.getPassword());
    }

    /**
     * The WebdavClient constructor.
     *
     * @param name The username.
     * @param password The password.
     */
    public WebdavClient(String name, String password)
    {
        this.name = name;
        this.password = password;

        // Create client with username and password.
        this.sardine = SardineFactory.begin(this.name, this.password);
    }

    /**
     * Get the webdav base url.
     *
     * @return The url.
     */
    protected String getUrl()
    {
        return String.format("https://%s.stackstorage.com/remote.php/webdav", this.name);
    }

    /**
     * Get the stack user.
     *
     * @return The staack user.
     */
    protected StackUser getStackUser()
    {
        return new StackUser(this.name, this.password);
    }

    /**
     * Check if the username and password are correct.
     *
     * @return True when correct.
     */
    public boolean authenticate()
    {
        return this.exists("/");
    }

    /**
     * Check if a file exists.
     *
     * @param path The path to check. Must be absolute.
     * @return True when exists.
     */
    public boolean exists(String path)
    {
        try {
            this.sardine.exists(this.getUrl() + path);
        } catch (IOException ex) {
            return false;
        }

        return true;
    }

    /**
     * Get a file list of a directory.
     *
     * @param path The path to get. Must be absolute.
     * @return List of files.
     */
    public List<StackFile> list(String path)
    {
        try {
            List<DavResource> davResources = this.sardine.list(this.getUrl() + path);
            List<StackFile> fileList = new ArrayList<>();

            // Skip first.
            for (int resourceIndex = 1; resourceIndex < davResources.size(); resourceIndex++) {
                DavResource davResource = davResources.get(resourceIndex);
                fileList.add(new StackFile(davResource.getPath().substring(18),
                        this.getStackUser(),
                        davResource.getContentLength()));
            }

            return fileList;
        } catch (IOException ex) {
            return null;
        }
    }
}
