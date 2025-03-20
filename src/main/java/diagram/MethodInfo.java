package diagram;

import com.github.javaparser.ast.Modifier;

import java.util.List;

/**
 * 记录方法的信息，包括访问修饰符以及是否为静态方法。
 */
public class MethodInfo {
    private final String name;
    private final String returnType;
    private final List<String> parameters;
    private final String accessModifier;
    private final boolean isStatic;

    public MethodInfo(String name, String returnType, List<String> parameters, Modifier keyword, boolean isStatic) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
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
    public String getReturnType() { return returnType; }
    public List<String> getParameters() { return parameters; }
    public String getAccessModifier() { return accessModifier; }
    public boolean isStatic() { return isStatic; }
}
