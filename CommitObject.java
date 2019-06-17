package gitlet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CommitObject implements java.io.Serializable {
    //File mapped to hashed File key: Filename, value:sha1 of contents of file
    Map<String, String> filesCommitted;
    String message;
    String timeDate;
    String parent; //sha1 of commit objects
    String branchName; //branch in which this commit is in
    //give it a name which is the contents sha1
    public CommitObject(HashMap<String, String> commitFiles,
                        String message, String parent, String branchName) {
        this.filesCommitted = commitFiles;
        this.message = message;
        this.parent = parent;
        this.branchName = branchName;

        long yourmilliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        Date resultdate = new Date(yourmilliseconds);
        //setting time
        this.timeDate = (sdf.format(resultdate));
    }
}
