package model;

import com.github.javaparser.ast.body.MethodDeclaration;

import static utils.ModifierUtils.extractVisibility;

public class MethodModel extends BaseModel {
    private final String returnType;
    private final String parameters;
    private final boolean isStatic;

    public MethodModel(MethodDeclaration method, boolean isInterface) {
        super(method.getName().toString(), extractVisibility(method.getModifiers(), isInterface));
        this.returnType = method.getType().asString();
        this.isStatic = method.isStatic();
        StringBuilder paramBuilder = new StringBuilder();
        method.getParameters().forEach(param -> {
            if (!paramBuilder.isEmpty()) {
                paramBuilder.append(", ");
            }
            paramBuilder.append(param.getNameAsString()).append(": ").append(param.getType().asString());
        });
        this.parameters = paramBuilder.toString();
    }


    @Override
    public String toString() {
        // 排除构造函数
        if (this.getName().equals(this.getVisibility())) {
            return "";
        }
        String visibility = getVisibility();
        String staticModifier = isStatic ? "{static} " : "";
        return visibility + " " + staticModifier + getName() + "(" + parameters + "): " + returnType;
    }
}
