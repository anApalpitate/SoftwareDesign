package diagram;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import model.AbstractClassModel;
import utils.CommonUtil;
import utils.Factory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClassParser {
    private final CompilationUnit root;
    private final List<AbstractClassModel> classList;

    /*TODO：添加联系类以处理依赖和关联:在ClassModel的每次处理后返回找到的relate,将relate在这里加入队列
     *  构件关系的队列以方便3.1，3.2两个图算法的处理（就可以直接变成写力扣了捏）
     */
    ClassParser(File file) throws IOException {
        this.classList = new ArrayList<>();
        this.root = StaticJavaParser.parse(file);

        for (BodyDeclaration decl : root.findAll(BodyDeclaration.class)) {
            if (decl instanceof ClassOrInterfaceDeclaration || decl instanceof EnumDeclaration) {
                AbstractClassModel classModel = Factory.classFactory(decl);
                classList.add(classModel);
            }
        }
        CommonUtil.sortClassList(classList);
    }

    public String generateUML() {
        StringBuilder sb = new StringBuilder();
        String prefix = "@startuml\n";
        sb.append(prefix);
        for (AbstractClassModel classModel : classList) {
            sb.append(classModel.generateString());
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
