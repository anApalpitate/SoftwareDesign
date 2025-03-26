package model;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import graph.Graph;

public class InterfaceModel extends AbstractClassModel {
    String GenericType;

    public InterfaceModel(ClassOrInterfaceDeclaration decl) {
        super(decl);
        this.GenericType = extractGenericTypes(decl);

        parseFields(decl);
        parseMethods(decl);
        SortFieldsAndMethods();

        findInheritances(decl);
    }

    void parseFields(BodyDeclaration declaration) {
    }

    void parseMethods(BodyDeclaration declaration) {
        if (declaration instanceof ClassOrInterfaceDeclaration interfaceDecl) {
            for (MethodDeclaration method : interfaceDecl.getMethods()) {
                methods.add(new MethodModel(method, getName(), "interface"));
            }
        }
    }

    private void findInheritances(ClassOrInterfaceDeclaration decl) {
        String srcName = getName();
        for (ClassOrInterfaceType relatedClass : decl.getExtendedTypes()) {
            String dstName = relatedClass.getNameAsString();
            Graph.addInheritance(srcName, dstName);
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
