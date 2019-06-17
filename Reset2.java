package gitlet;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static gitlet.Utils.*;

public class Reset2 extends Serialize {

    public static void reset2(String commitID) throws IOException, ClassNotFoundException {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();

        /** Working Directory */
        List<String> workingDirectory = Utils.plainFilenamesIn(s);
        /** If not commits with the given ID exists */
        List<String> globalCommits = (ArrayList<String>)
                toObject(s + "/.gitlet/References/globalCommits");
        if (!globalCommits.contains(commitID)) {
            System.out.println("No commit with that id exists.");
            return;
        }
        /** If a working file is untracked in the current branch
         * and would be overwritten by the reset */
        List<String> filesToRemove = (ArrayList<String>)
                toObject(s + "/.gitlet/References/fileToBeRemoved");
        CommitObject givenCommit = (CommitObject) toObject(s + "/.gitlet/" + commitID);
        for (Map.Entry<String, String> entry : givenCommit.filesCommitted.entrySet()) {
            String key = entry.getKey();
            for (int i = 0; i < workingDirectory.size() - 1; i = i + 1) {
                if (!givenCommit.filesCommitted.containsKey(workingDirectory.get(i))) {
                    System.out.println("There is an untracked file in the way; "
                            + "delete it or add it first.");
                    return;
                }
            }
            CheckOut.checkOutWithID(commitID, key);
        }


        HashMap<String, String> trackedFiles = (HashMap<String, String>)
                toObject(s + "/.gitlet/References/trackedFiles");
        for (Map.Entry<String, String> entry : trackedFiles.entrySet()) {
            String key = entry.getKey();
            if (!givenCommit.filesCommitted.containsKey(key)) {
                trackedFiles.remove(key);
                filesToRemove.add(key);
            }
        }
        writeContents(new File(s + "/.gitlet/References/fileToBeRemoved"),
                objToByte(filesToRemove));
        String headPointer = (String) toObject(s + "/.gitlet/References/Head");
        headPointer = givenCommit.branchName;
        writeContents(new File(s + "/.gitlet/References/Head"), objToByte(headPointer));
        HashMap<String, String> branchMap = (HashMap<String, String>)
                toObject(s + "/.gitlet/References/branchMap");
        branchMap.put(givenCommit.branchName, commitID);
        /** Clears staging area */
        HashMap<String, byte[]> stagingArea = (HashMap<String, byte[]>)
                toObject(s + "/.gitlet/References/stagingArea");
        stagingArea.clear();
        File stageFile = new File(s + "/.gitlet/References/stagingArea");
        writeContents(stageFile, objToByte(stagingArea));
        /** Updates branchmap */
        File branchFile = new File(s + "/.gitlet/References/branchMap");
        writeContents(branchFile, objToByte(branchMap));
    }
}
