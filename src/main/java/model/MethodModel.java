package model;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import utils.CommonUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static utils.CommonUtil.extractVisibility;

public class MethodModel extends BaseModel {
    private final String returnType;
    private final String genericReturnType;
    private final boolean isStatic;
    private final boolean isAbstract;
    private final boolean isConstructor;
    private String parameterString;
    private List<String> dependencies;

    MethodModel(MethodDeclaration method, String arg) {
        super(method.getName().toString(), extractVisibility(method.getModifiers(), arg));

        this.returnType = method.getType().asString().replaceAll(",", ", ");
        this.genericReturnType = extractGenericReturnType(method);

        this.isStatic = method.isStatic();
        this.isAbstract = method.isAbstract();
        this.isConstructor = method.isConstructorDeclaration();
        buildParam(method);
        parseDependencies(method);
    }

    private void parseDependencies(MethodDeclaration method) {
        //参数依赖(已在buildParam中添加到dependencies)
        Set<String> set = new HashSet<>(dependencies);
        //返回值依赖
        set.addAll(CommonUtil.parseType(returnType));
        //局部变量依赖
        method.findAll(VariableDeclarationExpr.class).forEach(varDecl ->
                set.addAll(CommonUtil.parseType(varDecl.getVariable(0).getTypeAsString())));
        this.dependencies = new ArrayList<>(set);
    }

    private void buildParam(MethodDeclaration method) {
        StringBuilder paramBuilder = new StringBuilder();
        StringBuilder TypeBuilder = new StringBuilder();
        method.getParameters().forEach(param -> {
            if (!paramBuilder.isEmpty()) {
                paramBuilder.append(", ");
                TypeBuilder.append(",");
            }
            String paramName = param.getNameAsString();
            String paramType = param.getType().asString().replaceAll(",", ", ");
            TypeBuilder.append(paramType);
            paramBuilder.append(paramName).append(": ").append(paramType);
        });
        this.parameterString = paramBuilder.toString();
        this.dependencies = CommonUtil.parseType(TypeBuilder.toString());
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
        return visibility + genericModifier + staticModifier + abstractModifier + getName() + "(" + parameterString + "): " + returnType;
    }

    public boolean isConstructor() {
        return isConstructor;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

}
