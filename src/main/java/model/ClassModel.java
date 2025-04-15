package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import graph.Graph;
import utils.CommonUtil;

public class ClassModel extends AbstractClassModel {
    private final boolean isAbstract;
    String GenericType;
    Set<String> AssociatedClasses;
    Set<String> DependedClasses;

    /*注意:field在处理int a,b,c的情况时应该是加3*/
    private int fieldCnt = 0;
    /*注意区分构造方法*/
    private int methodCnt = 0;

    public ClassModel(ClassOrInterfaceDeclaration decl) {
        super(decl);
        this.isAbstract = decl.isAbstract();
        this.GenericType = extractGenericTypes(decl);

        /*解析域和方法*/
        parseFields(decl);
        parseMethods(decl);
        SortFieldsAndMethods();
        /*类间关系分析*/
        addInheritancesAndImplementations(decl);
        addAssociations();
        addDependencies();
    }

    // 拷贝构造函数
    public ClassModel(ClassModel other) {
        super();  // 调用父类的无参构造函数，初始化fields和methods

        this.name = other.name;  // 复制name字段
        this.visibility = other.visibility;  // 复制可见性字段
        this.isAbstract = other.isAbstract;  // 复制是否是抽象类
        this.GenericType = other.GenericType; // 复制泛型类型

        // 深拷贝集合类型字段
        this.AssociatedClasses = new HashSet<>(other.AssociatedClasses);  // 深拷贝AssociatedClasses集合
        this.DependedClasses = new HashSet<>(other.DependedClasses);    // 深拷贝DependedClasses集合

        // 深拷贝fields列表
        this.fields = new ArrayList<>();
        for (FieldModel field : other.fields) {
            this.fields.add(new FieldModel(field));
        }

        // 深拷贝methods列表
        this.methods = new ArrayList<>();
        for (MethodModel method : other.methods) {
            this.methods.add(new MethodModel(method));
        }

        this.methodCnt = other.methodCnt;
        this.fieldCnt = other.fieldCnt;
    }

    void parseFields(BodyDeclaration declaration) {
        AssociatedClasses = new HashSet<>();
        if (declaration instanceof ClassOrInterfaceDeclaration classDecl) {
            for (FieldDeclaration field : classDecl.getFields()) {
                FieldModel fieldModel = new FieldModel(field, "");
                fields.add(fieldModel);
                AssociatedClasses.addAll(fieldModel.getAssociations());
                fieldCnt += fieldModel.getCnt();
            }
        }
    }

    void parseMethods(BodyDeclaration declaration) {
        DependedClasses = new HashSet<>();
        if (declaration instanceof ClassOrInterfaceDeclaration classDecl) {
            for (BodyDeclaration member : classDecl.getMembers()) {
                if (member instanceof MethodDeclaration method) {
                    MethodModel methodModel = new MethodModel(method, "");
                    methods.add(methodModel);
                    DependedClasses.addAll(methodModel.getDependencies());
                    methodCnt += (methodModel.isConstructor() ? 0 : 1);
                } else if (member instanceof ConstructorDeclaration constructor) {
                    // 处理构造函数
                    MethodModel methodModel = new MethodModel(constructor, "");
                    methods.add(methodModel);
                    DependedClasses.addAll(methodModel.getDependencies());
                    // 构造函数这里 methodCnt 不增加，因为之前的逻辑构造函数不计数
                }
            }
        }
    }

    private void addInheritancesAndImplementations(ClassOrInterfaceDeclaration decl) {
        String srcName = getName();
        for (ClassOrInterfaceType relatedClass : decl.getExtendedTypes()) {
            String dstName = relatedClass.getNameAsString();
            Graph.addInheritance(srcName, dstName);
        }
        for (ClassOrInterfaceType relatedClass : decl.getImplementedTypes()) {
            String dstName = relatedClass.getNameAsString();
            Graph.addImplementation(srcName, dstName);
        }
    }

    private void addAssociations() {
        for (String associatedClass : AssociatedClasses) {
            if (CommonUtil.isBasicType(associatedClass))
                continue;
            Graph.addAssociation(getName(), associatedClass);
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
        String AbstractStr = isAbstract ? "abstract " : "";
        sb.append(AbstractStr);
        sb.append("class ").append(getName()).append(GenericType).append(" {\n");

        for (FieldModel field : fields)
            sb.append(blank).append(field.generateString()).append("\n");
        for (MethodModel method : methods)
            // 构造函数不打印
            if (!method.isConstructor()){
                sb.append(blank).append(method.generateString()).append("\n");
            }
        sb.append("}\n");
        return sb.toString();
    }

    public boolean isGodClass() {
        return fieldCnt >= 20 || methodCnt >= 20;
    }

    public boolean isLazyClass() {
        return fieldCnt == 0 || methodCnt <= 1;
    }

    public boolean isDataClass() {
        if (isGodClass() || isLazyClass())
            return false;
        for (MethodModel method : methods) {
            if(method.isConstructor())
                continue;
            if (!method.getName().startsWith("get") && !method.getName().startsWith("set"))
                return false;
        }
        return true;
    }

    public void addField(FieldDeclaration field) {
        FieldModel fieldModel = new FieldModel(field, "");
        fields.add(fieldModel);  // 添加到字段列表
        //AssociatedClasses.addAll(fieldModel.getAssociations());  // 更新关联类信息
        fieldCnt += fieldModel.getCnt();  // 更新字段数量计数
    }

    public void addMethod(MethodDeclaration methodDecl) {
        MethodModel methodModel = new MethodModel(methodDecl, this.getName());
        methods.add(methodModel);
        methodCnt += 1;
    }

    public boolean getIsAbstract(){
        return  isAbstract;
    }

    public String getGenericType(){
        return GenericType;
    }

    public List<FieldModel> getFields(){
        return fields;
    }

    public List<MethodModel> getMethods(){
        return methods;
    }

    public void deleteField(String fieldName) {
        List<FieldModel> toRemove = new ArrayList<>();

        for (FieldModel field : fields) {
            String[] names = field.getName().split(",");
            for (String name : names) {
                if (name.trim().equals(fieldName)) {
                    toRemove.add(field);
                    break;
                }
            }
        }
        fieldCnt -= 1;
        fields.removeAll(toRemove);
    }

    public void deleteMethod(String functionName) {
        List<MethodModel> toRemove = new ArrayList<>();

        for (MethodModel method : methods) {
            if (method.getName().equals(functionName)) {
                toRemove.add(method);
            }
        }
        methodCnt -= 1;
        methods.removeAll(toRemove);
    }
}
