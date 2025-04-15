package diagram;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ClassDiagramGenerator {
    // 解析源文件路径，返回ClassDiagram对象
    public ClassDiagram parse(Path sourcePath) throws IOException {
        if (Files.isRegularFile(sourcePath) && sourcePath.toString().endsWith(".java")) {
            // 如果是单个.java文件，直接解析该文件
            return new ClassDiagram(sourcePath);
        } else if (Files.isDirectory(sourcePath)) {
            // 如果是目录，递归遍历目录下的所有.java文件
            List<Path> javaFiles = new ArrayList<>();
            collectJavaFiles(sourcePath, javaFiles);
            return createClassDiagramFromFiles(javaFiles);
        }
        throw new IllegalArgumentException("The provided path is neither a .java file nor a directory.");
    }

    // 递归收集目录下的所有.java文件
    private void collectJavaFiles(Path directory, List<Path> javaFiles) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path path : stream) {
                if (Files.isRegularFile(path) && path.toString().endsWith(".java")) {
                    javaFiles.add(path);
                } else if (Files.isDirectory(path)) {
                    collectJavaFiles(path, javaFiles);
                }
            }
        }
    }

    // 根据收集到的所有.java文件创建ClassDiagram
    private ClassDiagram createClassDiagramFromFiles(List<Path> javaFiles) {
        // 这里假设ClassDiagram有一个构造函数可以接受多个文件路径
        // 实际实现中可能需要根据具体情况调整
        return new ClassDiagram(javaFiles);
    }
}