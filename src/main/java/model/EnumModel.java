package model;

import com.github.javaparser.ast.body.*;

import java.util.ArrayList;
import java.util.List;

public class EnumModel extends AbstractClassModel {
    private final List<String> constants;

    public EnumModel(EnumDeclaration decl) {
        super(decl);
        this.constants = new ArrayList<>();

        parseConstants(decl);
        parseFields(decl);
        parseMethods(decl);
        SortFieldsAndMethods();
    }

    private void parseConstants(EnumDeclaration declaration) {
        for (EnumConstantDeclaration constant : declaration.getEntries()) {
            if (constant.getArguments().isEmpty()) {
                constants.add(constant.getNameAsString());
            } else {
                String constantWithValue = constant.toString();
                constants.add(constantWithValue);
            }
        }
    }

    void parseFields(BodyDeclaration declaration) {
        if (declaration instanceof EnumDeclaration enumDecl) {
            for (FieldDeclaration field : enumDecl.getFields()) {
                fields.add(new FieldModel(field, "enum_field"));
            }
        }
    }

    void parseMethods(BodyDeclaration declaration) {
        if (declaration instanceof EnumDeclaration enumDecl) {
            for (MethodDeclaration methodDecl : enumDecl.getMethods()) {
                methods.add(new MethodModel(methodDecl, "enum_method"));
            }
        }
    }


    public String generateString() {
        String blank = "    ";
        StringBuilder sb = new StringBuilder();
        sb.append("enum ").append(getName()).append(" {\n");
        for (String constant : constants)
            sb.append(blank).append(constant).append(",\n");

        for (FieldModel field : fields)
            sb.append(blank).append(field.generateString()).append("\n");
        for (MethodModel method : methods)
            sb.append(blank).append(method.generateString()).append("\n");

        sb.append("}\n");
        return sb.toString();
    }
}

