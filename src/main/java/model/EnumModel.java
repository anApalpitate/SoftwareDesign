package model;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class EnumModel extends AbstractClassModel {
    public EnumModel(ClassOrInterfaceDeclaration classOrInterface) {
        super(classOrInterface);
        parseMethods(classOrInterface);
        parseFields(classOrInterface);
        SortFieldsAndMethods();
    }

    void parseMethods(ClassOrInterfaceDeclaration classOrInterface) {

    }

    void parseFields(ClassOrInterfaceDeclaration classOrInterface) {

    }

    public String generateString() {
        return "";

    }
}

