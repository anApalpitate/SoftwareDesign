package diagram;

import graph.ClassMap;
import graph.Graph;
import model.AbstractClassModel;
import model.ClassModel;

import java.util.*;

public class SmellAnalyzer {
    private final Graph graph;
    private final List<AbstractClassModel> classModels;
    private final List<String> output;

    SmellAnalyzer(List<AbstractClassModel> classModels, Graph graph) {
        this.classModels = classModels;
        this.graph = graph;
        this.output = new ArrayList<>();
    }

    public List<String> generateOutput() {
        ClassAnalyze();
        InheritanceTreeAnalyze();
        CircularDependencyAnalyze();
        return output;
    }

    private void ClassAnalyze() {
        /*识别God Class、Lazy Class 、Data Class*/
        List<String> GodClassBuffer = new ArrayList<>();
        List<String> LazyClassBuffer = new ArrayList<>();
        List<String> DataClassBuffer = new ArrayList<>();
        for (AbstractClassModel model : classModels) {
            if (model instanceof ClassModel classModel) {
                if (classModel.isGodClass())
                    GodClassBuffer.add("God Class: " + classModel.getName());
                if (classModel.isLazyClass())
                    LazyClassBuffer.add("Lazy Class: " + classModel.getName());
                if (classModel.isDataClass())
                    DataClassBuffer.add("Data Class: " + classModel.getName());
            }
        }
        output.addAll(GodClassBuffer);
        output.addAll(LazyClassBuffer);
        output.addAll(DataClassBuffer);
    }

    void InheritanceTreeAnalyze() {
        Set<String> allClasses = new HashSet<>();
        for (AbstractClassModel model : classModels) {
            allClasses.add(model.getName());
        }
        // 找出没有父类的类（即根节点）
        Set<String> roots = new HashSet<>(allClasses);
        for (String className : allClasses) {
            if (!graph.getInheritance(className).isEmpty()) {
                // 找爸爸，有爸爸，排除
                roots.remove(className);
            }
        }
        // Too Many Children 分析
        for (String className : allClasses) {
            HashSet<String> children = graph.getReverseInheritance(className);
            if (children.size() >= 10) {
                output.add("Too Many Children: " + className);
            }
        }
        // Inheritance Abuse 分析：从每个 root 开始 DFS
        for (String root : roots) {
            DfsCheckDepth(root, new LinkedList<>());
        }
    }

    void CircularDependencyAnalyze() {
        /*由于样例中至多有一个环，此处仅设计了支持输出最多一个环的算法*/
        ClassMap newGraph = graph.getMergedMap();
        LinkedList<String> path = new LinkedList<>();
        for (String cls : newGraph.getKeys()) {
            if (IsCycleByDFS(cls, newGraph, new HashSet<>(), new HashSet<>(), path)) {
                formatCycleOutput(path);
                return;
            }
        }
    }

    private void DfsCheckDepth(String className, LinkedList<String> path) {
        // 过深继承树
        path.add(className);
        if (path.size() >= 6) {
            StringBuilder sb = new StringBuilder("Inheritance Abuse: ");
            for (int i = 0; i < path.size(); i++) {
                sb.append(path.get(i));
                if (i != path.size() - 1) {
                    sb.append(" <|-- ");
                }
            }
            output.add(sb.toString());

        }
        for (String child : graph.getReverseInheritance(className)) {
            DfsCheckDepth(child, path);
        }
        path.removeLast();
    }

    private void formatCycleOutput(List<String> path) {
        // path: [..., X, ..., Y, Z, X]
        // 该函数中将path并格式化为 Circular Dependency: <A类名> <.. <B类名> <.. <C类名> <.. ... <.. <A类名>
        StringBuilder sb = new StringBuilder("Circular Dependency: ");
        int n = path.size();
        int startIdx = 0; //起始位置可以自行更改
        sb.append(path.get(startIdx));
        for (int i = 0; i < n; i++) {
            sb.append(" <.. ");
            //沿path向前
            startIdx = (startIdx + n - 1) % n;
            sb.append(path.get(startIdx));
        }
        output.add(sb.toString());
    }

    private boolean IsCycleByDFS(String node, ClassMap classMap,
                                 Set<String> visited, Set<String> stack, LinkedList<String> path) {
        if (stack.contains(node)) /*栈内节点重复，存在环*/
            return true;
        if (visited.contains(node))/*节点已访问，跳过*/
            return false;
        visited.add(node);//当前节点已访问
        stack.add(node);//当前节点入栈
        path.add(node);
        for (String adj : classMap.get(node)) {
            if (IsCycleByDFS(adj, classMap, visited, stack, path))
                return true;
        }
        stack.remove(node);
        path.removeLast();
        return false;
    }
}
