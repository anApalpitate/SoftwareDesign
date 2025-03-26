package model;

import com.github.javaparser.ast.body.MethodDeclaration;

import static utils.CommonUtil.extractVisibility;

public class MethodModel extends BaseModel {
    private final String returnType;
    private final boolean isStatic;
    private final boolean isAbstract;
    private final boolean isConstructor;
    private final String genericReturnType;
    private final String className;
    private String parameters;

    public MethodModel(MethodDeclaration method, String className, String arg) {
        super(method.getName().toString(), extractVisibility(method.getModifiers(), arg));

        this.returnType = method.getType().asString().replaceAll(",", ", ");
        this.isStatic = method.isStatic();
        this.isAbstract = method.isAbstract();
        this.isConstructor = method.isConstructorDeclaration();
        this.className = className;
        this.genericReturnType = extractGenericReturnType(method);
        this.buildParam(method);
    }

    private void buildParam(MethodDeclaration method) {
        StringBuilder paramBuilder = new StringBuilder();
        method.getParameters().forEach(param -> {
            if (!paramBuilder.isEmpty()) {
                paramBuilder.append(", ");
            }
            String paramName = param.getNameAsString();
            String paramType = param.getType().asString().replaceAll(",", ", ");
            paramBuilder.append(paramName).append(": ").append(paramType);
        });
        this.parameters = paramBuilder.toString();
    }

    private String extractGenericReturnType(MethodDeclaration method) {
        if (method.getTypeParameters().isEmpty())
            return "";
        String res = method.getTypeParameters().toString();
        return res.replace("[", "<").replace("]", ">");
    }

    public String generateString() {
        if (isConstructor) {
            return "";
        }
        String visibility = getVisibility() + " ";
        String staticModifier = isStatic ? "{static} " : "";
        String abstractModifier = isAbstract ? "{abstract} " : "";
        String genericModifier = genericReturnType.isEmpty() ? "" : genericReturnType + " ";
        return visibility + genericModifier + staticModifier + abstractModifier + getName() + "(" + parameters + "): " + returnType;
    }

    public boolean isConstructor() {
        return isConstructor;
    }

    public String getClassName() {
        return className;
    }
}
