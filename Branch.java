package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static gitlet.Serialize.objToByte;
import static gitlet.Serialize.toObject;
import static gitlet.Utils.*;

public class Branch {
    public static void branch(String branch) throws IOException {
        //the commit given is the most recent commit because when a branch is created
        // it just makes the pointer.
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        String headPointer = null;
        try {
            headPointer = (String) toObject(s + "/.gitlet/References/Head");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Map<String, String> branchMap = null;
        try {
            branchMap = (Map<String, String>) toObject(s + "/.gitlet/References/branchMap");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (branchMap.containsKey(branch)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        String commmitID = branchMap.get(headPointer);
        branchMap.put(branch, commmitID);
        File bmap = new File(s + "/.gitlet/References/branchMap");
        writeContents(bmap, objToByte(branchMap));
    }

    public static void removeBranch(String branchName) throws IOException {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        Map<String, String> branchMap = null;
        try {
            branchMap = (Map<String, String>) toObject(s + "/.gitlet/References/branchMap");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String headPointer = null;
        try {
            headPointer = (String) toObject(s + "/.gitlet/References/Head");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (!branchMap.containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (branchName == headPointer) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        branchMap.remove(branchName);
        File bmap = new File(s + "/.gitlet/References/branchMap");
        writeContents(bmap, objToByte(branchMap));
    }

}
