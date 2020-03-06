package gui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Filehandler {
    private String targetPath = System.getProperty("user.dir") + "/.files/";

    public void uploadFile(String filePath, String fileName, String extension, int spelerID) {
        Path sourceDirectory = Paths.get(filePath);
        Path targetDirectory = Paths.get(String.format("%s%s/%s.%s",targetPath, spelerID, fileName, extension));
        try {
            Files.createDirectories(targetDirectory.getParent());
            Files.copy(sourceDirectory, targetDirectory, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ImageView getProfilePic(int spelerId, String profielfotoExt) {
        return new ImageView("file:"+targetPath + spelerId + "/profilePic." + profielfotoExt);
    }

    public Image getBackgroundPic(int spelerId, String achtergrondfotoExt) {
        return new Image("file:"+targetPath + spelerId + "/backgroundPic." + achtergrondfotoExt);
    }
}
