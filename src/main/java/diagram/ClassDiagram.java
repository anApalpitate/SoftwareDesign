package diagram;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 生成 UML 表示，按照指定规则排序
 */
public class ClassDiagram {
    private final List<ClassInfo> classInfos;

    public ClassDiagram(List<ClassInfo> classInfos) {
        this.classInfos = classInfos;
    }

    public String generateUML() {
        StringBuilder uml = new StringBuilder();
        uml.append("@startuml\n");

        // 1 先输出所有类
        classInfos.stream().filter(info -> !info.isInterface())
                .forEach(info -> uml.append(formatClass(info)));

        // 2 再输出所有接口
        classInfos.stream().filter(ClassInfo::isInterface)
                .forEach(info -> uml.append(formatClass(info)));

        // 3 继承关系
        classInfos.forEach(info -> info.getSuperClasses().forEach(superClass ->
                uml.append(superClass).append(" <|-- ").append(info.getName()).append("\n")));

        // 4 实现关系
        classInfos.forEach(info -> info.getInterfaces().forEach(iface ->
                uml.append(iface).append(" <|.. ").append(info.getName()).append("\n")));

        uml.append("@enduml\n");
        return uml.toString();
    }

    /**
     * 生成符合格式的类/接口的 UML 代码
     */
    private String formatClass(ClassInfo info) {
        StringBuilder sb = new StringBuilder();

        // 处理类、接口、枚举的关键字
        sb.append(info.isInterface() ? "interface " : "class ")
                .append(info.getName()).append(" {\n");

        // 1️⃣ 先输出属性（按照访问修饰符排序）
        List<FieldInfo> sortedFields = info.getFields().stream()
                .sorted(Comparator.comparing(FieldInfo::getAccessModifier, this::compareModifiers))
                .collect(Collectors.toList());
        sortedFields.forEach(field -> {
            // 继续构建 UML 字符串
            sb.append("    ") // 4 空格缩进
                    .append(field.getAccessModifier()).append(" ")
                    .append(field.isStatic() ? "{static} " : "") // 如果是静态的，加上 {static}
                    .append(field.getName()).append(": ")
                    .append(field.getType()).append("\n");
        });


        // 2️⃣ 再输出方法（按照访问修饰符排序）
        List<MethodInfo> sortedMethods = info.getMethods().stream()
                .sorted(Comparator.comparing(MethodInfo::getAccessModifier, this::compareModifiers))
                .collect(Collectors.toList());
        sortedMethods.forEach(method -> sb.append("    ")
                .append(method.getAccessModifier()).append(" ")
                .append(method.isStatic() ? "{static} " : "") // 如果是静态的，加上 {static}
                .append(method.getName()).append("(")
                .append(String.join(", ", method.getParameters()))
                .append("): ").append(method.getReturnType()).append("\n"));

        sb.append("}\n"); // 关闭类定义
        return sb.toString();
    }

    /**
     * 访问修饰符排序规则：
     * private (-) > protected (#) > package-private (~) > public (+)
     */
    private int compareModifiers(String m1, String m2) {
        return Integer.compare(getModifierRank(m1), getModifierRank(m2));
    }

    private int getModifierRank(String modifier) {
        switch (modifier) {
            case "-": return 1; // private
            case "#": return 2; // protected
            case "~": return 3; // package-private
            case "+": return 4; // public
            default: return 5; // 其他情况
        }
    }
}
