package nl.stackftp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"FTP_PORT = 2121"})
public class StackftpApplicationTests {

	@Test
	public void contextLoads() {
	}

}
