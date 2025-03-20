import diagram.ClassDiagram;
import diagram.ClassDiagramGenerator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        // 指定要解析的 Java 源文件路径
        Path javaFilePath = Paths.get("src/test/resources/MultiInterface.java");

        try {
            // 解析 Java 文件
            ClassDiagramGenerator generator = new ClassDiagramGenerator();
            ClassDiagram classDiagram = generator.parse(javaFilePath);

            // 生成 UML 并打印
            String uml = classDiagram.generateUML();
            System.out.println(uml);

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}
