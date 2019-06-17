package gitlet;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static gitlet.Serialize.toObject;

public class Status {
    public static void status() {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        Map<String, String> branchMap = null;
        try {
            branchMap = (Map<String, String>) toObject(s + "/.gitlet/References/branchMap");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String headPointer = null;
        try {
            headPointer = (String) toObject(s + "/.gitlet/References/Head");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("=== Branches ===");
        String head = null;
        for (Map.Entry<String, String> entry : branchMap.entrySet()) {
            String key = entry.getKey();
            if (headPointer == key) { //find headpointer in directory and retrieve
                System.out.println("*" + key);
            } else {
                System.out.println("*" + key);
            }
        }
        System.out.println(" ");
        System.out.println("=== Staged Files ===");
        Map<String, byte[]> stagingArea = null;
        try {
            stagingArea = (Map<String, byte[]>) toObject(s + "/.gitlet/References/stagingArea");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        for (String key : stagingArea.keySet()) {
            System.out.println(key);
        }
        System.out.println(" ");
        System.out.println("=== Removed Files ===");
        List<String> fileToRemove = null;
        try {
            fileToRemove = (ArrayList<String>) toObject(s + "/.gitlet/References/fileToBeRemoved");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        for (String entry : fileToRemove) {
            System.out.println(entry);
        }
        System.out.println(" ");
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println(" ");
        System.out.println("=== Untracked Files ===");
    }
}
