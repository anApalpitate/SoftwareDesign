package diagram;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import model.ClassModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClassParser {
    private final CompilationUnit root;
    private final List<ClassModel> classModels;
    private final List<ClassModel> interfaces;

    /*TODO：添加联系类以处理依赖和关联:在ClassModel的每次处理后返回找到的relate,将relate在这里加入队列
     *  构件关系的队列以方便3.1，3.2两个图算法的处理（就可以直接变成写力扣了捏）
     */
    ClassParser(File file) throws IOException {
        this.classModels = new ArrayList<>();
        this.interfaces = new ArrayList<>();
        this.root = StaticJavaParser.parse(file);
        System.out.println(file.getAbsolutePath());
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
        //TODO：关系的输出方式待优化
        addInheritance(sb);
        addImplement(sb);

        String suffix = "@enduml\n";
        sb.append(suffix);

        return sb.toString();
    }

    /*TODO: 继承和实现关系待优化（需要进一步封装）*/
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
