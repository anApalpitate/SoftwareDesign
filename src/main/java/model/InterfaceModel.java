package model;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

public class InterfaceModel extends AbstractClassModel {
    public InterfaceModel(ClassOrInterfaceDeclaration classOrInterface) {
        super(classOrInterface);
        parseMethods(classOrInterface);
        parseFields(classOrInterface);
        SortFieldsAndMethods();
    }

    void parseFields(ClassOrInterfaceDeclaration classOrInterface) {
    }

    void parseMethods(ClassOrInterfaceDeclaration classOrInterface) {
        List<MethodModel> methods = new ArrayList<>();
        for (MethodDeclaration method : classOrInterface.getMethods()) {
            methods.add(new MethodModel(method, "interface"));
        }
        this.methods = methods;
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
