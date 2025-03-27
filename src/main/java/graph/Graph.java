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
    //API如有需要，请继续补充：

    public static void addInheritance(String className, String relatedClassName) {
        /*添加新的继承关系(子类，父类)*/
        inheritanceMap.add(className, relatedClassName);
    }

    public static void addImplementation(String className, String relatedClassName) {
        /*添加新的实现关系(当前类，被实现类 )*/
        implementationMap.add(className, relatedClassName);
    }


    public static void addAssociation(String className, String relatedClassName) {
        /*添加新的关联关系(当前类，被关联类 )*/
        associationMap.add(className, relatedClassName);
    }

    public static void addDependency(String className, String relatedClassName) {
        /*添加新的依赖关系(当前类，被依赖类 )*/
        dependencyMap.add(className, relatedClassName);
    }

    public HashSet<String> getInheritance(String src) {
        /*获取当前类继承的类集合*/
        return inheritanceMap.get(src);
    }

    public HashSet<String> getReverseInheritance(String src) {
        /*获取继承当前类的类集合*/
        return inheritanceMap.getReverse(src);
    }

    public HashSet<String> getImplementation(String src) {
        /*获取当前类的实现类集合 */
        return implementationMap.get(src);
    }

    public HashSet<String> getAssociation(String src) {
        /*获取当前类的关联类集合*/
        return associationMap.get(src);
    }

    public HashSet<String> getDependency(String src) {
        /*获取当前类的依赖类集合*/
        return dependencyMap.get(src);
    }

    public HashSet<String> getReverseDependency(String src) {
        /*获取依赖当前类的类集合*/
        return dependencyMap.getReverse(src);
    }

    public String generateString() {
        return inheritanceMap.generateString(" <|-- ") +
                implementationMap.generateString(" <|.. ") +
                associationMap.generateString(" <-- ") +
                /*已经建立关联的类不再绘制依赖*/
                dependencyMap.generateStringWithFilter(" <.. ", associationMap);
    }


}
