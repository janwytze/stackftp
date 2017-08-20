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
     * Size of the file.
     */
    protected Long size = 0L;

    /**
     * The StackFile constructor.
     *
     * @param path The file path. Must be absolute!.
     * @param stackUser The file user.
     */
    public StackFile(String path, StackUser stackUser)
    {
        this.stackUser = stackUser;
        this.path = path;
    }

    /**
     * The StackFile constructor.
     *
     * @param path The file path. Must be absolute!.
     * @param stackUser The file user.
     * @param size The file size.
     */
    public StackFile(String path, StackUser stackUser, Long size)
    {
        this(path, stackUser);
        this.size = size;
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
     * Always true.
     *
     * @return True when exists.
     */
    public boolean doesExist() {
        return true;
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
        return !this.path.equals("/");
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

    public int getLinkCount() {
        return 0;
    }

    public long getLastModified() {
        return 0;
    }

    public boolean setLastModified(long l) {
        return false;
    }

    public long getSize() {
        return this.size;
    }

    public Object getPhysicalFile() {
        return null;
    }

    public boolean mkdir() {
        /*
         * @Todo create directory.
         */
        return false;
    }

    public boolean delete() {
        /*
         * @Todo delete file.
         */
        return false;
    }

    public boolean move(FtpFile ftpFile) {
        /*
         * @Todo move file.
         */
        return false;
    }

    public List<? extends FtpFile> listFiles() {
        if (this.isDirectory()) {
            WebdavClient webdavClient = this.stackUser.getWebdavClient();

            return webdavClient.list(this.path);
        }

        return null;
    }

    public OutputStream createOutputStream(long l) throws IOException {
        return null;
    }

    public InputStream createInputStream(long l) throws IOException {
        return null;
    }
}
