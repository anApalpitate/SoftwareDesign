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

    /**
     * 你应当在迭代二中实现这个方法
     *
     * @return 返回代码中的“坏味道”
     */
    public List<String> getCodeSmells() {
        return null;
    }

}