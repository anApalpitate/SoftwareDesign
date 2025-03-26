import diagram.ClassDiagram;
import diagram.ClassDiagramGenerator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Main {
    public static void main(String[] args) throws IOException {
        ClassDiagramGenerator generator = new ClassDiagramGenerator();
        Path cwd = Paths.get(System.getProperty("user.dir")).resolve("./src/main/Main.java"); // 获取当前文件路径
        Path path = Paths.get("../../test/resources/lab2/DataTypes.java");  //本地测试时路径自行更换

        ClassDiagram diagram = generator.parse(cwd.resolve(path).normalize());
        String uml = diagram.generateUML();
        System.out.println(uml);

    }
}

