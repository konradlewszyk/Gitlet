package gitlet;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gitlet.Serialize.*;
import static gitlet.Utils.sha1;
import static gitlet.Utils.writeContents;

///we dont need no commitmap, git checkout already gives us the commit object we want

public class CommitMethod extends CommitObject {

    public CommitMethod(HashMap<String, String> commitFiles,
                        String message, String parent, String branchName) {
        super(commitFiles, message, parent, branchName);
    }

    public static void commit(String message) throws java.io.IOException, ClassNotFoundException {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        //things we need to retrieve
        List<String> fileToBeRemoved = (ArrayList<String>)
                toObject(s + "/.gitlet/References/fileToBeRemoved");
        Map<String, byte[]> stagingArea = (Map<String, byte[]>)
                toObject(s + "/.gitlet/References/stagingArea");
        Map<String, String> branchMap = (Map<String, String>)
                toObject(s + "/.gitlet/References/branchMap");
        Map<String, String> filesCommitedMap = (Map<String, String>)
                toObject(s + "/.gitlet/References/filesCommited");
        Map<String, String> trackedFilesMap = (Map<String, String>)
                toObject(s + "/.gitlet/References/trackedFiles");
        List<String> globalCommitsList = (List<String>)
                toObject(s + "/.gitlet/References/globalCommits");
        String head = (String) toObject(s + "/.gitlet/References/Head");

        //files inside of commit key: filename, value: sha1
        Map<String, String> filesInsideOfCommit = new HashMap<>();


        if (stagingArea.isEmpty() && fileToBeRemoved.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }

        //clear trackedFiles because you are creating the most recent commit right now\
        trackedFilesMap.clear();
        writeContents(new File(s + "/.gitlet/References/trackedFiles"), objToByte(trackedFilesMap));

        //for loop to get things inside of filesComitted
        // and then place that hashmap inside of commit object
        for (String key : stagingArea.keySet()) {
            byte[] blob = stagingArea.get(key);
            String sha1NameOfFileFromBlob = sha1(blob);
            //creates file inside of .gitlet
            toByteArray(blob, s + "/.gitlet/" + sha1NameOfFileFromBlob);
            //creating reference from filename to sha1nameofthisfile
            filesCommitedMap.put(key, sha1NameOfFileFromBlob);
            //putting files from stagingArea to a hashmap
            // that contains the files inside of the commmit object
            filesInsideOfCommit.put(key, sha1NameOfFileFromBlob);

            //Update and add all files to tracked files
            trackedFilesMap.put(key, sha1NameOfFileFromBlob);

        }
        writeContents(new File(s + "/.gitlet/References/filesCommited"),
                objToByte(filesCommitedMap));

        writeContents(new File(s + "/.gitlet/References/trackedFiles"),
                objToByte(trackedFilesMap));

        String parent = branchMap.get(head);
        CommitObject newCommit = new CommitObject((HashMap<String, String>)
                filesInsideOfCommit, message, parent, head);

        String uniqueNameofCommitObject = sha1(newCommit.message
                + newCommit.timeDate
                + newCommit.parent + newCommit.branchName);

        toByteArray(newCommit, s + "/.gitlet/" + uniqueNameofCommitObject);

        branchMap.put(head, uniqueNameofCommitObject);
        writeContents(new File(s + "/.gitlet/References/branchMap"), objToByte(branchMap));

        new File(s + "/.gitlet/References/stagingArea").delete();
        stagingArea = new HashMap<>();
        writeContents(new File(s + "/.gitlet/References/stagingArea"), objToByte(stagingArea));

        globalCommitsList.add(uniqueNameofCommitObject);
        writeContents(new File(s + "/.gitlet/References/globalCommits"),
                objToByte(globalCommitsList));

        new File(s + "/.gitlet/References/fileToBeRemoved").delete();
        fileToBeRemoved = new ArrayList<>();
        writeContents(new File(s + "/.gitlet/References/fileToBeRemoved"),
                objToByte(fileToBeRemoved));
    }
}
