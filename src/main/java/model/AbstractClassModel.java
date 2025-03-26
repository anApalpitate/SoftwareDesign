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

    public AbstractClassModel(BodyDeclaration declaration) {
        if (declaration instanceof ClassOrInterfaceDeclaration classDecl) {
            initialize(classDecl.getNameAsString(), classDecl.getModifiers().toString());
        } else if (declaration instanceof EnumDeclaration enumDecl) {
            initialize(enumDecl.getNameAsString(), enumDecl.getModifiers().toString());
        }
        fields = new ArrayList<>();
        methods = new ArrayList<>();
    }

    abstract void parseFields(BodyDeclaration declaration);

    abstract void parseMethods(BodyDeclaration declaration);

    public abstract String generateString();

    protected void SortFieldsAndMethods() {
        if (!(fields == null))
            fields.sort(Comparator.comparing(FieldModel::getVisibility, CommonUtil::visibilityOrder));
        if (!(methods == null))
            methods.sort(Comparator.comparing(MethodModel::getVisibility, CommonUtil::visibilityOrder));
    }
}
