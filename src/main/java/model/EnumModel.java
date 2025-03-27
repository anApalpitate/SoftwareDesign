package model;

import com.github.javaparser.ast.body.*;
import graph.Graph;
import utils.CommonUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnumModel extends AbstractClassModel {
    private final List<String> constants;
    private Set<String> AssociatedClasses;
    private Set<String> DependedClasses;

    public EnumModel(EnumDeclaration decl) {
        super(decl);
        this.constants = new ArrayList<>();

        parseConstants(decl);
        parseFields(decl);
        parseMethods(decl);
        SortFieldsAndMethods();

        addAssociations();
        addDependencies();
    }

    /*解析枚举类的常数部分*/
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
        AssociatedClasses = new HashSet<>();
        if (declaration instanceof EnumDeclaration enumDecl) {
            for (FieldDeclaration field : enumDecl.getFields()) {
                FieldModel fieldModel = new FieldModel(field, "enum_field");
                fields.add(fieldModel);
                AssociatedClasses.addAll(fieldModel.getAssociations());
            }
        }
    }

    void parseMethods(BodyDeclaration declaration) {
        DependedClasses = new HashSet<>();
        if (declaration instanceof EnumDeclaration enumDecl) {
            for (MethodDeclaration methodDecl : enumDecl.getMethods()) {
                MethodModel methodModel = new MethodModel(methodDecl, "enum_method");
                methods.add(methodModel);
                DependedClasses.addAll(methodModel.getDependencies());
            }
        }
    }

    private void addAssociations() {
        for (String associatedClass : AssociatedClasses) {
            if (CommonUtil.isBasicType(associatedClass))
                continue;
            Graph.addAssociation(getName(), associatedClass);
        }
    }

    private void addDependencies() {
        for (String dependedClass : DependedClasses) {
            if (CommonUtil.isBasicType(dependedClass))
                continue;
            Graph.addDependency(getName(), dependedClass);
        }
    }


    public String generateString() {
        String blank = "    ";
        StringBuilder sb = new StringBuilder();
        sb.append("enum ").append(getName()).append(" {\n");
        for (String constant : constants)
            sb.append(blank).append(constant).append("\n");

        for (FieldModel field : fields)
            sb.append(blank).append(field.generateString()).append("\n");
        for (MethodModel method : methods)
            sb.append(blank).append(method.generateString()).append("\n");

        sb.append("}\n");
        return sb.toString();
    }
}

