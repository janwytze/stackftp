package nl.stackftp.ftp;

import nl.stackftp.webdav.WebdavClient;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;

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
        return new StackFile("/", this.stackUser);
    }

    /**
     * Get the current working directory.
     *
     * @return The current working directory.
     */
    public FtpFile getWorkingDirectory() throws FtpException {
        return new StackFile(this.workingDirectory, this.stackUser);
    }

    /**
     * Change from working directory.
     * First check on webdav if the directory exists.
     *
     * @param directory The directory to change to.
     * @return True when change is possible.
     */
    public boolean changeWorkingDirectory(String directory) throws FtpException {
        directory = this.formatDirectory(directory);

        WebdavClient webdavClient = this.stackUser.getWebdavClient();

        if (!webdavClient.exists(directory)) {
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

        return new StackFile(path, this.stackUser);
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
        boolean isDirectory = path.endsWith("/");

        // If not an absolute path add the working directory.
        if (!path.startsWith("/")) {
            path = this.workingDirectory + path;
        }

        // Remove Redundancies.
        path = Paths.get(path).normalize().toString();

        // Re-add the / when the file is a directory.
        // This is necessary to not confuse files with directories.
        if (isDirectory && !path.endsWith("/")) {
            path += '/';
        }

        return path;
    }

    /**
     * Format a directory string.
     * Don't execute after formatFile has been executed. This would at the working directory twice.
     *
     * @param path The path to format.
     * @return A valid directory string.
     */
    protected String formatDirectory(String path) {
        path = this.formatFile(path);

        if (!path.endsWith("/")) {
            path += '/';
        }

        return path;
    }
}
