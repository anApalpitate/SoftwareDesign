package diagram;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ClassDiagramGenerator {
    // 解析源文件路径，返回ClassDiagram对象
    public ClassDiagram parse(Path sourcePath) throws IOException {
        // 返回ClassDiagram对象
        File file = sourcePath.toFile();
        CompilationUnit root = StaticJavaParser.parse(file);
        return new ClassDiagram(root);
    }
}