package gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.net.ftp.FTPClient;

public class FTPserver {

    private final String server = "files.000webhost.com";
    private final String user = "app-1524829407";
    private final String pass = "123Brambestanie";

    public void uploadFile(String filePath, String fileName, String extension, int spelerID) {
        FTPClient client = new FTPClient();
        FileInputStream fis = null;
        try {
            client.connect(server);
            client.login(user, pass);
            client.setDataTimeout(6000);
            client.setDefaultTimeout(6000);
            client.setSoTimeout(3000);
            client.enterLocalPassiveMode();
            if (!client.changeWorkingDirectory("/public_html/" + spelerID)) {
                boolean result = client.makeDirectory("/public_html/" + spelerID);
                client.changeWorkingDirectory("/public_html/" + spelerID);
                //System.out.println("result: "+result);
                //System.out.println(client.printWorkingDirectory());
            }
            //System.out.println("end if");
            // Create an InputStream of the file to be uploaded
            File file = new File(filePath);
            fis = new FileInputStream(file);
            // Store file on server and logout
            //System.out.println(client.get);
            client.setFileType(client.BINARY_FILE_TYPE, client.BINARY_FILE_TYPE);
            client.setFileTransferMode(client.BINARY_FILE_TYPE);
            client.storeFile(fileName + "." + extension, fis);
            client.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
