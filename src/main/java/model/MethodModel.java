package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

import utils.CommonUtil;
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

    MethodModel(ConstructorDeclaration method, String arg) {
        super(method.getName().toString(), extractVisibility(method.getModifiers(), arg));

        this.returnType = "void";
        this.genericReturnType = "";

        this.isStatic = method.isStatic();
        this.isAbstract = method.isAbstract();
        this.isConstructor = method.isConstructorDeclaration();
        buildConstructorParam(method);
        parseConstructorDependencies(method);
    }
    
    MethodModel(MethodModel other){
        this.dependencies = new ArrayList<>(other.dependencies);
        this.name = other.name;
        this.visibility = other.visibility;
        this.parameterString = other.parameterString;
        this.isStatic = other.isStatic;
        this.genericReturnType = other.genericReturnType;
        this.isAbstract = other.isAbstract;
        this.isConstructor = other.isConstructor;
        this.returnType = other.returnType;
    }

    private void buildConstructorParam(ConstructorDeclaration method){
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

    private void parseConstructorDependencies(ConstructorDeclaration method){
        //参数依赖(已在buildParam中添加到dependencies)
        Set<String> set = new HashSet<>(dependencies);
        //返回值依赖
        set.addAll(CommonUtil.parseType(returnType));
        //局部变量依赖
        method.findAll(VariableDeclarationExpr.class).forEach(varDecl ->
                set.addAll(CommonUtil.parseType(varDecl.getVariable(0).getTypeAsString())));
        this.dependencies = new ArrayList<>(set);
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

    public String getReturnType(){
        return returnType;
    }

    public String getParameterList(){
        return parameterString;
    }

    // 添加 isStatic() 方法
    public boolean isStatic() {
        return isStatic;
    }

}
