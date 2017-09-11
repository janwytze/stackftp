package nl.stackftp.ftp;

import org.apache.ftpserver.ftplet.FtpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    /**
     * The application context.
     */
    @Autowired
    protected ApplicationContext applicationContext;

    /**
     * A Map with all users that have connected while the application is running.
     */
    protected Map<String, StackUser> userList = new HashMap<>();

    /**
     * Authenticate an user by username and password.
     *
     * @param username The username.
     * @param password The password.
     * @return The StackUser object.
     * @throws FtpException Thrown when authenticating failed.
     */
    public StackUser authenticate(String username, String password) throws FtpException {
        if (!this.checkName(username)) {
            throw new FtpException("Name not correct");
        }

        // This string contains the username and password so it works easily with a Map.
        String credentialsString = String.format("%s:%s", username, password);

        StackUser stackUser;

        // Check if the user is already added to the list.
        if (this.userList.containsKey(credentialsString)) {
            // Return the user when it is found.
            stackUser = this.userList.get(credentialsString);
        } else {
            // Create an user when it isn't found and add it to the userList.
            //stackUser = new StackUser(username, password);
            stackUser = this.applicationContext.getBean(StackUser.class, username, password);
            this.userList.put(credentialsString, stackUser);
        }

        // Always re-authenticate an user, even if it is in the list.
        // This is because the password may have changed
        this.authenticateUser(stackUser);

        return stackUser;
    }

    /**
     * Check the user credentials.
     *
     * @param stackUser The user to check.
     * @throws FtpException Thrown when the credentials are incorrect.
     */
    protected void authenticateUser(StackUser stackUser) throws FtpException {
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
    protected boolean checkName(String name) {
        int separatorIndex = name.lastIndexOf('@');

        return separatorIndex != -1 // It must contain the separator character.
                && separatorIndex != 0 // The separator character can't be the first character.
                && separatorIndex != (name.length() - 1); // The separator character can't be the last character.
    }
}
