package command;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import model.AbstractClassModel;
import model.ClassModel;
import model.FieldModel;
import utils.CommonUtil;

import java.util.List;
import java.util.Optional;

public class FieldOp {
    public static void addField(String target, String fieldName, String type, String access, boolean isStatic, List<AbstractClassModel> class_list) {
        // 找到目标类
        Optional<ClassModel> targetOpt = CommonUtil.findTargetClass(target, class_list);
        ClassModel model = targetOpt.get();

        // 构造 JavaParser 的 FieldDeclaration
        FieldDeclaration field = new FieldDeclaration();
        field.addVariable(new VariableDeclarator(
                StaticJavaParser.parseType(type),
                fieldName
        ));

        // 添加访问修饰符
        if (access == null) {
            field.setPrivate(true);
        } else {
            switch (access) {
                case "+" -> field.setPublic(true);
                case "#" -> field.setProtected(true);
                case "-" -> field.setPrivate(true);
            }
        }

        // 添加 static
        if (isStatic) {
            field.setStatic(true);
        }

        // 添加字段到类中
        model.addField(field);
        model.SortFieldsAndMethods();
        CommonUtil.sortClassList(class_list);
    }

    public static void deleteField(String target, String fieldName, List<AbstractClassModel> class_list) {
        Optional<ClassModel> targetOpt = CommonUtil.findTargetClass(target, class_list);
        ClassModel model = targetOpt.get();

        model.deleteField(fieldName);
        model.SortFieldsAndMethods();
        CommonUtil.sortClassList(class_list);
    }

    public static void modifyField(String target, String fieldName, String newName, String newType, String newAccess, Boolean isStatic, List<AbstractClassModel> class_list) {
        // 找到目标类
        Optional<ClassModel> targetOpt = CommonUtil.findTargetClass(target, class_list);
        ClassModel model = targetOpt.get();

        // 获取原字段
        FieldModel originalField = model.getFields().stream()
                .filter(f -> f.getName().equals(fieldName))
                .findFirst()
                .orElse(null);

        if (originalField == null) return;

        // 删除原字段
        model.deleteField(fieldName);

        // 保留原字段属性
        String name = newName != null ? newName : originalField.getName();
        String type = newType != null ? newType : originalField.getType();
        String access = newAccess != null ? newAccess : originalField.getVisibility();

        // 构造新字段
        FieldDeclaration field = new FieldDeclaration();
        field.addVariable(new VariableDeclarator(
                StaticJavaParser.parseType(type),
                name
        ));

        switch (access) {
            case "+" -> field.setPublic(true);
            case "#" -> field.setProtected(true);
            default -> field.setPrivate(true);
        }

        // static 默认 false，不继承原字段 static 属性
        if (isStatic != null && isStatic) {
            field.setStatic(true);
        }

        model.addField(field);
        model.SortFieldsAndMethods();
        CommonUtil.sortClassList(class_list);
    }
}
