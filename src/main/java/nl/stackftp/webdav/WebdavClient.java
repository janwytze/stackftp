package nl.stackftp.webdav;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import nl.stackftp.ftp.StackFile;
import nl.stackftp.ftp.StackUser;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
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
    public WebdavClient(StackUser stackUser) {
        this.stackUser = stackUser;

        // Create client with username and password.
        this.sardine = SardineFactory.begin(this.stackUser.getName(), this.stackUser.getPassword());
    }

    /**
     * Get the Webdav base url.
     *
     * @return The url.
     */
    protected String getUrl() {
        return String.format("https://%s/remote.php/webdav", this.stackUser.getUrl());
    }

    /**
     * Get the stack user.
     *
     * @return The user.
     */
    protected StackUser getStackUser() {
        return this.stackUser;
    }

    /**
     * Check if the username and password are correct.
     *
     * @return True when correct.
     */
    public boolean authenticate() {
        try {
            return this.exists("/");
        } catch (IOException ex) {
            return false;
        }
    }

    /**
     * Check if a file exists.
     *
     * @param path The path to check. Must be absolute.
     * @return True when exists.
     * @throws IOException Thrown when exists failed.
     */
    public boolean exists(String path) throws IOException {
        return this.sardine.exists(this.getUrl() + this.encodePath(path));
    }

    /**
     * Get a file list of a directory.
     *
     * @param path The path to get. Must be absolute.
     * @return List of files.
     */
    public List<StackFile> list(String path) {
        try {
            List<DavResource> davResources = this.sardine.list(this.getUrl() + this.encodePath(path));
            List<StackFile> fileList = new ArrayList<>();

            // Skip first.
            for (int resourceIndex = 1; resourceIndex < davResources.size(); resourceIndex++) {
                DavResource davResource = davResources.get(resourceIndex);
                String filePath = davResource.getPath().substring(18);
                if (davResource.getPath().endsWith("/")) {
                    filePath = filePath.substring(0, filePath.length() - 1);
                }
                fileList.add(new StackFile(filePath,
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
     * @throws IOException Thrown when delete failed.
     */
    public void delete(String path) throws IOException {
        this.sardine.delete(this.getUrl() + this.encodePath(path));
    }

    /**
     * Get a file from webdav.
     *
     * @param path The file path.
     * @return The file input stream.
     * @throws IOException Thrown when getting file failed.
     */
    public InputStream get(String path) throws IOException {
        return this.sardine.get(this.getUrl() + this.encodePath(path));
    }

    /**
     * Move a file.
     *
     * @param fromPath From path.
     * @param toPath To path.
     * @throws IOException Thrown when move failed.
     */
    public void move(String fromPath, String toPath) throws IOException {
        this.sardine.move(this.getUrl() + this.encodePath(fromPath), this.getUrl() + this.encodePath(toPath));
    }

    /**
     * Create a directory.
     *
     * @param path The path.
     * @throws IOException Thrown making directory failed.
     */
    public void mkdir(String path) throws IOException {
        this.sardine.createDirectory(this.getUrl() + this.encodePath(path));
    }

    /**
     * Upload a file by piped input stream.
     *
     * @param path The file name.
     * @param inputStream The file to upload.
     * @throws IOException Thrown when put failed.
     */
    public void put(String path, PipedInputStream inputStream) throws IOException {
        this.sardine.put(this.getUrl() + this.encodePath(path), inputStream);
    }

    /**
     * Check if an absolute path is a directory.
     *
     * @param path The absolute path to check.
     * @return True when directory.
     * @throws IOException Thrown on Webdav exception.
     */
    public boolean isDirectory(String path) throws IOException {
        List<DavResource> davResources = this.sardine.list(this.getUrl() + this.encodePath(path));
        for (int resourceIndex = 0; resourceIndex < davResources.size(); resourceIndex++) {
            DavResource davResource = davResources.get(resourceIndex);

            if (davResource.getPath().substring(18, davResource.getPath().length() - 1).equals(this.formatPath(path))
                    && davResource.isDirectory()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Format the path.
     *
     * @param path The path to format.
     * @return Formatted path.
     */
    protected String formatPath(String path) {
        if (path.equals("/")) {
            return "";
        }

        return path;
    }

    /**
     * Encode the path.
     *
     * @param path The path to encode.
     * @return Encoded path.
     */
    protected String encodePath(String path) {
        path = this.formatPath(path);

        return path.replace(" ", "%20");
    }
}
