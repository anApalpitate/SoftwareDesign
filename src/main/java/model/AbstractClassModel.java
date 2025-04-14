package model;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import utils.CommonUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractClassModel extends BaseModel {
    List<FieldModel> fields;
    List<MethodModel> methods;

    /*
     *BodyDeclaration是ClassOrInterfaceDeclaration和EnumDeclaration的公共父类，
     * 使用BodyDeclaration以兼容子类的构造函数。
     */
    public AbstractClassModel(BodyDeclaration declaration) {
        if (declaration instanceof ClassOrInterfaceDeclaration classDecl) {
            initialize(classDecl.getNameAsString(), classDecl.getModifiers().toString());
        } else if (declaration instanceof EnumDeclaration enumDecl) {
            initialize(enumDecl.getNameAsString(), enumDecl.getModifiers().toString());
        }
        fields = new ArrayList<>();
        methods = new ArrayList<>();
    }

    public AbstractClassModel() {
        this.fields = new ArrayList<>();
        this.methods = new ArrayList<>();
    }

    /*域的解析方法*/
    abstract void parseFields(BodyDeclaration declaration);

    /*方法的解析方法*/
    abstract void parseMethods(BodyDeclaration declaration);

    /*生产类的输出字符串*/
    public abstract String generateString();

    /*对访问符进行排序*/
    public void SortFieldsAndMethods() {
        if (!(fields == null))
            fields.sort(Comparator.comparing(FieldModel::getVisibility, CommonUtil::visibilityOrder));
        if (!(methods == null))
            methods.sort(Comparator.comparing(MethodModel::getVisibility, CommonUtil::visibilityOrder));
    }

    /*对于泛型符号，将[]替换为<>,以便于按照指定格式输出*/
    protected String extractGenericTypes(ClassOrInterfaceDeclaration decl) {
        String res = decl.getTypeParameters().toString();
        if (res.length() == 2)
            return "";
        return res.replace("[", "<").replace("]", ">");
    }
}
