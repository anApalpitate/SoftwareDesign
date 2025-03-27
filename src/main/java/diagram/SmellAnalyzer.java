package diagram;

import graph.Graph;
import model.AbstractClassModel;
import model.ClassModel;

import java.util.ArrayList;
import java.util.List;

public class SmellAnalyzer {
    private final Graph graph;
    private final List<AbstractClassModel> classModels;
    private List<String> output;

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

    public List<String> generateOutput() {
        classAnalyze();
        return output;
    }

}
