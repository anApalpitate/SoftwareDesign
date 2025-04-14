package diagram;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import graph.Graph;
import model.AbstractClassModel;
import model.ClassModel;
import model.EnumModel;
import model.InterfaceModel;
import utils.CommonUtil;
import utils.Factory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class ClassParser {
    protected Graph graph;
    protected List<AbstractClassModel> class_list;

    // 用于保存历史记录
    private final Deque<List<AbstractClassModel>> classHistory = new ArrayDeque<>();
    private final Deque<Graph> graphHistory = new ArrayDeque<>();

    ClassParser(File file) throws IOException {
        this.graph = new Graph();
        classListInit(file);
    }

    private void classListInit(File file) throws IOException {
        this.class_list = new ArrayList<>();
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

    public void saveSnapshot() {
        List<AbstractClassModel> classListCopy = new ArrayList<>();
        for (AbstractClassModel model : class_list) {
            if (model instanceof ClassModel) {
                classListCopy.add(new ClassModel((ClassModel) model));
            } else if (model instanceof InterfaceModel) {
                classListCopy.add(new InterfaceModel((InterfaceModel) model));
            } else if (model instanceof EnumModel) {
                classListCopy.add(new EnumModel((EnumModel) model));
            }
        }

        Graph graphCopy = new Graph(graph);
        classHistory.push(classListCopy);
        graphHistory.push(graphCopy);
    }

    public boolean undo() {
        if (classHistory.isEmpty() || graphHistory.isEmpty()) {
            return false;
        }
        class_list = classHistory.pop();
        graph = graphHistory.pop();
        graph.loadGraph();
        return true;
    }
}
