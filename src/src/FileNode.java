public class FileNode extends FileSystemNode {

    private String extension;


    public FileNode(String name, String extension, String lastModifiedDate, int size, String accessLevel) {
        super(name, lastModifiedDate, size, accessLevel);
        this.extension = extension;
    }

    public String getExtension() { return extension; }

    @Override
    public void removeNode(FileSystemNode node) {

    }

    @Override
    public void display() {
        String fullName = extension.isEmpty() ? name : name + "." + extension;
        System.out.println("[FILE] " + fullName +
                " | Size: " + size +
                " | Last Modified: " + lastModifiedDate +
                " | Access: " + accessLevel);
    }
}
