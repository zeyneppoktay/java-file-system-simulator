public abstract class FileSystemNode {

    protected String name;
    protected String lastModifiedDate;
    protected int size;
    protected String accessLevel;
    protected DirectoryNode parent;

    public FileSystemNode(String name, String lastModifiedDate, int size, String accessLevel) {
        this.name = name;
        this.lastModifiedDate = lastModifiedDate;
        this.size = size;
        this.accessLevel = accessLevel;
        this.parent = null;
    }


    public String getName() {
        return name; }
    public String getLastModifiedDate() {
        return lastModifiedDate; }
    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate; }
    public int getSize() {
        return size; }
    protected void setSize(int size) {
        this.size = size; }
    public String getAccessLevel() {
        return accessLevel; }
    protected void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel; }
    public void setParent(DirectoryNode parent) {
        this.parent = parent; }
    public DirectoryNode getParent() {
        return parent; }

    public String getPath() {
        if (this.getParent() == null) {
            return this.getName();
        } else {
            return this.getParent().getPath() + "/" + this.getName();
        }
    }
    public void ParentUpdateLastModified() {
        if (this.parent == null) return;
        this.parent.recalculateLastModifiedFromChildren();
        this.parent.ParentUpdateLastModified();
    }

    public void ParentUpdateAccessLevel() {
        if (this.parent == null) return;
        this.parent.recalculateAccessLevelFromChildren();
        this.parent.ParentUpdateAccessLevel();
    }

    public void ParentUpdateSize() {
        if (this.parent == null) return;
        this.parent.recalculateSizeFromChildren();
        this.parent.ParentUpdateSize();
    }
  public void displaySingleLine() {
      String type = (this instanceof DirectoryNode) ? "[DIR]" : "[FILE]";

      String fullName = getName();
      if (this instanceof FileNode) {
          FileNode f = (FileNode) this;
          if (f.getExtension() != null && !f.getExtension().isEmpty()) {
              fullName += "." + f.getExtension();
          }
      }

      System.out.println(type + " " + fullName +
              " | Size: " + getSize() +
              " | Last Modified: " + getLastModifiedDate() +
              " | Access: " + getAccessLevel());
  }

    protected void recalculateLastModifiedFromChildren() {}
    protected void recalculateAccessLevelFromChildren() {}
    protected void recalculateSizeFromChildren() {}
    public abstract void display();
    public abstract void removeNode(FileSystemNode node);
}
