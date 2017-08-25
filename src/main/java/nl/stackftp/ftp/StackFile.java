package nl.stackftp.ftp;

import nl.stackftp.webdav.WebdavClient;
import org.apache.ftpserver.ftplet.FtpFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class StackFile implements FtpFile {

    /**
     * The user of this file.
     */
    protected StackUser stackUser;

    /**
     * The path of this file.
     * Must be absolute, so always starts with /!
     */
    protected String path;

    /**
     * Does this file exists.
     */
    protected boolean exists;

    /**
     * Size of the file.
     */
    protected Long size = 0L;

    /**
     * The last modified date of this file.
     */
    protected Long lastModified = 0L;

    /**
     * The StackFile constructor.
     *
     * @param path The file path. Must be absolute!.
     * @param stackUser The file user.
     */
    public StackFile(String path, StackUser stackUser)
    {
        WebdavClient webdavClient = stackUser.getWebdavClient();

        this.stackUser = stackUser;
        this.path = path;
        this.exists = webdavClient.exists(this.path);
    }

    /**
     * The StackFile constructor.
     *
     * @param path The file path. Must be absolute!
     * @param stackUser The file user.
     * @param size The file size.
     */
    public StackFile(String path, StackUser stackUser, Long size)
    {
        this(path, stackUser);
        this.size = size;
    }

    /**
     * The StackFile constructor.
     *
     * @param path The file path. Must be absolute!
     * @param stackUser The file user.
     * @param size The file size.
     * @param lastModified The last modified date.
     */
    public StackFile(String path, StackUser stackUser, Long size, Long lastModified)
    {
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
        String path = this.path;

        if (this.isDirectory()) {
            path = path.substring(0, path.length()-1);
        }

        return path.substring(path.lastIndexOf("/") + 1);
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
        // If the path ends with / it is a directory.
        return this.path.endsWith("/");
    }

    /**
     * Is this file a file?
     *
     * @return True when file.
     */
    public boolean isFile() {
        // If the path doesn't ends with / it is a directory.
        return !this.path.endsWith("/");
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
        // Everything is removable except for root directory.
        return this.exists && !this.path.equals("/");
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
        return 0;
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

        return webdavClient.mkdir(this.path);
    }

    /**
     * Delete this file.
     *
     * @return True when successful.
     */
    public boolean delete() {
        WebdavClient webdavClient = this.stackUser.getWebdavClient();

        return webdavClient.delete(this.path);
    }

    /**
     * Move a file.
     *
     * @param ftpFile The destination file.
     * @return True when successfull.
     */
    public boolean move(FtpFile ftpFile) {
        WebdavClient webdavClient = this.stackUser.getWebdavClient();

        return webdavClient.move(this.path, ftpFile.getAbsolutePath());
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
     * Upload a file to the server.
     *
     * @param l Write offset.
     * @return The output stream.
     * @throws IOException Thrown on upload fail.
     */
    public OutputStream createOutputStream(long l) throws IOException {
        return new StackOutputStream(this);
    }

    /**
     * Get the input stream of the file.
     *
     * @param l Read offset.
     * @return The file output stream.
     * @throws IOException Thrown when getting file failed.
     */
    public InputStream createInputStream(long l) throws IOException {
        /*
         * @Todo Do something with l.
         */
        WebdavClient webdavClient = this.stackUser.getWebdavClient();

        return webdavClient.get(this.path);
    }

    /**
     * Get the user of this file.
     *
     * @return The user.
     */
    public StackUser getStackUser()
    {
        return this.stackUser;
    }
}
