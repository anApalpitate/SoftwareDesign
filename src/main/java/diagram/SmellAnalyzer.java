package diagram;

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
    private void dfsCheckInheritanceDepth(String className, LinkedList<String> path) {
//        过深的继承树
//        if (depth >= 5) {
//            output.add("Inheritance Abuse: " + className);
//        }
//
//        for (String child : Graph.getReverseInheritance(className)) {
//            dfsCheckInheritanceDepth(child, depth + 1);
//        }
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

        for (String child : Graph.getReverseInheritance(className)) {
            dfsCheckInheritanceDepth(child, path);
        }

        path.removeLast();
    }

    private void classAnalyze() {
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

    void inheritanceTreeAnalyze() {
        //TODO:完成继承树的分析
        /*
         * Inheritance Abuse 思路: 对inheritanceMap做DFS,需要记录一条最长路径以便输出到smell
         *Too Many Children 思路:对
         * 得到的List<String>加入output
         */
        Set<String> allClasses = new HashSet<>();
        for (AbstractClassModel model : classModels) {
            allClasses.add(model.getName());
        }

        // 找出没有父类的类（即根节点）
        Set<String> roots = new HashSet<>(allClasses);
        for (String className : allClasses) {
            if (!Graph.getInheritance(className).isEmpty()) {
                // 找爸爸，有爸爸，排除
                roots.remove(className);
            }
        }

        // Too Many Children 分析
        for (String className : allClasses) {
            HashSet<String> children = Graph.getReverseInheritance(className);
            if (children.size() >= 10) {
                output.add("Too Many Children: " + className);
            }
        }

        // Inheritance Abuse 分析：从每个 root 开始 DFS
        for (String root : roots) {
            dfsCheckInheritanceDepth(root, new LinkedList<>());
        }
    }



    void CircularDependencyAnalyze() {
        //TODO:完成循环依赖分析
        /*
         * 对图进行拓扑排序
         * 得到的List<String>或者String加入output
         */
        Map<String, List<String>> graph = new HashMap<>();

        Set<String> allClasses = new HashSet<>();
        for (AbstractClassModel model : classModels) {
            allClasses.add(model.getName());
        }

        for (String cls : allClasses) {
            List<String> deps = new ArrayList<>();
            deps.addAll(Graph.getInheritance(cls));
            deps.addAll(Graph.getImplementation(cls));
            deps.addAll(Graph.getAssociation(cls));
            deps.addAll(Graph.getDependency(cls));
            graph.put(cls, deps);
        }

        Set<String> visited = new HashSet<>();
        Set<String> inStack = new HashSet<>();
        LinkedList<String> path = new LinkedList<>();

        for (String cls : allClasses) {
            if (dfs_isCycle(cls, graph, visited, inStack, path)) {
                formatCycleOutput(path);
                return;
            }
        }
    }
    private void formatCycleOutput(List<String> path) {
        // path: [..., X, ..., Y, Z, X] 需要裁剪成环并格式化为 Circular Dependency: <A类名> <.. <B类名> <.. <C类名> <.. ... <.. <A类名>
        String start = path.get(path.size() - 1);
        int startIdx = path.indexOf(start);

        List<String> cycle = path.subList(startIdx, path.size());
        StringBuilder sb = new StringBuilder("Circular Dependency: ");

        for (int i =cycle.size()-1; i>=0; i--) {
            sb.append(cycle.get(i));
            if (i !=0) sb.append(" <.. ");
        }
        output.add(sb.toString());
    }
    private boolean dfs_isCycle(String current, Map<String, List<String>> graph,
                        Set<String> visited, Set<String> inStack, LinkedList<String> path) {
        if (inStack.contains(current)) {
            path.add(current);
            return true;
        }
        if (visited.contains(current)) return false;

        visited.add(current);
        inStack.add(current);
        path.add(current);

        for (String neighbor : graph.getOrDefault(current, List.of())) {
            if (dfs_isCycle(neighbor, graph, visited, inStack, path)) return true;
        }

        inStack.remove(current);
        path.removeLast();
        return false;
    }

    public List<String> generateOutput() {
        classAnalyze();
        CircularDependencyAnalyze();
        inheritanceTreeAnalyze();
        return output;
    }

}
