package model;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import utils.CommonUtil;

import java.util.Comparator;
import java.util.List;

public abstract class AbstractClassModel extends BaseModel {
    List<FieldModel> fields;
    List<MethodModel> methods;

    public AbstractClassModel(ClassOrInterfaceDeclaration classOrInterface) {
        super(classOrInterface.getNameAsString(), classOrInterface.getModifiers().toString());
    }


    abstract void parseFields(ClassOrInterfaceDeclaration classOrInterface);

    abstract void parseMethods(ClassOrInterfaceDeclaration classOrInterface);

    public abstract String generateString();

    protected void SortFieldsAndMethods() {
        if (!(fields == null))
            fields.sort(Comparator.comparing(FieldModel::getVisibility, CommonUtil::visibilityOrder));
        if (!(methods == null))
            methods.sort(Comparator.comparing(MethodModel::getVisibility, CommonUtil::visibilityOrder));
    }
}
