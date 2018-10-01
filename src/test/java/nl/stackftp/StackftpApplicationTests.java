package nl.stackftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;

import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"FTP_PORT = 2121"})
public class StackftpApplicationTests {

    /**
     * The ftp port.
     */
    @Value("#{environment.FTP_PORT?:21}")
    private int ftpPort;

    /**
     * The ftp address.
     */
    @Value("#{environment.FTP_HOST?:'127.0.0.1'}")
    private String ftpAddress;

    /**
     * The ftp username.
     */
    @Value("#{environment.USERNAME}")
    private String username;

    /**
     * The ftp password.
     */
    @Value("#{environment.PASSWORD}")
    private String password;

    /**
     * Check if the application starts.
     */
    @Test
    public void contextLoads() {
    }

    /**
     * Try to login on the FTP server.
     * This test should not be executed in production because it contains user credentials.
     * The credentials used must be provided with environment variables.
     * The env variables used are USERNAME and PASSWORD.
     */
    @Test
    public void ftpConnection() throws Exception {
        if (this.username == null || this.password == null) {
            throw new Exception("No username and password provided");
        }

        FTPClient ftpClient = new FTPClient();

        // Connect to the server.
        ftpClient.connect(this.ftpAddress, this.ftpPort);

        // Login with the provided credentials.
        boolean loginSuccess = ftpClient.login(this.username, this.password);

        if (!loginSuccess) {
            throw new Exception("Username or password wrong");
        }

        RandomStringGenerator randomFileNameGenerator = (new RandomStringGenerator.Builder())
                .withinRange('0', 'z')
                .filteredBy(LETTERS, DIGITS)
                .build();

        // Generate a random filename and some content.
        String fileName = "/" + randomFileNameGenerator.generate(20) + ".test";
        String fileContent = "test";

        // Try to store a file.
        boolean storeSuccess = ftpClient.storeFile(fileName, new ByteArrayInputStream(fileContent.getBytes()));

        if (!storeSuccess) {
            throw new Exception("Upload has failed");
        }

        // Wait for webdav upload.
        // This is needed because the file get's uploaded in a thread.
        // When the thread isn't finished before the delete the file won't exist on the webdav server.
        Thread.sleep(2000);

        boolean deleteSuccess = ftpClient.deleteFile(fileName);

        if (!deleteSuccess) {
            throw new Exception("Delete has failed");
        }

        ftpClient.disconnect();
    }
}
