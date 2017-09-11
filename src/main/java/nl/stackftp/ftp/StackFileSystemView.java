package nl.stackftp.ftp;

import nl.stackftp.webdav.WebdavClient;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;

import java.io.IOException;
import java.nio.file.Paths;

public class StackFileSystemView implements FileSystemView {

    /**
     * The user of this FileSystemView.
     */
    protected StackUser stackUser;

    /**
     * The current working directory.
     * Must be absolute, so always starts with /!
     */
    protected String workingDirectory;

    /**
     * The FileSystemView constructor.
     *
     * @param stackUser The user.
     */
    public StackFileSystemView(StackUser stackUser) {
        this.stackUser = stackUser;
        this.workingDirectory = "/";
    }

    /**
     * Get the home directory of this FileSystemView.
     * Always "/" because this isn't a real file system.
     *
     * @return The home directory.
     */
    public FtpFile getHomeDirectory() throws FtpException {
        try {
            return new StackFile("/", this.stackUser);
        } catch (IOException ex) {
            throw new FtpException("Could not get home directory");
        }
    }

    /**
     * Get the current working directory.
     *
     * @return The current working directory.
     */
    public FtpFile getWorkingDirectory() throws FtpException {
        try {
            return new StackFile(this.workingDirectory, this.stackUser);
        } catch (IOException ex) {
            throw new FtpException("Could not get working directory");
        }
    }

    /**
     * Change from working directory.
     * First check on webdav if the directory exists.
     *
     * @param directory The directory to change to.
     * @return True when change is possible.
     */
    public boolean changeWorkingDirectory(String directory) throws FtpException {
        directory = this.formatFile(directory);
        WebdavClient webdavClient = this.stackUser.getWebdavClient();

        try {
            if (!webdavClient.exists(directory)) {
                return false;
            }
        } catch (IOException ex) {
            return false;
        }

        this.workingDirectory = directory;

        return true;
    }

    /**
     * Get a file from Webdav.
     *
     * @param path The file path.
     * @return The file.
     * @throws FtpException Thrown when getting file failed.
     */
    public FtpFile getFile(String path) throws FtpException {
        path = this.formatFile(path);

        try {
            return new StackFile(path, this.stackUser);
        } catch (IOException ex) {
            throw new FtpException("Could not get file");
        }
    }

    /**
     * Is the file content random accessible?
     *
     * @return True when random accessible.
     */
    public boolean isRandomAccessible() throws FtpException {
        return false;
    }

    /**
     * Dispose FileSystemView.
     */
    public void dispose() {
    }

    /**
     * Format a file string.
     *
     * @param path The path to format.
     * @return A valid file string.
     */
    protected String formatFile(String path) {
        // If not an absolute path add the working directory.
        if (!path.startsWith("/")) {
            path = this.workingDirectory + '/' + path;
        }

        // Remove Redundancies.
        path = Paths.get(path).normalize().toString();

        return path;
    }
}
