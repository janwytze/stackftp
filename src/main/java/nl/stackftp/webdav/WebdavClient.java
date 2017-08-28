package nl.stackftp.webdav;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import com.github.sardine.impl.SardineException;
import nl.stackftp.ftp.StackFile;
import nl.stackftp.ftp.StackUser;

import java.io.IOException;
import java.io.InputStream;
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
            return this.sardine.exists(this.getUrl() + this.formatPath(path));
        } catch (IOException ex) {
            return false;
        }
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
            List<DavResource> davResources = this.sardine.list(this.getUrl() + this.formatPath(path));
            List<StackFile> fileList = new ArrayList<>();

            // Skip first.
            for (int resourceIndex = 1; resourceIndex < davResources.size(); resourceIndex++) {
                DavResource davResource = davResources.get(resourceIndex);
                fileList.add(new StackFile(davResource.getPath().substring(18),
                        this.getStackUser(),
                        davResource.getContentLength(),
                        davResource.getModified().getTime()
                        ));
            }

            return fileList;
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Delete a file or directory.
     *
     * @param path The path to delete.
     * @return True when successful.
     */
    public boolean delete(String path)
    {
        try {
            this.sardine.delete(this.getUrl() + this.formatPath(path));
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Get a file from webdav.
     *
     * @param path The file path.
     * @return The file input stream.
     * @throws IOException Thrown when getting file failed.
     */
    public InputStream get(String path) throws IOException
    {
        return this.sardine.get(this.getUrl() + this.formatPath(path));
    }

    /**
     * Move a file.
     *
     * @param fromPath From path.
     * @param toPath To path.
     * @return True when successful.
     */
    public boolean move(String fromPath, String toPath)
    {
        try {
            this.sardine.move(this.getUrl() + this.formatPath(fromPath), this.getUrl() + this.formatPath(toPath));
        } catch (IOException ex) {
            return false;
        }

        return true;
    }

    /**
     * Create a directory.
     *
     * @param path The path.
     * @return True when successful.
     */
    public boolean mkdir(String path)
    {
        try {
            this.sardine.createDirectory(this.getUrl() + this.formatPath(path));
        } catch (IOException ex) {
            return false;
        }

        return true;
    }

    /**
     * Format the path.
     *
     * @param path The path.
     * @return Formatted path.
     */
    protected String formatPath(String path)
    {
        return path.replace(" ", "%20");
    }
}
