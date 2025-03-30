// package diagram;

// import java.util.List;

// public class ClassDiagram {
    
//     // 生成PlantUML语法的文本格式类图。
//     // e.g. System.out.println(diagram.generateUML());
//     public String generateUML() {
//         return null;
//     }

//     /**
//      * 你应当在迭代二中实现这个方法
//      * @return 返回代码中的“坏味道”
//      */
//     public List<String> getCodeSmells() {
//         return null;
//     }
// }

// ClassDiagram.java
package diagram;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;

/**
 * 这个类表示一个类图，包含类、接口和它们之间的关系
 */
public class ClassDiagram {
    // 存储所有的类定义
    private List<ClassOrInterfaceDeclaration> classes = new ArrayList<>();
    // 存储所有的接口定义
    private List<ClassOrInterfaceDeclaration> interfaces = new ArrayList<>();
    // 存储类/接口之间的关系
    private List<Relationship> relationships = new ArrayList<>();

    /**
     * 添加一个类到类图中
     * @param classDecl 类的定义（从JavaParser解析得到）
     */
    public void addClass(ClassOrInterfaceDeclaration classDecl) {
        if (!classDecl.isInterface()) {  // 确保添加的是类而不是接口
            classes.add(classDecl);
        }
    }

    /**
     * 添加一个接口到类图中
     * @param interfaceDecl 接口的定义（从JavaParser解析得到）
     */
    public void addInterface(ClassOrInterfaceDeclaration interfaceDecl) {
        if (interfaceDecl.isInterface()) {  // 确保添加的是接口而不是类
            interfaces.add(interfaceDecl);
        }
    }

    /**
     * 添加一个关系到类图中
     * @param relationship 关系对象，包含关系类型和参与的两个类/接口
     */
    public void addRelationship(Relationship relationship) {
        relationships.add(relationship);
    }

    /**
     * 生成PlantUML格式的类图文本
     * @return PlantUML格式的字符串
     */
    // 修改ClassDiagram.java中的generateUML()方法
    public String generateUML() {
        StringBuilder uml = new StringBuilder("@startuml\n");
    
        // 处理类
        for (ClassOrInterfaceDeclaration classDecl : classes) {
            uml.append("class ").append(classDecl.getNameAsString()).append(" {\n");
            
            // 按访问修饰符顺序处理字段
            List<FieldDeclaration> fields = new ArrayList<>(classDecl.getFields()); // 创建可修改的副本
            fields.sort((f1, f2) -> {
                String mod1 = f1.getAccessSpecifier().asString();
                String mod2 = f2.getAccessSpecifier().asString();
                return getAccessModifierOrder(mod1) - getAccessModifierOrder(mod2);
            });
            
            for (FieldDeclaration field : fields) {
                for (VariableDeclarator var : field.getVariables()) {
                    String accessModifier = getAccessModifierSymbol(field.getAccessSpecifier().asString());
                    String staticModifier = field.isStatic() ? "{static} " : "";
                    uml.append("    ").append(accessModifier).append(" ").append(staticModifier)
                       .append(var.getNameAsString()).append(": ").append(var.getTypeAsString()).append("\n");
                }
            }
            
            // 按访问修饰符顺序处理方法
            List<MethodDeclaration> methods = new ArrayList<>(classDecl.getMethods()); // 创建可修改的副本
            methods.sort((m1, m2) -> {
                String mod1 = m1.getAccessSpecifier().asString();
                String mod2 = m2.getAccessSpecifier().asString();
                return getAccessModifierOrder(mod1) - getAccessModifierOrder(mod2);
            });
            
            for (MethodDeclaration method : methods) {
                // 跳过构造函数
                if (method.getNameAsString().equals(classDecl.getNameAsString())) {
                    continue;
                }
                
                String accessModifier = getAccessModifierSymbol(method.getAccessSpecifier().asString());
                String staticModifier = method.isStatic() ? "{static} " : "";
                uml.append("    ").append(accessModifier).append(" ").append(staticModifier)
                   .append(method.getNameAsString()).append("(");
                
                boolean firstParam = true;
                for (Parameter param : method.getParameters()) {
                    if (!firstParam) uml.append(", ");
                    uml.append(param.getNameAsString()).append(": ").append(param.getTypeAsString());
                    firstParam = false;
                }
                
                uml.append(")");
                uml.append(": ").append(method.getTypeAsString());
                uml.append("\n");
            }
            
            uml.append("}\n");
        }
    
        // 处理接口(接口方法默认public，不需要排序)
        for (ClassOrInterfaceDeclaration interfaceDecl : interfaces) {
            uml.append("interface ").append(interfaceDecl.getNameAsString()).append(" {\n");
            
            for (MethodDeclaration method : interfaceDecl.getMethods()) {
                uml.append("    + ").append(method.getNameAsString()).append("(");
                
                boolean firstParam = true;
                for (Parameter param : method.getParameters()) {
                    if (!firstParam) uml.append(", ");
                    uml.append(param.getNameAsString()).append(": ").append(param.getTypeAsString());
                    firstParam = false;
                }
                
                uml.append(")");
                uml.append(": ").append(method.getTypeAsString());
                uml.append("\n");
            }
            
            uml.append("}\n");
        }
    
        // 处理关系
        for (Relationship rel : relationships) {
            uml.append(rel.from).append(" ").append(rel.type).append(" ").append(rel.to).append("\n");
        }
    
        uml.append("@enduml");
        return uml.toString();
    }
    
    // 新增方法：获取访问修饰符的排序优先级
    private int getAccessModifierOrder(String modifier) {
        switch (modifier.toLowerCase()) {
            case "private": return 0;      // 最先
            case "protected": return 1;    // 其次
            case "": return 2;             // 包私有(默认)
            case "public": return 3;       // 最后
            default: return 4;             // 其他情况(应该不会出现)
        }
    }

    /**
     * 将Java访问修饰符转换为PlantUML符号
     * @param accessSpecifier Java访问修饰符字符串
     * @return PlantUML符号
     */
    private String getAccessModifierSymbol(String accessSpecifier) {
        switch (accessSpecifier.toLowerCase()) {
            case "private": return "-";    // private用-表示
            case "protected": return "#";   // protected用#表示
            case "public": return "+";     // public用+表示
            default: return "~";            // 默认(包私有)用~表示
        }
    }

    // 这个方法在迭代二中实现，暂时返回null
    public List<String> getCodeSmells() {
        return null;
    }

    /**
     * 内部类，用于表示类/接口之间的关系
     */
    static class Relationship {
        String from;  // 关系来源(子类/实现类)
        String type;  // 关系类型(<|-- 继承, <|.. 实现)
        String to;    // 关系目标(父类/接口)

        Relationship(String from, String type, String to) {
            this.from = from;
            this.type = type;
            this.to = to;
        }
    }
}
