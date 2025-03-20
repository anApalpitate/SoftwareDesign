package diagram;

import com.github.javaparser.ast.Modifier;

/**
 * 记录字段（属性）的信息，包括访问修饰符和静态标志。
 */
public class FieldInfo {
    private final String name;
    private final String type;
    private final String accessModifier;
    private final boolean isStatic;  // 新增字段来记录静态标志

    public FieldInfo(String name, String type, Modifier keyword, boolean isStatic) {
        this.name = name;
        this.type = type;
        this.accessModifier = mapAccessModifier(keyword);
        this.isStatic = isStatic;
    }

    private String mapAccessModifier(Modifier keyword) {
        if (keyword == null) return "~"; // package-private
        switch (keyword.getKeyword()) {
            case PUBLIC: return "+";
            case PRIVATE: return "-";
            case PROTECTED: return "#";
            default: return "~";
        }
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public String getAccessModifier() { return accessModifier; }

    public boolean isStatic() {
        return isStatic;
    }

    // 获取UML格式化的字段表示，添加静态标记
    public String getUmlField() {
        String staticPrefix = isStatic ? " {static}" : "";
        return accessModifier + staticPrefix + " " + name + ": " + type;
    }
}
