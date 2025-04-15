// package diagram;

// import graph.ClassMap;
// import graph.Graph;
// import model.AbstractClassModel;
// import model.ClassModel;
// import utils.AnalyzerUtil;

// import java.util.*;

// public class SmellAnalyzer {
//     private final Graph graph;
//     private final List<AbstractClassModel> classModels;
//     private final List<String> output;
//     private final AnalyzerUtil util;

//     SmellAnalyzer(List<AbstractClassModel> classModels, Graph graph) {
//         this.classModels = classModels;
//         this.graph = graph;
//         this.output = new ArrayList<>();
//         this.util = new AnalyzerUtil();
//     }

//     public List<String> generateOutput() {
//         ClassAnalyze();
//         InheritanceTreeAnalyze();
//         CircularDependencyAnalyze();
//         return output;
//     }

//     private void ClassAnalyze() {
//         /*识别God Class、Lazy Class 、Data Class*/
//         List<String> GodClassBuffer = new ArrayList<>();
//         List<String> LazyClassBuffer = new ArrayList<>();
//         List<String> DataClassBuffer = new ArrayList<>();
//         for (AbstractClassModel model : classModels) {
//             if (model instanceof ClassModel classModel) {
//                 if (classModel.isGodClass())
//                     GodClassBuffer.add("God Class: " + classModel.getName());
//                 if (classModel.isLazyClass())
//                     LazyClassBuffer.add("Lazy Class: " + classModel.getName());
//                 if (classModel.isDataClass())
//                     DataClassBuffer.add("Data Class: " + classModel.getName());
//             }
//         }
//         output.addAll(GodClassBuffer);
//         output.addAll(LazyClassBuffer);
//         output.addAll(DataClassBuffer);
//     }

//     void InheritanceTreeAnalyze() {
//         Set<String> allClasses = new HashSet<>();
//         for (AbstractClassModel model : classModels) {
//             allClasses.add(model.getName());
//         }
//         // 找出没有父类的类（即根节点）
//         Set<String> roots = new HashSet<>(allClasses);
//         for (String className : allClasses) {
//             if (!graph.getInheritance(className).isEmpty()) {
//                 // 找爸爸，有爸爸，排除
//                 roots.remove(className);
//             }
//         }
//         // Too Many Children 分析
//         for (String className : allClasses) {
//             HashSet<String> children = graph.getReverseInheritance(className);
//             if (children.size() >= 10) {
//                 output.add("Too Many Children: " + className);
//             }
//         }
//         // Inheritance Abuse 分析：从每个 root 开始 DFS
//         for (String root : roots) {
//             util.DfsCheckDepth(root, new LinkedList<>(), graph, output);
//         }
//     }

//     void CircularDependencyAnalyze() {
//         /*由于样例中至多有一个环，此处仅设计了支持输出最多一个环的算法*/
//         ClassMap newGraph = graph.getMergedMap();
//         LinkedList<String> path = new LinkedList<>();
//         for (AbstractClassModel model : classModels) {
//             if (util.IsCycleByDFS(model.getName(), newGraph, new HashSet<>(), new HashSet<>(), path)) {
//                 output.add(util.formatCycleOutput(path));
//                 return;
//             }
//         }
//     }


// }

// pzj-0413
package diagram;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import graph.ClassMap;
import graph.Graph;
import model.AbstractClassModel;
import model.ClassModel;
import model.FieldModel;
import model.MethodModel;
import utils.AnalyzerUtil;

public class SmellAnalyzer {
    private final Graph graph;
    private final List<AbstractClassModel> classModels;
    private final List<String> output;
    private final AnalyzerUtil util;

    public SmellAnalyzer(List<AbstractClassModel> classModels, Graph graph) {
        this.classModels = classModels;
        this.graph = graph;
        this.output = new ArrayList<>();
        this.util = new AnalyzerUtil();
    }

    public List<String> generateOutput() {
        ClassAnalyze();
        InheritanceTreeAnalyze();
        CircularDependencyAnalyze();
        detectSingletonPattern();
        detectStrategyPattern();
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
            // 遍历每个类
            // 传入：类名x，综合图，新的HashSet1，新的HashSet2，路径
            if (util.IsCycleByDFS(model.getName(), newGraph, new HashSet<>(), new HashSet<>(), path)) {
                // output.add(path.toString());
                path = util.extractTrailingSequence(path);
                // output.add(path.toString());
                output.add(util.formatCycleOutput(path));
                return;
            }
        }
    }


    private void detectSingletonPattern() {
        // output.add("Singleton Pattern Detection:");
        for (AbstractClassModel model : classModels) {
            if (model instanceof ClassModel classModel) {
                // output.add("now class: " + classModel.getName());
                if (isSingleton(classModel)) {
                    output.add("Possible Design Patterns: Singleton Pattern");
                }
            }
        }
    }

    private boolean isSingleton(ClassModel classModel) {
        // 不存在子类继承自类A
        // output.add("0:"+graph.getInheritance(classModel.getName()).toString());
        if (!graph.getReverseInheritance(classModel.getName()).isEmpty()) {
            // output.add("1: not a singleton class" + classModel.getName());
            // output.add("111:"+graph.getReverseInheritance(classModel.getName()).toString());
            return false;
        }
        // 没有公共（public）构造函数且存在私有构造函数
        boolean hasPublicConstructor = false;
        boolean hasPrivateConstructor = false;
        for (MethodModel method : classModel.getMethods()) {
            // output.add(method.getName());
            // output.add("aaa");
            if (method.isConstructor()) {
                // output.add("kkk");
                // output.add(method.getVisibility());
                if ("+".equals(method.getVisibility())) {
                    // output.add("hasPublicConstructor is true in class:" + classModel.getName());
                    hasPublicConstructor = true;
                } else if ("-".equals(method.getVisibility())) {
                    hasPrivateConstructor = true;
                }
            }
        }
        if (hasPublicConstructor || !hasPrivateConstructor) {
            // output.add("2: not a singleton class" + classModel.getName());
            return false;
        }
        // 包含静态私有字段，类型为自身类
        boolean hasStaticPrivateField = false;
        for (FieldModel field : classModel.getFields()) {
            if (field.isStatic() && "-".equals(field.getVisibility()) && field.getType().equals(classModel.getName())) {
                hasStaticPrivateField = true;
                break;
            }
        }
        if (!hasStaticPrivateField) {
            // output.add("3: not a singleton class" + classModel.getName());
            return false;
        }
        // 提供静态公有方法获取实例
        boolean hasStaticPublicMethod = false;
        for (MethodModel method : classModel.getMethods()) {
            if (method.isStatic() && "+".equals(method.getVisibility()) && method.getReturnType().equals(classModel.getName())) {
                hasStaticPublicMethod = true;
                break;
            }
        }
        return hasStaticPublicMethod;
    }

    private void detectStrategyPattern() {
        List<AbstractClassModel> strategyInterfaces = new ArrayList<>();
        // 找出所有可能的策略接口
        for (AbstractClassModel model : classModels) {
            if ((model.isInterface() || model.isAbstract()) &&
                    model.getMethods().size() > 0 &&
                    (model.getName().endsWith("Strategy") || model.getName().endsWith("Policy") || model.getName().endsWith("Behavior"))) {
                strategyInterfaces.add(model);
            }
        }
        // if(strategyInterfaces.size() == 0) {
        //     System.out.println("No Strategy Pattern found.");
        // }
        // 检查每个策略接口是否满足策略模式的条件
        for (AbstractClassModel strategyInterface : strategyInterfaces) {
            // 检查实现类大于等于2
            List<AbstractClassModel> concreteStrategies = new ArrayList<>();
            for (AbstractClassModel model : classModels) {
                // System.out.println("dad:"+ graph.getInheritance(model.getName()).size());
                if (model instanceof ClassModel && graph.getInheritance(model.getName()).contains(strategyInterface.getName())) {
                    concreteStrategies.add(model);
                }
                else if ( model instanceof ClassModel && graph.getImplementation(model.getName()).contains(strategyInterface.getName())) {
                    concreteStrategies.add(model);
                }
            }
            if (concreteStrategies.size() >= 2) {
                // 检查是否存在上下文类
                // System.out.println("1222" + strategyInterface.getName().toString());
                for (AbstractClassModel model : classModels) {
                    if (model instanceof ClassModel && graph.getAssociation(model.getName()).contains(strategyInterface.getName())) {
                        output.add("Possible Design Patterns: Strategy Pattern");
                        return;
                    }
                }
            }
        }
    }
}