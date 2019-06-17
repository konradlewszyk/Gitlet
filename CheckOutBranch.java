package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gitlet.Utils.*;

public class CheckOutBranch extends Serialize {

    public static void checkoutBranch(String branchNam) throws IOException, ClassNotFoundException {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        //things we need
        Map<String, String> branchMap = (HashMap<String, String>)
                toObject(s + "/.gitlet/References/branchMap");
        String hEAD = (String) toObject(s + "/.gitlet/References/Head");
        Map<String, byte[]> stagingArea = (Map<String, byte[]>)
                toObject(s + "/.gitlet/References/stagingArea");
        Map<String, String> trackedFiles = (Map<String, String>)
                toObject(s + "/.gitlet/References/trackedFiles");
        //Failure Cases

        if (!branchMap.containsKey(branchNam)) {
            System.out.println("No such branch exists.");
            return;
        }

        if (hEAD == branchNam) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        String currentCommitFromHeadSha1 = branchMap.get(hEAD);
        //current commit from Head
        CommitObject currentCommitFromHead = (CommitObject)
                toObject(s + "/.gitlet/" + currentCommitFromHeadSha1);
        //files from current commit of head
        Map<String, String> filesFromCurrentCommitOfHead = currentCommitFromHead.filesCommitted;
        //arraylist of files in working directory
        File n = new File(s + "/.gitlet/");
        List<String> workingDirectoryFiles = plainFilenamesIn(n.getParentFile());

        if (filesFromCurrentCommitOfHead.isEmpty() && !workingDirectoryFiles.isEmpty()) {
            System.out.println("There is an untracked "
                    + "file in the way; delete it or add it first.");
            return;
        }
        int i = 0;
        while (i < workingDirectoryFiles.size()) {
            if (filesFromCurrentCommitOfHead.containsKey(workingDirectoryFiles.get(i))) {
                String workingDirectoryFile = workingDirectoryFiles.get(i);
                byte[] blobWorkingDirectoryFile = readContents(new File(s + "/"
                        + workingDirectoryFile));
                String sha1ofWorkingDirectoryFile = sha1(blobWorkingDirectoryFile);
                if (!sha1ofWorkingDirectoryFile.
                        equals(filesFromCurrentCommitOfHead.get(workingDirectoryFile))) {
                    System.out.println("There is an untracked "
                            + "file in the way; delete it or add it first.");
                    return;
                }
            }
            i++;
        }
        String sha1OfRecentCommitFromBranchGiven = branchMap.get(branchNam);
        CommitObject commitFromBranch = (CommitObject)
                toObject(s + "/.gitlet/" + sha1OfRecentCommitFromBranchGiven);
        //Files in the commit at the HEAD of the given branch
        for (Map.Entry<String, String> entry : commitFromBranch.filesCommitted.entrySet()) {
            String filename = entry.getKey();
            String sha1offile = entry.getValue();
            String sha1ofBlobofFilename = entry.getValue();
            byte[] contentsOfsha1BlobOfFilename = (byte[])
                    toObject(s + "/.gitlet/" + sha1ofBlobofFilename);
            File overriddenFile = new File(s + "/" + filename);
            writeContents(overriddenFile, contentsOfsha1BlobOfFilename);
            CheckOut.checkOutWithID(sha1OfRecentCommitFromBranchGiven, filename);
        }
        //deleting trackedFiles not present in checked out branch
        for (Map.Entry<String, String> entry : currentCommitFromHead.filesCommitted.entrySet()) {
            String filename = entry.getKey();
            if (!commitFromBranch.filesCommitted.containsKey(filename)) {
                //trackedFiles.replace("", "");
                restrictedDelete(s + "/" + filename);
            }
        }
        stagingArea.clear();
        writeContents(new File(s + "/.gitlet/References/stagingArea"), objToByte(stagingArea));
        hEAD = branchNam;
        writeContents(new File(s + "/.gitlet/References/Head"), objToByte(hEAD));
    }
}


