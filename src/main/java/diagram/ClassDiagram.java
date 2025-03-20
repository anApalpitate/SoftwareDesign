package diagram;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import model.ClassModel;

import java.util.ArrayList;
import java.util.List;

public class ClassDiagram {
    private final CompilationUnit root;
    private final List<ClassModel> classModels;
    private final List<ClassModel> interfaces;


    ClassDiagram(CompilationUnit root) {
        this.interfaces = new ArrayList<>();
        this.classModels = new ArrayList<>();
        this.root = root;

        for (ClassOrInterfaceDeclaration classOrInterface : root.findAll(ClassOrInterfaceDeclaration.class)) {
            ClassModel classModel = new ClassModel(classOrInterface);
            if (classModel.isInterface())
                interfaces.add(classModel);
            else
                classModels.add(classModel);
        }
    }

    public String generateUML() {
        StringBuilder sb = new StringBuilder();
        String prefix = "@startuml\n";
        sb.append(prefix);

        for (ClassModel classModel : classModels) {
            sb.append(classModel.toString());
        }
        for (ClassModel interfaceModel : interfaces) {
            sb.append(interfaceModel.toString());
        }
        addInheritance(sb); //继承
        addImplement(sb); //实现

        String suffix = "@enduml\n";
        sb.append(suffix);

        return sb.toString();
    }

    private void addInheritance(StringBuilder sb) {
        root.findAll(ClassOrInterfaceDeclaration.class).forEach(classDecl -> {
            List<ClassOrInterfaceType> extendedTypes = classDecl.getExtendedTypes();
            for (ClassOrInterfaceType type : extendedTypes) {
                sb.append(type.getNameAsString()).append(" <|-- ").append(classDecl.getNameAsString()).append("\n");
            }
        });
    }

    private void addImplement(StringBuilder sb) {
        root.findAll(ClassOrInterfaceDeclaration.class).forEach(classDecl -> {
            List<ClassOrInterfaceType> implementedTypes = classDecl.getImplementedTypes();
            for (ClassOrInterfaceType type : implementedTypes) {
                sb.append(type.getNameAsString()).append(" <|.. ").append(classDecl.getNameAsString()).append("\n");
            }
        });
    }

}