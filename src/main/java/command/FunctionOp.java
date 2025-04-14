package command;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.Type;
import model.AbstractClassModel;
import model.ClassModel;
import model.MethodModel;
import utils.CommonUtil;

import java.util.List;
import java.util.Optional;

public class FunctionOp {
    public static void addFunction(String target, String functionName, String returnType, String params, String access, boolean isStatic, boolean isAbstract, List<AbstractClassModel> class_list) {
        // 找到目标类（已知类名唯一）
        Optional<ClassModel> targetOpt = CommonUtil.findTargetClass(target, class_list);
        ClassModel model = targetOpt.get();

        // 构建方法的类型
        Type returnTypeParsed = StaticJavaParser.parseType(returnType);

        // 构造 JavaParser 的 MethodDeclaration
        MethodDeclaration method = new MethodDeclaration();
        method.setName(functionName);
        method.setType(returnTypeParsed);

        // 设置访问修饰符
        if (access == null) {
            method.setPrivate(true);
        } else {
            switch (access) {
                case "+" -> method.setPublic(true);
                case "#" -> method.setProtected(true);
                case "-" -> method.setPrivate(true);
            }
        }

        // 设置 static 或 abstract
        if (isStatic) {
            method.setStatic(true);
        }
        if (isAbstract) {
            method.setAbstract(true);
        }

        // 处理方法参数
        String[] paramList = params.split(",");
        for (String param : paramList) {
            String[] parts = param.trim().split(":");
            if (parts.length == 2) {
                String paramName = parts[0].trim();
                String paramType = parts[1].trim();
                Parameter parameter = new Parameter(StaticJavaParser.parseType(paramType), paramName);
                method.addParameter(parameter);
            }
        }

        // 将方法添加到类中
        model.addMethod(method);
        CommonUtil.sortClassList(class_list);
    }

    public static void deleteFunction(String target, String functionName, List<AbstractClassModel> class_list) {
        Optional<ClassModel> targetOpt = CommonUtil.findTargetClass(target, class_list);
        ClassModel model = targetOpt.get();

        model.deleteMethod(functionName);
        CommonUtil.sortClassList(class_list);
    }

    public static void modifyFunction(String target, String functionName, String newName, String newParams, String newReturn, String newAccess, Boolean isStatic, Boolean isAbstract, List<AbstractClassModel> class_list) {
        Optional<ClassModel> targetOpt = CommonUtil.findTargetClass(target, class_list);
        ClassModel model = targetOpt.get();

        // 找到原方法模型
        MethodModel originalMethodModel = model.getMethods().stream()
                .filter(m -> m.getName().equals(functionName))
                .findFirst()
                .orElse(null);

        if (originalMethodModel == null) return;

        // 删除原方法
        model.deleteMethod(functionName);

        // 设置新名称、返回类型、参数（若为空则使用原值）
        String methodName = newName != null ? newName : functionName;
        String returnType = newReturn != null ? newReturn : originalMethodModel.getReturnType();
        String paramListStr = newParams != null ? newParams : originalMethodModel.getParameterList();

        // 设置访问修饰符（若为 null 则继承原值）
        String access = newAccess != null ? newAccess : originalMethodModel.getVisibility();

        MethodDeclaration method = new MethodDeclaration();
        method.setName(methodName);
        method.setType(StaticJavaParser.parseType(returnType));

        switch (access) {
            case "+" -> method.setPublic(true);
            case "#" -> method.setProtected(true);
            default -> method.setPrivate(true);
        }

        // 设置 static/abstract，未指定默认 false（不继承原值）
        method.setStatic(isStatic != null && isStatic);
        method.setAbstract(isAbstract != null && isAbstract);

        // 设置参数（使用 name: type 格式）
        if (!paramListStr.isBlank()) {
            String[] paramList = paramListStr.split(",");
            for (String param : paramList) {
                String[] parts = param.trim().split(":");
                if (parts.length == 2) {
                    String paramName = parts[0].trim();
                    String paramType = parts[1].trim();
                    Parameter parameter = new Parameter(StaticJavaParser.parseType(paramType), paramName);
                    method.addParameter(parameter);
                }
            }
        }

        model.addMethod(method);
        model.SortFieldsAndMethods();
        CommonUtil.sortClassList(class_list);
    }
}
