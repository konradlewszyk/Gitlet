package gitlet;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static gitlet.Serialize.toObject;
import static gitlet.Utils.*;

public class Log {
    public static void log() {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        String headPointer = null;
        try {
            headPointer = (String) toObject(s + "/.gitlet/References/Head");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Map<String, String> branchMap = null;
        try {
            branchMap = (Map<String, String>) toObject(s + "/.gitlet/References/branchMap");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String currentID = branchMap.get(headPointer); //retrieve headpointer
        CommitObject currentCommit = null;
        try {
            currentCommit = (CommitObject) toObject(s + "/.gitlet/" + currentID);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        while (currentCommit.parent != null) {
            System.out.println("===");
            System.out.println("Commit " + currentID);
            System.out.println(currentCommit.timeDate);
            System.out.println(currentCommit.message);
            System.out.println(" ");
            CommitObject commitParent = null;
            try {
                commitParent = (CommitObject) toObject(s + "/.gitlet/" + currentCommit.parent);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            currentID = currentCommit.parent;
            currentCommit = commitParent; //traversing from child to parent commit
            if (currentCommit.parent == null) {
                System.out.println("===");
                System.out.println("Commit " + currentID);
                System.out.println(currentCommit.timeDate);
                System.out.println(currentCommit.message);
                System.out.println(" ");
            }
        }
    }

    public static void globalLog() {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        List<String> allCommit = null;
        try {
            allCommit = (List<String>) toObject(s + "/.gitlet/References/globalCommits");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        for (String commitID : allCommit) {
            CommitObject item = null;
            try {
                item = (CommitObject) toObject(s + "/.gitlet/" + commitID);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println("===");
            System.out.println("Commit " + commitID);
            System.out.println(item.timeDate);
            System.out.println(item.message);
            System.out.println(" ");
        }
    }
}

