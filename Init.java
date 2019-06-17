package gitlet;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static gitlet.Utils.*;

public class Init extends Serialize {

    public static void init() throws java.io.IOException {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        /** This throw exception statement catches if a .gitlet respository already exists.
         * If it already exists, abort. */
        if (new File(s + "/.gitlet").exists()) {
            System.out.println("A gitlet version-control system "
                    + "already exists in the current directory.");
            return;
        }
        //creating all of the directories we need
        createFolder(s + "/.gitlet"); //creates folder where we save everything
        createFolder(s + "/.gitlet/References"); //creates List where we have all of the references


        //variables inside of references folder
        //Key: Filename, Value: Sha1 of contents of file
        Map<String, String> filesCommitedMap = new HashMap<>();
        //Key: Branch Name, Value: Sha1 of Commit Object
        Map<String, String> branchMapMap = new HashMap<>();
        //Key: filename, Value: Sha1 of blob of these files (same thing as filesCommited).
        Map<String, String> trackedFilesMap = new HashMap<>();
        List<String> globalCommitsList = new ArrayList<>(); //Contains sha1 of all commits
        //contains Key: Filename, Value: readContents (trying to add inside of FilesCommitedMap)
        Map<String, byte[]> stagingAreaMap = new HashMap<>();
        //Key: Filename, Value: Sha1 of contents of file
        List<String> fileToBeRemovedList = new ArrayList<>();
        String head = "master"; //starts as master because we only have one branch

        //creating Files
        toByteArray((Serializable) filesCommitedMap, s + "/.gitlet/References/filesCommited");
        toByteArray((Serializable) branchMapMap, s + "/.gitlet/References/branchMap");
        toByteArray((Serializable) trackedFilesMap, s + "/.gitlet/References/trackedFiles");
        toByteArray((Serializable) globalCommitsList, s + "/.gitlet/References/globalCommits");
        toByteArray((Serializable) stagingAreaMap, s + "/.gitlet/References/stagingArea");
        toByteArray((Serializable) fileToBeRemovedList,  s + "/.gitlet/References/fileToBeRemoved");
        toByteArray((Serializable) head,  s + "/.gitlet/References/Head");



        //creating first commit
        CommitObject firstCommit = new CommitObject(new HashMap<>(), "initial commit", null, head);

        //Sha1 contents of commmit
        String sha1offirstcommitfiles = Utils.sha1(firstCommit.filesCommitted.toString());
        String nameOfFirstCommit = Utils.sha1(firstCommit.message + firstCommit.timeDate
                + sha1offirstcommitfiles);
        toByteArray(firstCommit, s + "/.gitlet/" + nameOfFirstCommit);

        //adding firstCommit to branchMap
        branchMapMap.put(head, nameOfFirstCommit);
        writeContents(new File(s + "/.gitlet/References/branchMap"), objToByte(branchMapMap));

        //adding firstCommit to globalCommit
        globalCommitsList.add(nameOfFirstCommit);
        writeContents(new File(s + "/.gitlet/References/globalCommits"),
                objToByte(globalCommitsList));

    }

    private static boolean createFolder(String theFilePath) throws IOException {
        boolean result = false;

        File george = new File(theFilePath);
        if (george.exists()) {
            System.out.println("Folder already exists");
        } else {
            result = george.mkdirs();
        }
        return result;
    }
}
