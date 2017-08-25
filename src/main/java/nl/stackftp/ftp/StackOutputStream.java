package nl.stackftp.ftp;

import nl.stackftp.webdav.WebdavClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class StackOutputStream extends ByteArrayOutputStream {

    /**
     * The file to upload.
     */
    protected StackFile stackFile;

    /**
     * StackOutputStream constructor.
     *
     * @param stackFile The file to upload.
     */
    public StackOutputStream(StackFile stackFile)
    {
        this.stackFile = stackFile;
    }

    /**
     * After the ftp upload.
     */
    @Override
    public void close() throws IOException
    {
        WebdavClient webdavClient = this.stackFile.getStackUser().getWebdavClient();

        if (!webdavClient.put(this.stackFile.getAbsolutePath(), this.toByteArray())) {
            throw new IOException("Webdav upload failed");
        }
    }
}
