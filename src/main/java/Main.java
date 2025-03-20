import diagram.ClassDiagram;
import diagram.ClassDiagramGenerator;

import java.io.IOException;
import java.nio.file.Paths;


public class Main {
    public static void main(String[] args) throws IOException {
        ClassDiagramGenerator generator = new ClassDiagramGenerator();
        ClassDiagram diagram = generator.parse(Paths.get("E:/Proj/software_group_31/src/test/resources/Implement.java"));
        String uml = diagram.generateUML();
        System.out.println(uml.contains("- signExtend(value: int, bits: int): int"));
        System.out.println(uml);
    }
}

