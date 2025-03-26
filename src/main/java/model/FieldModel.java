package model;

import com.github.javaparser.ast.body.FieldDeclaration;

import static utils.ModifierUtils.extractVisibility;

public class FieldModel extends BaseModel {
    private final String type;
    private final boolean isStatic;

    public FieldModel(FieldDeclaration field, String arg) {
        super(field.getVariable(0).getNameAsString(), extractVisibility(field.getModifiers(), arg));
        this.type = field.getVariable(0).getTypeAsString();
        this.isStatic = field.isStatic();
    }

    @Override
    public String toString() {
        String staticModifier = isStatic ? "{static} " : "";
        return getVisibility() + " " + staticModifier + getName() + ": " + type;
    }
}
