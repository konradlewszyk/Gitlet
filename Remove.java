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


public class Remove extends Serialize {
    public static void remove(String filename) throws IOException, ClassNotFoundException {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        List<String> workingDirectory = Utils.plainFilenamesIn(s);
        HashMap<String, String> tracked = (HashMap<String, String>)
                toObject(s + "/.gitlet/References/trackedFiles");
        Map<String, String> staged = (HashMap<String, String>)
                toObject(s + "/.gitlet/References/stagingArea");
        List<String> filesToRemove = (ArrayList<String>)
                toObject(s + "/.gitlet/References/fileToBeRemoved");

        Map<String, String> branchMap = (HashMap<String, String>)
                toObject(s + "/.gitlet/References/branchMap");
        String hEAD = (String) toObject(s + "/.gitlet/References/Head");

        String commitID = branchMap.get(hEAD);
        CommitObject givenCommit = (CommitObject) toObject(s + "/.gitlet/" + commitID);

        for (Map.Entry<String, String> entry : givenCommit.filesCommitted.entrySet()) {
            String key = entry.getKey();
            if (!tracked.containsKey(filename) && !staged.
                    containsKey(filename) && (key != filename)) {
                System.out.print("no reason to remove this file.");
                return;
            }
        }


        if (!tracked.containsKey(filename) && staged.containsKey(filename)) {
            staged.remove(filename);
            File stagedfiles = new File(s + "/.gitlet/References/stagingArea");
            writeContents(stagedfiles, objToByte(staged));
            return;
        }
        if (tracked.containsKey(filename)) {
            tracked.remove(filename);
            File trackedfiles = new File(s + "/.gitlet/References/trackedFiles");
            writeContents(trackedfiles, objToByte(tracked));
            Utils.restrictedDelete(s + "/" + filename);
        }
        if (staged.containsKey(filename)) {
            staged.remove(filename);
            File stagedfiles = new File(s + "/.gitlet/References/stagingArea");
            writeContents(stagedfiles, objToByte(staged));
        }
        filesToRemove.add(filename);
        writeContents(new File(s + "/.gitlet/References/fileToBeRemoved"),
                objToByte(filesToRemove));
    }
}
