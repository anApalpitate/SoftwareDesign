package model;

import com.github.javaparser.ast.body.FieldDeclaration;

import static utils.CommonUtil.extractVisibility;

public class FieldModel extends BaseModel {
    private final String type;
    private final boolean isStatic;

    public FieldModel(FieldDeclaration field, String arg) {
        super(getVariableName(field), extractVisibility(field.getModifiers(), arg));
        this.type = field.getVariable(0).getTypeAsString().replaceAll(",", ", "); //每个逗号后加空格
        this.isStatic = field.isStatic();
    }

    private static String getVariableName(FieldDeclaration field) {
        String res = field.getVariables().toString();
        res = res.replaceAll("=.*", "").trim();  // 正则表达式去除变量赋值
        return res.replace("[", "").replace("]", "");
    }

    public String generateString() {
        String staticModifier = isStatic ? "{static} " : "";
        return getVisibility() + " " + staticModifier + getName() + ": " + type;
    }
}
