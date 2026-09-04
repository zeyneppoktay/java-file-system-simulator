import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Stack;

public class FileSystemLoader {

    private static final String accessLevel = "USER";
    private static final String date = "01.01.2025";
    private static final String RootTheFirst = "RootTheFirst";

    public static DirectoryNode loadFileSystem(String filePath) throws IOException {
        try (Scanner scanner = new Scanner(new File(filePath))) {

            DirectoryNode stackNotEmpty = new DirectoryNode(RootTheFirst, date, 0, accessLevel);
            Stack<DirectoryNode> directoryStack = new Stack<>();
            directoryStack.push(stackNotEmpty);
            DirectoryNode realRoot = null;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) continue;

                int indentLevel = countSpaces(line);
                String trimmedLine = line.trim();


                if (trimmedLine.length() > 0 && trimmedLine.charAt(0) == '\\') {


                    String dirName = "";
                    for (int i = 1; i < trimmedLine.length(); i++) {
                        dirName += trimmedLine.charAt(i);
                    }
                    dirName = dirName.trim();

                    int length = dirName.length();
                    if (length >= 2 &&
                            dirName.charAt(length - 2) == '#' &&
                            dirName.charAt(length - 1) == '#') {

                        String cleaned = "";
                        for (int i = 0; i < length - 2; i++) {
                            cleaned += dirName.charAt(i);
                        }
                        dirName = cleaned.trim();
                    }

                    DirectoryNode newDir = new DirectoryNode(dirName, date, 0, accessLevel);

                    adjustDirectoryStack(directoryStack, indentLevel);
                    directoryStack.peek().addNode(newDir);
                    directoryStack.push(newDir);

                    if (realRoot == null)
                        realRoot = newDir;
                }

                else if (trimmedLine.contains("##")) {

                    String[] parts = trimmedLine.split("##");

                    String messyFileName;
                    String lastModifiedDate = date;
                    int size = 0;
                    String accLevel = accessLevel;

                    if (parts.length >= 4) {

                        messyFileName = parts[0].trim();
                        lastModifiedDate = parts[1].trim();

                        try {
                            size = Integer.parseInt(parts[2].trim());
                        } catch (NumberFormatException e) {
                            System.err.println("Warning: Invalid size for file " + parts[0].trim() + ". Set to 0.");
                            size = 0;
                        }

                        accLevel = parts[3].trim();

                    } else if (parts.length == 3) {
                        messyFileName = parts[0].trim();
                        String dateAndSize = parts[1].trim();
                        accLevel = parts[2].trim();

                        String[] dateSizeParts = dateAndSize.split("#");
                        if (dateSizeParts.length == 2) {
                            lastModifiedDate = dateSizeParts[0].trim();
                            try {
                                size = Integer.parseInt(dateSizeParts[1].trim());
                            } catch (NumberFormatException e) {
                                System.err.println("Warning: Invalid size for file " + parts[0].trim() + ". Set to 0.");
                                size = 0;
                            }
                        } else {
                            System.err.println("Warning: Could not parse date/size for line: " + trimmedLine);
                            continue;
                        }
                    } else {
                        System.err.println("Warning: Skipping malformed file line: " + trimmedLine);
                        continue;
                    }


                    String fileName = "";
                    String extension = "";
                    int dotIndex = messyFileName.lastIndexOf('.');

                    if (dotIndex == -1) {
                        fileName = messyFileName;
                        extension = "";
                    } else {
                        for (int i = 0; i < dotIndex; i++) {
                            fileName += messyFileName.charAt(i);
                        }
                        for (int i = dotIndex + 1; i < messyFileName.length(); i++) {
                            extension += messyFileName.charAt(i);
                        }
                    }

                    if (!accLevel.equals("USER") && !accLevel.equals("SYSTEM")) {
                        throw new IllegalArgumentException("Invalid access level: " + accLevel);
                    }

                    FileNode newFile = new FileNode(fileName, extension, lastModifiedDate, size, accLevel);

                    adjustDirectoryStack(directoryStack, indentLevel);
                    directoryStack.peek().addNode(newFile);
                } else {
                    System.err.println("Warning: Unrecognized line: " + trimmedLine);
                }
            }

            if (realRoot == null) {
                throw new IOException("File system file is empty or does not contain a root directory.");
            }
            return realRoot;
        }
    }

    private static void adjustDirectoryStack(Stack<DirectoryNode> directoryStack, int indentLevel) {
        while (directoryStack.size() > indentLevel + 1) {
            directoryStack.pop();
        }
    }

    private static int countSpaces(String line) {
        int level = 0;
        int spaceCount = 0;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '\t') {
                level++;
                spaceCount = 0;
            } else if (c == ' ') {
                spaceCount++;
                if (spaceCount == 4) {
                    level++;
                    spaceCount = 0;
                }
            } else {
                break;
            }
        }

        return level;
    }

}
