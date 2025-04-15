package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;

import utils.CommonUtil;

public abstract class AbstractClassModel extends BaseModel {
    List<FieldModel> fields;
    List<MethodModel> methods;
    private final boolean isInterface;
    private final boolean isAbstract;

    /*
     *BodyDeclaration是ClassOrInterfaceDeclaration和EnumDeclaration的公共父类，
     * 使用BodyDeclaration以兼容子类的构造函数。
     */
    public AbstractClassModel(BodyDeclaration declaration) {
        if (declaration instanceof ClassOrInterfaceDeclaration classDecl) {
            initialize(classDecl.getNameAsString(), classDecl.getModifiers().toString());
            this.isInterface = classDecl.isInterface();
            this.isAbstract = classDecl.isAbstract();
        } else if (declaration instanceof EnumDeclaration enumDecl) {
            initialize(enumDecl.getNameAsString(), enumDecl.getModifiers().toString());
            this.isInterface = false;
            this.isAbstract = false;
        } else {
            this.isInterface = false;
            this.isAbstract = false;
        }
        fields = new ArrayList<>();
        methods = new ArrayList<>();
    }

    public AbstractClassModel() {
        this.fields = new ArrayList<>();
        this.methods = new ArrayList<>();
        this.isInterface = false;
        this.isAbstract = false;
    }

    /*域的解析方法*/
    abstract void parseFields(BodyDeclaration declaration);

    /*方法的解析方法*/
    abstract void parseMethods(BodyDeclaration declaration);

    /*生产类的输出字符串*/
    public abstract String generateString();

    /*对访问符进行排序*/
    public void SortFieldsAndMethods() {
        if (fields != null)
            fields.sort(Comparator.comparing(FieldModel::getVisibility, CommonUtil::visibilityOrder));
        if (methods != null)
            methods.sort(Comparator.comparing(MethodModel::getVisibility, CommonUtil::visibilityOrder));
    }

    /*对于泛型符号，将[]替换为<>,以便于按照指定格式输出*/
    protected String extractGenericTypes(ClassOrInterfaceDeclaration decl) {
        String res = decl.getTypeParameters().toString();
        if (res.length() == 2)
            return "";
        return res.replace("[", "<").replace("]", ">");
    }

    // 添加 isInterface() 方法
    public boolean isInterface() {
        return isInterface;
    }

    // 添加 isAbstract() 方法
    public boolean isAbstract() {
        return isAbstract;
    }

    // 添加 getMethods() 方法
    public List<MethodModel> getMethods() {
        // for(MethodModel method : methods){
        //     System.out.println(method.getName());
        // }
        return methods;
    }
}