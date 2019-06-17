package gitlet;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.Map;

import static gitlet.Utils.*;

public class CheckOut extends Serialize {
    public static void checkOutRecentFile(String fileName) {
        Path currentRelativePath = Paths.get("");
        String pathdir = currentRelativePath.toAbsolutePath().toString();
        String headPointer = null;
        try {
            headPointer = (String) toObject(pathdir + "/.gitlet/References/Head");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Map<String, String> branchMap = null;
        try {
            branchMap = (Map<String, String>) toObject(pathdir + "/.gitlet/References/branchMap");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String commitID = branchMap.get(headPointer);
        CommitObject recentCommit = null;
        try {
            recentCommit = (CommitObject) toObject(pathdir + "/.gitlet/" + commitID);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (!recentCommit.filesCommitted.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        String filesha1 = recentCommit.filesCommitted.get(fileName);
        File gitletFile = new File(pathdir + "/.gitlet/" + filesha1);
        byte[] fileContent = readContents(gitletFile);
        File workingFile = new File(pathdir + "/" + fileName);
        //miracle pill this shit works baby
        byte[] n = null;
        try {
            n = (byte[]) toObject(pathdir + "/.gitlet/" + filesha1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        writeContents(workingFile, n);
        //Utils.writeContents(workingFile,fileContent);
    }

    public static void checkOutWithID(String commitID, String fileName) {
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
        if (!allCommit.contains(commitID)) {
            System.out.println("No commit with that id exists.");
            return;
        }
        CommitObject theCommit = null;
        try {
            theCommit = (CommitObject) toObject(pathdir + "/.gitlet/" + commitID);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (!theCommit.filesCommitted.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        String filesha1 = theCommit.filesCommitted.get(fileName);
        File gitletFile = new File(pathdir + "/.gitlet/" + filesha1);
        byte[] fileContent = readContents(gitletFile);
        File workingFile = new File(pathdir + "/" + fileName);
        byte[] n = null;
        try {
            n = (byte[]) toObject(pathdir + "/.gitlet/" + filesha1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        writeContents(workingFile, n);


    }

    public static void checkOutBranch(String branchName) throws IOException {
        Path currentRelativePath = Paths.get("");
        String pathdir = currentRelativePath.toAbsolutePath().toString();
        String headPointer = null;
        try {
            headPointer = (String) toObject(pathdir + "/.gitlet/References/Head");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Map<String, String> branchMap = null;
        try {
            branchMap = (Map<String, String>)
                    toObject(pathdir + "/.gitlet/References/branchMap");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Map<String, String> trackedFiles = null;
        try {
            trackedFiles = (Map<String, String>)
                    toObject(pathdir + "/.gitlet/References/trackedFiles");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String commitID = branchMap.get(headPointer);
        CommitObject recentCommit = null;
        try {
            recentCommit = (CommitObject) toObject(pathdir + "/.gitlet/" + commitID);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (!branchMap.containsKey(branchName)) {
            System.out.println("No such branch exist.");
            return;
        }
        if (headPointer == branchName) {
            System.out.println("No need to check out current branch.");
            return;
        }
        for (String sha1 : recentCommit.filesCommitted.values()) {
            if (trackedFiles.containsValue(sha1)) {
                System.out.println("There is an untracked "
                      + "file in the way; delete it or add it first.");
                return;
            }
        }
        headPointer = branchName;
        File head = new File(pathdir + "/.gitlet/References/Head");
        writeContents(head, objToByte(headPointer));
    }
}
