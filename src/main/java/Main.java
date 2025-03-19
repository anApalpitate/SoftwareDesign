import diagram.ClassDiagram;
import diagram.ClassDiagramGenerator;
import java.io.IOException;
import java.nio.file.Paths;


public class Main {
    public static void main(String[] args) throws IOException {
        ClassDiagramGenerator generator = new ClassDiagramGenerator();
        ClassDiagram diagram = generator.parse(Paths.get("path of java file"));
        System.out.println(diagram.generateUML());
    }
}

