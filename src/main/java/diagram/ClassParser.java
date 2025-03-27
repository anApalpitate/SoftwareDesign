package diagram;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import graph.Graph;
import model.AbstractClassModel;
import utils.CommonUtil;
import utils.Factory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClassParser {
    private final CompilationUnit root;
    private List<AbstractClassModel> class_list;
    private Graph graph;

    ClassParser(File file) throws IOException {
        this.root = StaticJavaParser.parse(file);
        this.graph = new Graph();
        classListInit();
    }

    private void classListInit() {
        this.class_list = new ArrayList<>();
        for (BodyDeclaration decl : root.findAll(BodyDeclaration.class)) {
            if (decl instanceof ClassOrInterfaceDeclaration || decl instanceof EnumDeclaration) {
                AbstractClassModel classModel = Factory.classFactory(decl);
                class_list.add(classModel);
            }
        }
        CommonUtil.sortClassList(class_list);
    }

    public String generateUML() {
        StringBuilder sb = new StringBuilder();
        String prefix = "@startuml\n";
        String suffix = "@enduml\n";

        sb.append(prefix);
        for (AbstractClassModel classModel : class_list) {
            sb.append(classModel.generateString());
        }
        sb.append(graph.generateString());
        sb.append(suffix);
        return sb.toString();
    }

    public List<String> getCodeSmells() {
        return null;
    }


}
