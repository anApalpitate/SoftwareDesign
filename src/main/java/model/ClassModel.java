package model;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ClassModel extends BaseModel {
    private final boolean isInterface;
    private List<FieldModel> fields;
    private List<MethodModel> methods;

    public ClassModel(ClassOrInterfaceDeclaration classOrInterface) {
        super(classOrInterface.getNameAsString(), classOrInterface.getModifiers().toString());
        this.isInterface = classOrInterface.isInterface();
        parseFields(classOrInterface);
        parseMethods(classOrInterface);
    }

    public boolean isInterface() {
        return isInterface;
    }

    private void parseFields(ClassOrInterfaceDeclaration classOrInterface) {
        List<FieldModel> fields = new ArrayList<>();
        for (FieldDeclaration field : classOrInterface.getFields()) {
            FieldModel fieldModel = new FieldModel(field, isInterface);
            fields.add(fieldModel);
        }
        this.fields = fields;
    }

    private void parseMethods(ClassOrInterfaceDeclaration classOrInterface) {
        List<MethodModel> methods = new ArrayList<>();
        for (MethodDeclaration method : classOrInterface.getMethods()) {
            methods.add(new MethodModel(method, isInterface));  // 调用 MethodClass 来解析方法
        }
        this.methods = methods;
    }

    private void SortFieldsAndMethods() {
        fields.sort(Comparator.comparing(FieldModel::getVisibility, this::SortOrder));
        methods.sort(Comparator.comparing(MethodModel::getVisibility, this::SortOrder));
    }

    private int SortOrder(String visibility1, String visibility2) {
        Map<String, Integer> visibilityOrder = Map.of(
                "-", 0,
                "#", 1,
                "~", 2,
                "+", 3
        );
        return Integer.compare(visibilityOrder.getOrDefault(visibility1, 4),
                visibilityOrder.getOrDefault(visibility2, 4));
    }

    @Override
    public String toString() {
        String blank = "    ";
        StringBuilder umlBuilder = new StringBuilder();

        String Prefix = isInterface ? "interface" : "class";
        umlBuilder.append(Prefix).append(" ").append(getName()).append(" {\n");
        SortFieldsAndMethods();
        if (!isInterface)
            for (FieldModel field : fields) {
                umlBuilder.append(blank).append(field.toString()).append("\n");
            }
        for (MethodModel method : methods) {
            umlBuilder.append(blank).append(method.toString()).append("\n");
        }
        umlBuilder.append("}\n");
        return umlBuilder.toString();
    }

}
