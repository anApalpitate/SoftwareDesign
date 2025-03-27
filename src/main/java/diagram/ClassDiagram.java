package diagram;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class ClassDiagram {
    private ClassParser classParser;

    ClassDiagram(Path path) {
        File file = path.toFile();
        try {
            this.classParser = new ClassParser(file);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public String generateUML() {
        return classParser.generateUML();
    }
    
    public List<String> getCodeSmells() {
        return classParser.getCodeSmells();
    }

}