package nl.stackftp.ftp;

import com.github.sardine.impl.SardineException;
import nl.stackftp.webdav.WebdavClient;
import org.apache.ftpserver.ftplet.FtpFile;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;

public class StackFile implements FtpFile {

    /**
     * The user of this file.
     */
    private StackUser stackUser;

    /**
     * The path of this file.
     * Must be absolute, so always starts with /!
     */
    private String path;

    /**
     * Does this file exists.
     */
    private boolean exists;

    /**
     * Is the file a directory.
     */
    private Boolean isDirectory;

    /**
     * Size of the file.
     */
    private long size = 0;

    /**
     * The last modified date of this file.
     */
    private long lastModified = 0;

    /**
     * The StackFile constructor.
     *
     * @param path The file path. Must be absolute!
     * @param stackUser The file user.
     */
    public StackFile(String path, StackUser stackUser) throws IOException {
        WebdavClient webdavClient = stackUser.getWebdavClient();

        this.stackUser = stackUser;
        this.path = path;

        try {
            this.isDirectory = webdavClient.isDirectory(this.path);
            this.exists = true;
        } catch (SardineException ex) {
            if (ex.getStatusCode() != 404) {
                throw ex;
            }
            this.exists = false;
        }
    }

    /**
     * The StackFile constructor.
     *
     * @param path The file path. Must be absolute!
     * @param stackUser The file user.
     * @param size The file size.
     */
    private StackFile(String path, StackUser stackUser, long size) throws IOException {
        this(path, stackUser);

        // To prevent directories not appearing set the minimum size to 0.
        this.size = Math.max(0, size);
    }

    /**
     * The StackFile constructor.
     *
     * @param path The file path. Must be absolute!
     * @param stackUser The file user.
     * @param size The file size.
     * @param lastModified The last modified date.
     */
    public StackFile(String path, StackUser stackUser, long size, long lastModified) throws IOException {
        this(path, stackUser, size);
        this.lastModified = lastModified;
    }

    /**
     * Get the absolute path of this file.
     *
     * @return The absolute path.
     */
    public String getAbsolutePath() {
        return this.path;
    }

    /**
     * Get the name of this file.
     *
     * @return The file name.
     */
    public String getName() {
        // Prevent NullPointerException.
        if (this.path.equals("/")) {
            return "";
        }

        return Paths.get(this.path).getFileName().toString();
    }

    /**
     * Is this file hidden?
     * Always false.
     *
     * @return True when hidden.
     */
    public boolean isHidden() {
        return false;
    }

    /**
     * Is this file a directory?
     *
     * @return True when directory.
     */
    public boolean isDirectory() {
        return this.isDirectory != null && this.isDirectory;
    }

    /**
     * Is this file a file?
     *
     * @return True when file.
     */
    public boolean isFile() {
        return this.isDirectory != null && !this.isDirectory;
    }

    /**
     * Does this file exists?
     *
     * @return True when exists.
     */
    public boolean doesExist() {
        return this.exists;
    }

    /**
     * Is this file readable.
     * Always true.
     *
     * @return True when readable.
     */
    public boolean isReadable() {
        return true;
    }

    /**
     * Is this file writable.
     * Always true.
     *
     * @return True when writable.
     */
    public boolean isWritable() {
        return true;
    }

    /**
     * Is this file removable.
     *
     * @return True when removable.
     */
    public boolean isRemovable() {
        // All existing files are removable except for home directory.
        return this.exists && !this.path.equals(this.stackUser.getHomeDirectory());
    }

    /**
     * Get the owner name of the file.
     *
     * @return The file owner name.
     */
    public String getOwnerName() {
        return this.stackUser.getName();
    }

    /**
     * Get the group name of the file.
     *
     * @return The file group name.
     */
    public String getGroupName() {
        return this.stackUser.getName();
    }

    /**
     * Get the link count of this file.
     *
     * @return The link count.
     */
    public int getLinkCount() {
        return this.isDirectory() ? 3 : 1;
    }

    /**
     * Get the last modified date.
     *
     * @return The last modified date of this file.
     */
    public long getLastModified() {
        return this.lastModified;
    }

    /**
     * Set the last modified date.
     * Always false because it can't be changed.
     *
     * @param l The date.
     * @return True when modified.
     */
    public boolean setLastModified(long l) {
        return false;
    }

    /**
     * Get the size of this file.
     *
     * @return Size of this file.
     */
    public long getSize() {
        return this.size;
    }

    /**
     * Get the physical file.
     *
     * @return The physical file.
     */
    public Object getPhysicalFile() {
        return null;
    }

    /**
     * Create a directory.
     *
     * @return True when successful.
     */
    public boolean mkdir() {
        WebdavClient webdavClient = this.stackUser.getWebdavClient();

        try {
            webdavClient.mkdir(this.path);
        } catch (IOException ex) {
            return false;
        }

        return true;
    }

    /**
     * Delete this file.
     *
     * @return True when successful.
     */
    public boolean delete() {
        WebdavClient webdavClient = this.stackUser.getWebdavClient();

        try {
            webdavClient.delete(this.path);
        } catch (IOException ex) {
            return false;
        }

        return true;
    }

    /**
     * Move this file.
     *
     * @param ftpFile The destination file.
     * @return True when successful.
     */
    public boolean move(FtpFile ftpFile) {
        WebdavClient webdavClient = this.stackUser.getWebdavClient();

        try {
            webdavClient.move(this.path, ftpFile.getAbsolutePath());
        } catch (IOException ex) {
            return false;
        }

        return true;
    }

    /**
     * Get list of files in this directory.
     *
     * @return List of files.
     */
    public List<? extends FtpFile> listFiles() {
        if (this.isDirectory()) {
            WebdavClient webdavClient = this.stackUser.getWebdavClient();

            return webdavClient.list(this.path);
        }

        return null;
    }

    /**
     * Upload a file to the Webdav server.
     *
     * @param l Write offset.
     * @return The output stream.
     * @throws IOException Thrown on upload fail.
     */
    public OutputStream createOutputStream(long l) throws IOException {
        PipedOutputStream outputStream = new PipedOutputStream();
        PipedInputStream inputStream = new PipedInputStream(outputStream);

        // Do the HTTP call in a thread so the application can read and write asynchronously.
        new Thread(() -> {
            WebdavClient webdavClient = stackUser.getWebdavClient();

            try {
                webdavClient.put(path, inputStream);
            } catch (IOException ex) { }
        }).start();

        return outputStream;
    }

    /**
     * Download a file from the Webdav server.
     *
     * @param l Read offset.
     * @return The file output stream.
     * @throws IOException Thrown when getting file failed.
     */
    public InputStream createInputStream(long l) throws IOException {
        WebdavClient webdavClient = this.stackUser.getWebdavClient();

        return webdavClient.get(this.path);
    }

    /**
     * Get the user of this file.
     *
     * @return The user.
     */
    public StackUser getStackUser() {
        return this.stackUser;
    }
}
