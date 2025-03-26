package model;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public class InterfaceModel extends AbstractClassModel {
    public InterfaceModel(ClassOrInterfaceDeclaration classOrInterface) {
        super(classOrInterface);
        parseFields(classOrInterface);
        parseMethods(classOrInterface);
        SortFieldsAndMethods();
    }

    void parseFields(BodyDeclaration declaration) {
    }

    void parseMethods(BodyDeclaration declaration) {
        if (declaration instanceof ClassOrInterfaceDeclaration interfaceDecl) {
            for (MethodDeclaration method : interfaceDecl.getMethods()) {
                methods.add(new MethodModel(method, "interface"));
            }
        }
    }


    public String generateString() {
        String blank = "    ";
        StringBuilder sb = new StringBuilder();
        sb.append("interface ").append(getName()).append(" {\n");
        for (MethodModel method : methods) {
            sb.append(blank).append(method.generateString()).append("\n");
        }
        sb.append("}\n");
        return sb.toString();
    }
}
