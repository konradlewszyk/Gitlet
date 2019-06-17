package gitlet;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;

import static gitlet.Serialize.toObject;
import static gitlet.Utils.*;

public class Find {
    public static void find(String message) {
        Path currentRelativePath = Paths.get("");
        String pathdir = currentRelativePath.toAbsolutePath().toString();
        List<String> allCommit = null;
        try {
            allCommit = (List<String>) toObject(pathdir + "/.gitlet/References/globalCommits");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Boolean notprinted = true;
        for (String id:allCommit) {
            CommitObject item = null;
            try {
                item = (CommitObject) toObject(pathdir + "/.gitlet/" + id);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (item.message.equals(message)) {
                System.out.println(id);
                notprinted = false;
            }
        }
        if (notprinted) {
            System.out.println("Found no commit with that message.");
        }
    }
}
