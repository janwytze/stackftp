package nl.stackftp.ftp;

import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;

public class StackFileSystemFactory implements FileSystemFactory{

    /**
     * Get the FileSystemView of an user.
     *
     * @param user The user.
     * @return The FileSystemView.
     * @throws FtpException Thrown of ftp error.
     */
    public FileSystemView createFileSystemView(User user) throws FtpException {
        StackUser stackUser = (StackUser) user;

        return stackUser.getFileSystemView();
    }
}
