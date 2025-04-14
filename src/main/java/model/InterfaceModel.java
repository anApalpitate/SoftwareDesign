package model;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import graph.Graph;
import utils.CommonUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InterfaceModel extends AbstractClassModel {
    String GenericType;
    Set<String> DependedClasses;

    public InterfaceModel(ClassOrInterfaceDeclaration decl) {
        super(decl);
        this.GenericType = extractGenericTypes(decl);

        parseFields(decl);
        parseMethods(decl);
        SortFieldsAndMethods();

        findInheritances(decl);
        addDependencies();
    }

    // 拷贝构造函数
    public InterfaceModel(InterfaceModel other) {
        super();  // 调用父类的拷贝构造函数
        this.GenericType = other.GenericType;  // 复制泛型类型

        // 复制方法列表
        this.methods = new ArrayList<>();
        for (MethodModel method : other.methods) {
            this.methods.add(new MethodModel(method));  // 复制每个方法
        }

        // 复制依赖类
        this.DependedClasses = new HashSet<>(other.DependedClasses);  // 复制依赖关系
        this.name = other.name;
        this.visibility = other.visibility;
    }

    void parseFields(BodyDeclaration declaration) {
    }

    void parseMethods(BodyDeclaration declaration) {
        this.DependedClasses = new HashSet<>();

        if (declaration instanceof ClassOrInterfaceDeclaration interfaceDecl) {
            for (MethodDeclaration method : interfaceDecl.getMethods()) {
                MethodModel methodModel = new MethodModel(method, "interface");
                methods.add(methodModel);
                DependedClasses.addAll(methodModel.getDependencies());
            }
        }
    }

    public String getGenericType(){
        return GenericType;
    }

    public List<MethodModel> getMethods(){
        return methods;
    }

    private void findInheritances(ClassOrInterfaceDeclaration decl) {
        String srcName = getName();
        for (ClassOrInterfaceType relatedClass : decl.getExtendedTypes()) {
            String dstName = relatedClass.getNameAsString();
            Graph.addInheritance(srcName, dstName);
        }
    }

    private void addDependencies() {
        for (String dependedClass : DependedClasses) {
            if (CommonUtil.isBasicType(dependedClass))
                continue;
            Graph.addDependency(getName(), dependedClass);
        }
    }

    public String generateString() {
        String blank = "    ";
        StringBuilder sb = new StringBuilder();
        sb.append("interface ").append(getName()).append(GenericType).append(" {\n");
        for (MethodModel method : methods) {
            sb.append(blank).append(method.generateString()).append("\n");
        }
        sb.append("}\n");
        return sb.toString();
    }
}
