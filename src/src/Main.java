import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scn = new Scanner(System.in);
        DirectoryNode root;
        try {
            root = FileSystemLoader.loadFileSystem("filesystem.txt");
        } catch (IOException e) {
            System.err.println("filesystem.txt can not found: " + e.getMessage());
            return;
        }

        FileSystemManager fsm = new FileSystemManager(root);
        System.out.println("File system uploaded. Root: " + root.getName());

        while (true) {
            System.out.println("\n--- Current: " + fsm.getCurrent().getPath() + " ---");
            System.out.println("1- Changing current directory");
            System.out.println("2- Go to lower directory (cd dirname)");
            System.out.println("3- Add new directory");
            System.out.println("4- Add new file");
            System.out.println("5- Delete child (file/directory)");
            System.out.println("6- Search by name (recursive)");
            System.out.println("7- Search by extension (recursive)");
            System.out.println("8- Search in current directory (O(1))");
            System.out.println("9- List contents");
            System.out.println("0- Exit");
            System.out.print("Choose: ");
            String choice = scn.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("Changing current directory:");
                    System.out.println("0 - Go to the upper directory");

                    int i = 1;
                    for (FileSystemNode child : fsm.getCurrent().getChildren()) {
                        if (child instanceof DirectoryNode) {
                            System.out.println(i + " - " + child.getName());
                            i++;
                        }
                    }

                    System.out.print("Choose: ");
                    String sec = scn.nextLine().trim();

                    if (sec.equals("0")) {
                        if (!fsm.cdUp()) System.out.println("Can not go to the upper directory (already at root).");
                        else System.out.println("Moved to upper directory.");
                        break;
                    }

                    try {
                        int index = Integer.parseInt(sec);
                        int j = 1;
                        String selected = null;

                        for (FileSystemNode child : fsm.getCurrent().getChildren()) {
                            if (child instanceof DirectoryNode) {
                                if (j == index) {
                                    selected = child.getName();
                                    break;
                                }
                                j++;
                            }
                        }

                        if (selected == null) {
                            System.out.println("Invalid selection.");
                            break;
                        }


                        DirectoryNode d = (DirectoryNode) fsm.getCurrent().getChild(selected);
                        boolean entr = true;
                        if (d.getAccessLevel().equals("SYSTEM")) {
                            System.out.print("This is a SYSTEM directory. Do you want to enter? (Y/N): ");
                            String ans = scn.nextLine().trim().toUpperCase();
                            entr = ans.equals("Y");
                        }

                        if (!fsm.cdTo(selected, entr)) {
                            System.out.println(" Could not change directory.");
                        } else {
                            System.out.println("New current directory: " + fsm.getCurrent().getName());
                        }

                    } catch (Exception e) {
                        System.out.println(" You must enter a numeric value.");
                    }
                    break;

                case "2":
                    System.out.print("Target directory name: ");
                    String dname = scn.nextLine().trim();
                    FileSystemNode node = fsm.searchInCurrentDirectoryO1(dname);

                    if (node == null || !(node instanceof DirectoryNode)) {
                        System.out.println("No such subdirectory.");
                        break;
                    }

                    DirectoryNode dir = (DirectoryNode) node;
                    boolean cdSuccess = false;

                    if ("SYSTEM".equals(dir.getAccessLevel())) {
                        System.out.print("This directory is SYSTEM access. Continue? (Y/N): ");
                        String yn = scn.nextLine().trim();
                        if (yn.equalsIgnoreCase("y")) {
                            cdSuccess = fsm.cdTo(dname, true);
                            if (cdSuccess) System.out.println(" Entered SYSTEM directory.");
                        } else {
                            System.out.println("Entry cancelled.");
                        }
                    } else {
                        cdSuccess = fsm.cdTo(dname, false);
                        if (cdSuccess) System.out.println("Entered subdirectory.");
                    }
                    if (!cdSuccess && !"SYSTEM".equals(dir.getAccessLevel())) {
                        System.out.println("Directory change failed.");
                    }
                    break;
                case "3":
                    System.out.print("New directory name: ");
                    String newDir = scn.nextLine().trim();
                    if (fsm.addDirectory(newDir)) System.out.println(" Directory created: " + newDir);
                    else System.out.println("Directory creation failed (name conflict or SYSTEM directory).");
                    break;
                case "4":
                    System.out.print("File name (ex: file.txt) ");
                    String fname = scn.nextLine().trim();
                    System.out.print("Last modified (gg.aa.yyyy): ");
                    String lmd = scn.nextLine().trim();
                    System.out.print("Size (integer): ");
                    int size;
                    try {
                        size = Integer.parseInt(scn.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid size.");
                        break;
                    }
                    System.out.print("Access (USER/SYSTEM): ");
                    String acc = scn.nextLine().trim().toUpperCase();

                    if (!FileSystemManager.isValidUserLevel(acc)) {
                        System.out.println("Invalid access level.");
                        break;
                    }

                    if (fsm.addFile(fname, lmd, size, acc)) System.out.println(" File created: " + fname);
                    else System.out.println("Cannot delete (not found, SYSTEM, or contains SYSTEM items).");
                    break;
                case "5":
                    System.out.print("Name of child to delete: ");
                    String del = scn.nextLine().trim();
                    if (fsm.deleteChild(del)) System.out.println("Deleted: " + del);
                    else System.out.println("Cannot delete (not found, SYSTEM, or contains SYSTEM items).");
                    break;
                case "6":
                    System.out.print("Name to search: ");
                    String sname = scn.nextLine().trim();
                    List<FileSystemNode> res = fsm.searchByName(sname);
                    if (res.isEmpty()) System.out.println("Not found");
                    else {
                        System.out.println("Found files:");
                        for (FileSystemNode r : res) {
                            System.out.println("--> " + r.getPath());
                        }
                    }
                    break;
                case "7":
                    System.out.print("Extension to search: (ex: pdf): ");
                    String ext = scn.nextLine().trim();
                    List<FileNode> fres = fsm.searchByExtension(ext);
                    if (fres.isEmpty()) System.out.println("Not found");
                    else {
                        System.out.println("Found files:");
                        for (FileNode f : fres) System.out.println("-> " + f.getPath() + " (" + f.getSize() + " bytes)");
                    }
                    break;
                case "8":
                    if ("SYSTEM".equals(fsm.getCurrent().getAccessLevel())) {
                        System.out.print("This directory is SYSTEM. Do you want to perform search? (Y/N): ");
                        String yn = scn.nextLine().trim();
                        if (!yn.equalsIgnoreCase("y")) {
                            System.out.println("Search cancelled.");
                            break;
                        }
                    }

                    System.out.print("Name to search (current directory):  ");
                    String q = scn.nextLine().trim();

                    
                    String searchKey = "";
                    int lastDot = -1;


                    for (int z = 0; z < q.length(); z++) {
                        if (q.charAt(z) == '.') {
                            lastDot = z;
                        }
                    }

                    if (lastDot == -1) {
                        searchKey = q;
                    } else {

                        for (int z = 0; z < lastDot; z++) {
                            searchKey += q.charAt(z);
                        }
                    }



                    FileSystemNode quick = fsm.searchInCurrentDirectoryO1(searchKey);


                    if (quick == null) {
                        System.out.println("Not found.");
                    } else {
                        System.out.println("Found files (O(1)):");

                        if (quick instanceof DirectoryNode) {
                            System.out.println("[DIR] " + quick.getName()
                                    + " | Size: " + quick.getSize()
                                    + " | Last Modified: " + quick.getLastModifiedDate()
                                    + " | Access: " + quick.getAccessLevel());
                        } else if (quick instanceof FileNode) {
                            FileNode f = (FileNode) quick;
                            String fullName = f.getName();
                            if (f.getExtension() != null && !f.getExtension().isEmpty()) {
                                fullName = fullName + "." + f.getExtension();
                            }
                            System.out.println("[FILE] " + fullName
                                    + " | Size: " + f.getSize()
                                    + " | Last Modified: " + f.getLastModifiedDate()
                                    + " | Access: " + f.getAccessLevel());
                        }
                    }

                    break;
                case "9":
                    if ("SYSTEM".equals(fsm.getCurrent().getAccessLevel())) {
                        System.out.print("This directory is SYSTEM. Do you want to list contents? (Y/N): ");
                        String yn = scn.nextLine().trim();
                        if (!yn.equalsIgnoreCase("y")) {
                            System.out.println("Listing cancelled.");
                            break;
                        }
                    }
                    fsm.listCurrentContents();
                    break;
                case "17":
                    System.out.println("Byeee");
                    scn.close();
                    return;
                default:
                    System.out.println("Invalid selection.");
            }
        }
    }

}