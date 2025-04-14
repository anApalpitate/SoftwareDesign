package command;

import diagram.ClassParser;
import graph.Graph;
import model.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Query {
    public static String queryClass(String name, String hide, List<AbstractClassModel>class_list) {
        // 找到目标类
        Optional<ClassModel> targetOpt = class_list.stream()
                .filter(c -> c instanceof ClassModel && c.getName().equals(name))
                .map(c -> (ClassModel) c)
                .findFirst();

        if (!targetOpt.isPresent()) {
            //System.out.println("未找到目标类: " + name);
            return null;
        }

        Set<String> hideSet = new HashSet<>();
        if (hide != null) {
            for (String h : hide.split("\\|")) {
                hideSet.add(h.trim());
            }
        }

        ClassModel model = targetOpt.get();

        String blank = "    ";
        StringBuilder sb = new StringBuilder();
        String AbstractStr = model.getIsAbstract() ? "abstract " : "";
        sb.append(AbstractStr);
        sb.append("class ").append(model.getName()).append(model.getGenericType()).append(" {\n");
        if (!hideSet.contains("field")){
            for (FieldModel field : model.getFields())
                sb.append(blank).append(field.generateString()).append("\n");
        }
        if (!hideSet.contains("method")){
            for (MethodModel method : model.getMethods())
                sb.append(blank).append(method.generateString()).append("\n");
        }

        sb.append("}\n");
        return sb.toString();
    }

    public static String queryInterface(String name, String hide, List<AbstractClassModel> class_list) {
        // 找到目标类
        Optional<InterfaceModel> targetOpt = class_list.stream()
                .filter(c -> c instanceof InterfaceModel && c.getName().equals(name))
                .map(c -> (InterfaceModel) c)
                .findFirst();

        if (!targetOpt.isPresent()) {
            //System.out.println("未找到目标类: " + name);
            return null;
        }

        InterfaceModel model = targetOpt.get();

        Set<String> hideSet = new HashSet<>();
        if (hide != null) {
            for (String h : hide.split("\\|")) {
                hideSet.add(h.trim());
            }
        }

        String blank = "    ";
        StringBuilder sb = new StringBuilder();
        sb.append("interface ").append(model.getName()).append(model.getGenericType()).append(" {\n");
        if (!hideSet.contains("method")){
            for (MethodModel method : model.getMethods()) {
                sb.append(blank).append(method.generateString()).append("\n");
            }
        }
        sb.append("}\n");
        return sb.toString();
    }

    public static String queryEnum(String name, String hide, List<AbstractClassModel> class_list) {
        Optional<EnumModel> targetOpt = class_list.stream()
                .filter(c -> c instanceof EnumModel && c.getName().equals(name))
                .map(c -> (EnumModel) c)
                .findFirst();

        if (!targetOpt.isPresent()) {
            //System.out.println("未找到目标类: " + name);
            return null;
        }

        EnumModel model = targetOpt.get();

        Set<String> hideSet = new HashSet<>();
        if (hide != null) {
            for (String h : hide.split("\\|")) {
                hideSet.add(h.trim());
            }
        }

        String blank = "    ";
        StringBuilder sb = new StringBuilder();
        sb.append("enum ").append(model.getName()).append(" {\n");
        if (!hideSet.contains("constant")){
            for (String constant : model.getConstants())
                sb.append(blank).append(constant).append("\n");
        }
        if (!hideSet.contains("field")) {
            for (FieldModel field : model.getFields())
                sb.append(blank).append(field.generateString()).append("\n");
        }
        if (!hideSet.contains("method")) {
            for (MethodModel method : model.getMethods())
                sb.append(blank).append(method.generateString()).append("\n");
        }

        sb.append("}\n");
        return sb.toString();
    }

    public static String relate(String elementA, String elementB, Graph graph) {
        StringBuilder result = new StringBuilder();

        //继承
        if (graph.getInheritance(elementA).contains(elementB)) {
            result.append(elementB).append(" <|-- ").append(elementA).append("\n");
        }
        if (graph.getReverseInheritance(elementA).contains(elementB)) {
            result.append(elementA).append(" <|-- ").append(elementB).append("\n");
        }

        //实现
        if (graph.getImplementation(elementA).contains(elementB)) {
            result.append(elementB).append(" <|.. ").append(elementA).append("\n");
        }
        if (graph.getImplementation(elementB).contains(elementA)) {
            result.append(elementA).append(" <|.. ").append(elementB).append("\n");
        }

        //关联
        if (graph.getAssociation(elementA).contains(elementB)) {
            result.append(elementB).append(" <-- ").append(elementA).append("\n");
        }
        if (graph.getAssociation(elementB).contains(elementA)) {
            result.append(elementA).append(" <-- ").append(elementB).append("\n");
        }

        //依赖
        if (graph.getDependency(elementA).contains(elementB)
                && !graph.getAssociation(elementA).contains(elementB)) {
            result.append(elementB).append(" <.. ").append(elementA).append("\n");
        }
        if (graph.getDependency(elementB).contains(elementA)
                && !graph.getAssociation(elementB).contains(elementA)) {
            result.append(elementA).append(" <.. ").append(elementB).append("\n");
        }

        return result.toString();
    }

    public static String smellDetail(String elementName, ClassParser classParser) {
        List<String> smells = classParser.getCodeSmells();
        StringBuilder sb = new StringBuilder();

        for (String smell : smells) {
            if (smell.contains(": " + elementName)) {
                sb.append(smell);
                sb.append('\n');
            }
            else if (smell.startsWith("Circular Dependency") && smell.contains(elementName)) {
                sb.append(smell);
                sb.append('\n');
            }
        }
        return sb.toString();
    }
}
