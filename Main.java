package gitlet;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static gitlet.Log.*;

/* Driver class for Gitlet, the tiny stupid version-control system.
   @author
*/
public class Main {
    /* Usage: java Main ARGS, where ARGS contains <COMMAND> <OPERAND> .... */
    public static void main(String... args) throws IOException, ClassNotFoundException {
        if (args.length > 4) {
            System.out.println("Incorrect commands");
            return;
        }
        if (args[0] == null) {
            System.out.println("Please enter a command");
            return;
        } else {
            switch (args[0]) {
                case "init":
                    if (args.length < 2) {
                        Init.init();
                        break;
                    }
                    System.out.println("Invalid second argument");
                    break;
                case "add":
                    Add.add(args[1]);
                    break;
                case "commit":
                    if (args[1].isEmpty()) {
                        System.out.println("Please enter a commit message.");
                    }
                    CommitMethod.commit(args[1]);
                    break;
                case "rm":
                    Remove.remove(args[1]);
                    break;
                case "checkout":
                    if (args.length == 2) {
                        CheckOutBranch.checkoutBranch(args[1]);
                    }
                    if (args.length == 4) {
                        if (args[2].equals("--")) {
                            if (args[1].length() < 40) {
                                Path currentRelativePath = Paths.get("");
                                String dir = currentRelativePath.toAbsolutePath().toString();
                                List<String> hashedCommitNames =
                                        Utils.plainFilenamesIn(dir + "/.gitlet");
                                for (int i = 0; i < hashedCommitNames.size(); i++) {
                                    if (hashedCommitNames.get(i)
                                            .toLowerCase().contains(args[1].toLowerCase())) {
                                        CheckOut.checkOutWithID(hashedCommitNames.get(i), args[3]);
                                    }
                                }
                                break;
                            }
                            CheckOut.checkOutWithID(args[1], args[3]);
                            break;
                        }
                        System.out.println("Incorrect operands.");
                        break;
                    }
                    if (args.length == 3) {
                        CheckOut.checkOutRecentFile(args[2]);
                    }
                    break;
                case "log":
                    Log.log();
                    break;
                case "global-log":
                    Log.globalLog();
                    break;
                case "find":
                    Find.find(args[1]);
                    break;
                case "status":
                    Status.status(); break;
                case "reset":
                    Reset2.reset2(args[1]); break;
                case "branch":
                    Branch.branch(args[1]); break;
                default:
                    System.exit(0);
            }
        }
    }
}



