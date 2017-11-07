package nl.stackftp.ftp;

import org.apache.ftpserver.ftplet.FtpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    /**
     * The application context.
     */
    @Autowired
    protected ApplicationContext applicationContext;

    /**
     * Authenticate an user by username and password.
     *
     * @param username The username.
     * @param password The password.
     * @return The StackUser object.
     * @throws FtpException Thrown when authenticating failed.
     */
    @Cacheable("authenticate")
    public StackUser authenticate(String username, String password) throws FtpException {
        if (!this.checkName(username)) {
            throw new FtpException("Name not correct");
        }

        StackUser stackUser = this.applicationContext.getBean(StackUser.class, username, password);

        this.authenticateUser(stackUser);

        return stackUser;
    }

    /**
     * Check the user credentials.
     *
     * @param stackUser The user to check.
     * @throws FtpException Thrown when the credentials are incorrect.
     */
    private void authenticateUser(StackUser stackUser) throws FtpException {
        if (!stackUser.getWebdavClient().authenticate()) {
            throw new FtpException("Username or password wrong");
        }
    }

    /**
     * Check if a login name is correct.
     * The name is correct when it contains an @ with an username before and an url after.
     *
     * @param name The login name.
     * @return True when valid.
     */
    private boolean checkName(String name) {
        int separatorIndex = name.lastIndexOf('@');

        return separatorIndex != -1 // It must contain the separator character.
                && separatorIndex != 0 // The separator character can't be the first character.
                && separatorIndex != (name.length() - 1); // The separator character can't be the last character.
    }
}
