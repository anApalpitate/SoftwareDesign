// package diagram;

// import com.github.javaparser.StaticJavaParser;
// import com.github.javaparser.ast.CompilationUnit;
// import com.github.javaparser.ast.body.BodyDeclaration;
// import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
// import com.github.javaparser.ast.body.EnumDeclaration;
// import graph.Graph;
// import model.AbstractClassModel;
// import utils.CommonUtil;
// import utils.Factory;

// import java.io.File;
// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.List;

// public class ClassParser {
//     private final Graph graph;
//     private List<AbstractClassModel> class_list;

//     ClassParser(File file) throws IOException {
//         this.graph = new Graph();
//         classListInit(file);
//     }

//     private void classListInit(File file) throws IOException {
//         this.class_list = new ArrayList<>();
//         CompilationUnit root = StaticJavaParser.parse(file);
//         for (BodyDeclaration decl : root.findAll(BodyDeclaration.class)) {
//             if (decl instanceof ClassOrInterfaceDeclaration || decl instanceof EnumDeclaration) {
//                 /*采用简单工厂方法得到抽象类对应的具体类*/
//                 AbstractClassModel classModel = Factory.classFactory(decl);
//                 class_list.add(classModel);
//             }
//         }
//         CommonUtil.sortClassList(class_list);
//     }

//     /*UML 类部分输出*/
//     public String generateUML() {
//         StringBuilder sb = new StringBuilder();
//         String prefix = "@startuml\n";
//         String suffix = "@enduml\n";

//         sb.append(prefix);
//         for (AbstractClassModel classModel : class_list) {
//             sb.append(classModel.generateString());
//         }
//         sb.append(graph.generateString());
//         sb.append(suffix);
//         return sb.toString();
//     }

//     /*输出坏味道*/
//     public List<String> getCodeSmells() {
//         SmellAnalyzer analyzer = new SmellAnalyzer(class_list, graph);
//         return analyzer.generateOutput();
//     }


// }

// pzj-0413
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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ClassParser {
    private final Graph graph;
    private List<AbstractClassModel> class_list;

    // 支持单个文件的构造函数
    ClassParser(File file) throws IOException {
        this.graph = new Graph();
        this.class_list = new ArrayList<>();
        classListInit(file);
    }

    // 支持多个文件的构造函数
    ClassParser(List<Path> paths) throws IOException {
        this.graph = new Graph();
        this.class_list = new ArrayList<>();
        for (Path path : paths) {
            File file = path.toFile();
            classListInit(file);
        }
        CommonUtil.sortClassList(class_list);
    }

    private void classListInit(File file) throws IOException {
        // this.class_list = new ArrayList<>();
        CompilationUnit root = StaticJavaParser.parse(file);
        for (BodyDeclaration decl : root.findAll(BodyDeclaration.class)) {
            if (decl instanceof ClassOrInterfaceDeclaration || decl instanceof EnumDeclaration) {
                /*采用简单工厂方法得到抽象类对应的具体类*/
                AbstractClassModel classModel = Factory.classFactory(decl);
                class_list.add(classModel);
            }
        }
        CommonUtil.sortClassList(class_list);
    }

    /*UML 类部分输出*/
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

    /*输出坏味道*/
    public List<String> getCodeSmells() {
        SmellAnalyzer analyzer = new SmellAnalyzer(class_list, graph);
        return analyzer.generateOutput();
    }


}
