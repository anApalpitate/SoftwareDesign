package utils;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import model.AbstractClassModel;
import model.ClassModel;
import model.EnumModel;
import model.InterfaceModel;
import relate.Relate;

public class Factory {
    public static AbstractClassModel classFactory(ClassOrInterfaceDeclaration classOrInterface) {
        if (classOrInterface.isInterface())
            return new InterfaceModel(classOrInterface);
        if (classOrInterface.isEnumDeclaration())
            return new EnumModel(classOrInterface);
        return new ClassModel(classOrInterface);
    }

    public static Relate relateFactory(ClassOrInterfaceDeclaration classOrInterface) {
        return null;
    }
}
