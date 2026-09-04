import java.util.ArrayList;
import java.util.List;

public class FileSystemManager {

    private DirectoryNode root;
    private DirectoryNode curr;

    public FileSystemManager(DirectoryNode root) {
        this.root = root;
        this.curr = root;
    }

    public DirectoryNode getRoot() { return root; }
    public DirectoryNode getCurrent() { return curr; }
    public boolean cdUp() {
        if (curr.getParent() == null)
            return false;
        if (curr.getParent().getName().equals("RootTheFirst"))
            return false;

        curr = curr.getParent();
        return true;
    }

    public boolean cdTo(String dirName, boolean EnterSystemIsIt) {
        FileSystemNode node = curr.getChild(dirName);
        if (node == null || !(node instanceof DirectoryNode))
            return false;
        DirectoryNode dir = (DirectoryNode) node;


        if ("SYSTEM".equals(dir.getAccessLevel()) && !EnterSystemIsIt) {

            return false;
        }
        curr = dir;
        return true;
    }

    public boolean addDirectory(String name) {
        if (name == null || name.trim().isEmpty())
            return false;
        if (curr.getChild(name) != null)
            return false;

        if ("SYSTEM".equals(curr.getAccessLevel()))
            return false;
        DirectoryNode newDir = new DirectoryNode(name, "01.01.2025", 0, "USER");
        curr.addNode(newDir);
        return true;
    }
  public boolean addFile(String messyFileName, String lastModified, int size, String accessLevel) {

      if (!isValidUserLevel(accessLevel))
          return false;
      if ("SYSTEM".equals(curr.getAccessLevel()))
          return false;
      if (messyFileName == null || messyFileName.trim().isEmpty())
          return false;

      int dotPos = -1;
      for (int i = 0; i < messyFileName.length(); i++) {
          if (messyFileName.charAt(i) == '.') {
              dotPos = i;

          }
      }

      String fileName = "";
      String extension = "";

      if (dotPos == -1) {

          fileName = messyFileName;
          extension = "";
      } else {

          char[] charsName = new char[dotPos];
          for (int i = 0; i < dotPos; i++) {
              charsName[i] = messyFileName.charAt(i);
          }
          fileName = new String(charsName);


          int extLength = messyFileName.length() - (dotPos + 1);
          char[] extChars = new char[extLength];

          int j = 0;
          for (int i = dotPos + 1; i < messyFileName.length(); i++) {
              extChars[j] = messyFileName.charAt(i);
              j++;
          }
          extension = new String(extChars);
      }

      String checkName = fileName;
      if (curr.getChild(checkName) != null)
          return false;

      FileNode f = new FileNode(fileName, extension, lastModified, size, accessLevel);
      curr.addNode(f);

      return true;
  }

    public boolean deleteChild(String name) {
        FileSystemNode node = curr.getChild(name);
        if (node == null)
            return false;
        if ("SYSTEM".equals(node.getAccessLevel()))
            return false;

        if (node instanceof DirectoryNode) {
            if (containsSystem((DirectoryNode) node))
                return false;
            if (node == root) return false;
        }

        curr.removeNode(node);
        return true;
    }

    private boolean containsSystem(DirectoryNode dir) {
        for (FileSystemNode child : dir.getChildren()) {
            if ("SYSTEM".equals(child.getAccessLevel()))
                return true;
            if (child instanceof DirectoryNode) {
                if (containsSystem((DirectoryNode) child))
                    return true;
            }
        }
        return false;
    }

    public List<FileSystemNode> searchByName(String name) {
        List<FileSystemNode> results = new ArrayList<>();
        searchByNameRecursive(curr, name, results);
        return results;
    }

    private void searchByNameRecursive(DirectoryNode dir, String name, List<FileSystemNode> out) {
        for (FileSystemNode child : dir.getChildren()) {
            if (child.getName().equals(name)) out.add(child);
            if (child instanceof DirectoryNode) searchByNameRecursive((DirectoryNode) child, name, out);
        }
    }


    public List<FileNode> searchByExtension(String extension) {
        List<FileNode> results = new ArrayList<>();
        searchByExtensionRecursive(curr, extension, results);
        return results;
    }

    private void searchByExtensionRecursive(DirectoryNode dir, String ext, List<FileNode> out) {
        for (FileSystemNode child : dir.getChildren()) {
            if (child instanceof FileNode) {
                FileNode f = (FileNode) child;
                if (f.getExtension().equals(ext)) out.add(f);
            } else if (child instanceof DirectoryNode) {
                searchByExtensionRecursive((DirectoryNode) child, ext, out);
            }
        }
    }


    public FileSystemNode searchInCurrentDirectoryO1(String name) {
        return curr.getChild(name);
    }

   public void listCurrentContents() {
       System.out.println("--- Content List ---");
       boolean hasChildren = false;

       for (FileSystemNode child : curr.getChildren()) {
           hasChildren = true;


           if (child instanceof DirectoryNode) {
               System.out.println("[DIR] " + child.getName()
                       + " | Size: " + child.getSize()
                       + " | Last Modified: " + child.getLastModifiedDate()
                       + " | Access: " + child.getAccessLevel());
           }

           else if (child instanceof FileNode) {
               FileNode f = (FileNode) child;
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

       if (!hasChildren) {
           System.out.println("(Directory empty.)");
       }
       System.out.println("--------------------");
   }

    public static boolean isValidUserLevel(String accessLevel) {
        return "USER".equals(accessLevel) || "SYSTEM".equals(accessLevel);
    }
}