package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gitlet.Serialize.objToByte;
import static gitlet.Utils.readContents;
import static gitlet.Utils.writeContents;


public class Add extends Serialize {

    public static void add(String file) throws IOException, ClassNotFoundException {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        File copy = new File(s + "/" + file);

        //things we need to retrieve


        Map<String, byte[]> staging = (Map<String, byte[]>)
                toObject(s + "/.gitlet/References/stagingArea");

        //need this to see if file is in filestobedeleted
        List<String> filesToBeDeleted = (ArrayList<String>)
                toObject(s + "/.gitlet/References/fileToBeRemoved");
        //need this to see if file added is on most recent commit
        HashMap<String, String> branchMap = (HashMap<String, String>)
                toObject(s + "/.gitlet/References/branchMap");

        //Need this to get where we are currently commiting
        String head = (String)
                toObject(s + "/.gitlet/References/Head");


        if (copy == null) {
            throw new IOException();
        }
        if (!copy.exists()) {
            System.out.println("File does not exist");
            return;
        }

        //If the file had been marked to be removed (see gitlet rm),
        // delete that mark before adding the file as usual.
        //After a new commit is created commit method does
        // not have to refresh filesToBeDeleted because:
        //rm specs-- this is the duty of remove

        if (filesToBeDeleted.contains(file)) {
            filesToBeDeleted.remove(file);
            writeContents(new File(s + "/.gitlet/References/fileToBeRemoved"),
                    objToByte(filesToBeDeleted));
        }

        //If the current working version of file is identical to something you have added dont add
        if (staging.containsKey(file)) {
            byte[] blobOfFileToBeAdded = Utils.readContents(new File(s + "/" + file));
            byte[] blobOfFileInStagingArea = staging.get(file);
            String sha1blobOfFileToBeAdded = Utils.sha1(blobOfFileToBeAdded);
            String sha1blobOfFileInStagingArea = Utils.sha1(blobOfFileInStagingArea);
            if (sha1blobOfFileInStagingArea.equals(sha1blobOfFileToBeAdded)) {
                System.out.println("File is already in staging area");
                return;
            }
        }
        //If the current working version of the file is identical to the version in the
        if (!branchMap.isEmpty()) {
            //getting most recent commit from branch
            String sha1NameOfRecentCommit = branchMap.get(head);
            CommitObject recentCommit = (CommitObject)
                    toObject(s + "/.gitlet/" + sha1NameOfRecentCommit); //gives recentCommit

            //checks if recentCommit contains File that we are trying to add
            if (recentCommit.filesCommitted.containsKey(file)) {
                //sha1 of recentCommit contens
                String sha1OfFileInRecentCommit = recentCommit.filesCommitted.get(file);
                //read contents of file given by user
                byte[] contentsOfFileGivenByUser = readContents(new File(s + "/" + file));
                //sha1 of file given by user
                String sha1OfFileToBeAdded = Utils.sha1(contentsOfFileGivenByUser);
                //compares if sha1 of files in recent commit is thesame as the contents of our file
                if (sha1OfFileToBeAdded.equals(sha1OfFileInRecentCommit)) {
                    //System.out.println("Do Not Stage, File is in recent commit");
                    return;
                }
            }
        }
        byte[] blob = Utils.readContents(new File(s + "/" + file));
        staging.put(file, blob);
        writeContents(new File(s + "/.gitlet/References/stagingArea"), objToByte(staging));
    }
}
