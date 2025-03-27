package diagram;

import graph.Graph;
import model.AbstractClassModel;
import model.ClassModel;

import java.util.ArrayList;
import java.util.List;

public class SmellAnalyzer {
    private final Graph graph;
    private final List<AbstractClassModel> classModels;
    private final List<String> output;

    SmellAnalyzer(List<AbstractClassModel> classModels, Graph graph) {
        this.classModels = classModels;
        this.graph = graph;
        this.output = new ArrayList<>();
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
    }

    void CircularDependencyAnalyze() {
        //TODO:完成循环依赖分析
        /*
         * 对图进行拓扑排序
         * 得到的List<String>或者String加入output
         */
    }

    public List<String> generateOutput() {
        classAnalyze();
        CircularDependencyAnalyze();
        inheritanceTreeAnalyze();
        return output;
    }

}
