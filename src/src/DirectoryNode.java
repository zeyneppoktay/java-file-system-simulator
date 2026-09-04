import java.util.*;

public class DirectoryNode extends FileSystemNode {

    private List<FileSystemNode> children;
    private Map<String, FileSystemNode> childMap;
    public DirectoryNode(String name, String lastModifiedDate, int size, String accessLevel) {
        super(name, lastModifiedDate, size, accessLevel);
        this.children = new ArrayList<>();
        this.childMap = new HashMap<>();
    }

    public List<FileSystemNode> getChildren() { return children; }
    public FileSystemNode getChild(String name) { return childMap.get(name); }

    public void addNode(FileSystemNode node) {
        node.setParent(this);
        children.add(node);
        childMap.put(node.getName(), node);


        recalculateSizeFromChildren();
        recalculateAccessLevelFromChildren();
        recalculateLastModifiedFromChildren();

        ParentUpdateSize();
        ParentUpdateAccessLevel();
        ParentUpdateLastModified();
    }

    @Override
    public void removeNode(FileSystemNode node) {
        if (node == null) return;
        children.remove(node);
        childMap.remove(node.getName());

        recalculateSizeFromChildren();
        recalculateAccessLevelFromChildren();
        recalculateLastModifiedFromChildren();

        ParentUpdateSize();
        ParentUpdateAccessLevel();
        ParentUpdateLastModified();
    }

    @Override
    protected void recalculateSizeFromChildren() {
        int total = 0;
        for (FileSystemNode child : children) {
            total += child.getSize();
        }
        this.setSize(total);
    }

    @Override
    protected void recalculateAccessLevelFromChildren() {
        boolean allSystem = true;
        for (FileSystemNode child : children) {
            if (!"SYSTEM".equals(child.getAccessLevel())) {
                allSystem = false;
                break;
            }
        }
        this.setAccessLevel(allSystem ? "SYSTEM" : "USER");
    }
    @Override
    public void displaySingleLine() {
        System.out.println("[DIR] " + name + " | Size: " + size +
                " | Last Modified: " + lastModifiedDate + " | Access: " + accessLevel);
    }

    @Override
    protected void recalculateLastModifiedFromChildren() {
        String latest = this.lastModifiedDate;
        for (FileSystemNode child : children) {
            if (child.getLastModifiedDate().compareTo(latest) > 0) {
                latest = child.getLastModifiedDate();
            }
        }
        this.setLastModifiedDate(latest);
    }

    @Override
    public void display() {
        System.out.println("[DIR] " + name + " | Size: " + size +
                " | Last Modified: " + lastModifiedDate + " | Access: " + accessLevel);
        for (FileSystemNode child : children) {
            child.display();
        }
    }
}
