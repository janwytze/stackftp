package nl.stackftp.webdav;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import nl.stackftp.ftp.StackFile;
import nl.stackftp.ftp.StackUser;
import org.apache.ftpserver.ftplet.FtpException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class WebdavClient {

    /**
     * The user.
     */
    protected StackUser stackUser;

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
        this.stackUser = stackUser;

        // Create client with username and password.
        this.sardine = SardineFactory.begin(this.stackUser.getName(), this.stackUser.getPassword());
    }

    /**
     * Get the Webdav base url.
     *
     * @return The url.
     */
    protected String getUrl()
    {
        return String.format("https://%s/remote.php/webdav", this.stackUser.getUrl());
    }

    /**
     * Get the stack user.
     *
     * @return The user.
     */
    protected StackUser getStackUser()
    {
        return this.stackUser;
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
     * Upload a file by byte array.
     *
     * @param path The file name.
     * @param bytes The file to upload.
     * @return True when successful.
     */
    public boolean put(String path, byte[] bytes)
    {
        try {
            this.sardine.put(this.getUrl() + this.formatPath(path), bytes);
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
