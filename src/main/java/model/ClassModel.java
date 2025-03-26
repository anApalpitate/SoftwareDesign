package model;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import utils.ModifierUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ClassModel extends BaseModel {
    private final boolean isInterface;
    private final boolean isAbstract;
    private List<FieldModel> fields;
    private List<MethodModel> methods;


    public ClassModel(ClassOrInterfaceDeclaration classOrInterface) {
        super(classOrInterface.getNameAsString(), classOrInterface.getModifiers().toString());
        this.isInterface = classOrInterface.isInterface();
        this.isAbstract = classOrInterface.isAbstract();
        parseFields(classOrInterface);
        parseMethods(classOrInterface);
    }

    public boolean isInterface() {
        return isInterface;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    private void parseFields(ClassOrInterfaceDeclaration classOrInterface) {
        List<FieldModel> fields = new ArrayList<>();
        for (FieldDeclaration field : classOrInterface.getFields()) {
            FieldModel fieldModel = new FieldModel(field, isInterface ? "interface" : "");
            fields.add(fieldModel);
        }
        this.fields = fields;
    }

    private void parseMethods(ClassOrInterfaceDeclaration classOrInterface) {
        List<MethodModel> methods = new ArrayList<>();
        for (MethodDeclaration method : classOrInterface.getMethods()) {
            methods.add(new MethodModel(method, isInterface ? "interface" : ""));  // 调用 MethodClass 来解析方法
        }
        this.methods = methods;
    }

    private void SortFieldsAndMethods() {
        fields.sort(Comparator.comparing(FieldModel::getVisibility, ModifierUtils::visibilityOrder));
        methods.sort(Comparator.comparing(MethodModel::getVisibility, ModifierUtils::visibilityOrder));
    }


    @Override
    public String toString() {
        String blank = "    ";
        StringBuilder sb = new StringBuilder();
        String AbstractStr = isAbstract ? "abstract " : "";
        String Prefix = isInterface ? "interface " : "class ";
        sb.append(AbstractStr);
        //WARNING: 注意检查空格
        sb.append(Prefix).append(getName()).append(" {\n");
        SortFieldsAndMethods();
        if (!isInterface)
            for (FieldModel field : fields) {
                sb.append(blank).append(field.toString()).append("\n");
            }
        for (MethodModel method : methods) {
            sb.append(blank).append(method.toString()).append("\n");
        }
        sb.append("}\n");
        return sb.toString();
    }

}
