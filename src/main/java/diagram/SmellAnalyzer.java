package diagram;

import graph.ClassMap;
import graph.Graph;
import model.AbstractClassModel;
import model.ClassModel;
import utils.AnalyzerUtil;

import java.util.*;

public class SmellAnalyzer {
    private final Graph graph;
    private final List<AbstractClassModel> classModels;
    private final List<String> output;
    private final AnalyzerUtil util;

    SmellAnalyzer(List<AbstractClassModel> classModels, Graph graph) {
        this.classModels = classModels;
        this.graph = graph;
        this.output = new ArrayList<>();
        this.util = new AnalyzerUtil();
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
            util.DfsCheckDepth(root, new LinkedList<>(), graph, output);
        }
    }

    void CircularDependencyAnalyze() {
        /*由于样例中至多有一个环，此处仅设计了支持输出最多一个环的算法*/
        ClassMap newGraph = graph.getMergedMap();
        LinkedList<String> path = new LinkedList<>();
        for (AbstractClassModel model : classModels) {
            if (util.IsCycleByDFS(model.getName(), newGraph, new HashSet<>(), new HashSet<>(), path)) {
                output.add(util.formatCycleOutput(path));
                return;
            }
        }
    }


}
