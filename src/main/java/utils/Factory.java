package utils;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import model.AbstractClassModel;
import model.ClassModel;
import model.EnumModel;
import model.InterfaceModel;

public class Factory {
    public static AbstractClassModel classFactory(BodyDeclaration declaration) {
        if (declaration instanceof ClassOrInterfaceDeclaration decl) {
            if (decl.isInterface()) {
                return new InterfaceModel(decl);
            } else {
                return new ClassModel(decl);
            }
        } else if (declaration instanceof EnumDeclaration decl) {
            return new EnumModel(decl);
        } else {
            System.out.println("Declaration is not a class or interface or enum");
            return null;
        }
    }

}
