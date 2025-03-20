package diagram;

import java.util.List;

/**
 * 表示单个类、接口或枚举的信息。
 */
public class ClassInfo {
    private final String name;
    private final boolean isInterface;
    private final List<String> superClasses;
    private final List<String> interfaces;
    private final List<FieldInfo> fields;
    private final List<MethodInfo> methods;

    public ClassInfo(String name, boolean isInterface, List<String> superClasses, List<String> interfaces, List<FieldInfo> fields, List<MethodInfo> methods) {
        this.name = name;
        this.isInterface = isInterface;
        this.superClasses = superClasses;
        this.interfaces = interfaces;
        this.fields = fields;
        this.methods = methods;
    }

    public String getName() { return name; }
    public boolean isInterface() { return isInterface; }
    public List<String> getSuperClasses() { return superClasses; }
    public List<String> getInterfaces() { return interfaces; }
    public List<FieldInfo> getFields() { return fields; }
    public List<MethodInfo> getMethods() { return methods; }
}
