package graph;

import java.util.HashSet;

public class Graph {
    static private ClassMap inheritanceMap;
    static private ClassMap implementationMap;
    static private ClassMap associationMap;
    static private ClassMap dependencyMap;

    public Graph() {
        inheritanceMap = new ClassMap();
        implementationMap = new ClassMap();
        associationMap = new ClassMap();
        dependencyMap = new ClassMap();
    }

    public static void addInheritance(String dst, String src) {
        inheritanceMap.add(dst, src);
    }

    public static void addImplementation(String dst, String src) {
        implementationMap.add(dst, src);
    }

    public static void addAssociation(String dst, String src) {
        associationMap.add(dst, src);
    }

    public static void addDependency(String dst, String src) {
        dependencyMap.add(dst, src);
    }

    public HashSet<String> getInheritance(String src) {
        return inheritanceMap.get(src);
    }

    public HashSet<String> getImplementation(String src) {
        return implementationMap.get(src);
    }

    public HashSet<String> getAssociation(String src) {
        return associationMap.get(src);
    }

    public HashSet<String> getDependency(String src) {
        return dependencyMap.get(src);
    }

    public String generateString() {
        return inheritanceMap.generateString(" <|-- ") +
                implementationMap.generateString(" <|.. ") +
                associationMap.generateString(" <-- ") +
                /*已经建立关联的类不再绘制依赖*/
                dependencyMap.generateStringWithFilter(" <.. ", associationMap);
    }

}
