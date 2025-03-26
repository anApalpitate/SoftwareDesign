package model;

import com.github.javaparser.ast.body.MethodDeclaration;

import static utils.CommonUtil.extractVisibility;

public class MethodModel extends BaseModel {
    private final String returnType;
    private final boolean isStatic;
    private final boolean isAbstract;
    private String parameters;

    public MethodModel(MethodDeclaration method, String arg) {
        super(method.getName().toString(), extractVisibility(method.getModifiers(), arg));
        this.returnType = method.getType().asString();
        this.isStatic = method.isStatic();
        this.isAbstract = method.isAbstract();
        this.buildParam(method);
    }

    private void buildParam(MethodDeclaration method) {
        StringBuilder paramBuilder = new StringBuilder();
        method.getParameters().forEach(param -> {
            if (!paramBuilder.isEmpty()) {
                paramBuilder.append(", ");
            }
            paramBuilder.append(param.getNameAsString()).append(": ").append(param.getType().asString());
        });
        this.parameters = paramBuilder.toString();
    }

    public String generateString() {
        // 排除构造函数
        if (this.getName().equals(this.getVisibility())) {
            return "";
        }
        String visibility = getVisibility() + " ";
        String staticModifier = isStatic ? "{static} " : "";
        String abstractModifier = isAbstract ? "{abstract} " : "";
        //WARNING:注意空格
        return visibility + staticModifier + abstractModifier + getName() + "(" + parameters + "): " + returnType;
    }

}
