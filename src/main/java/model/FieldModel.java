package model;

import com.github.javaparser.ast.body.FieldDeclaration;

import static utils.CommonUtil.extractVisibility;

public class FieldModel extends BaseModel {
    private final String type;
    private final boolean isStatic;

    public FieldModel(FieldDeclaration field, String arg) {
        super(getVariableName(field), extractVisibility(field.getModifiers(), arg));
        this.type = field.getVariable(0).getTypeAsString();
        this.isStatic = field.isStatic();
    }

    private static String getVariableName(FieldDeclaration field) {
        String res = field.getVariables().toString();
        return res.substring(1, res.length() - 1);
    }

    public String generateString() {
        String staticModifier = isStatic ? "{static} " : "";
        return getVisibility() + " " + staticModifier + getName() + ": " + type;
    }
}
