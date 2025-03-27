package model;

import com.github.javaparser.ast.body.FieldDeclaration;
import utils.CommonUtil;

import java.util.List;

import static utils.CommonUtil.extractVisibility;

public class FieldModel extends BaseModel {
    private final String type;
    private final boolean isStatic;
    private final int cnt;

    FieldModel(FieldDeclaration field, String arg) {
        super(VariableToString(field), extractVisibility(field.getModifiers(), arg));
        this.type = field.getVariable(0).getTypeAsString().replaceAll(",", ", "); //每个逗号后加空格
        this.isStatic = field.isStatic();
        this.cnt = field.getVariables().size();
    }

    private static String VariableToString(FieldDeclaration field) {
        /*对于 int a,b,c; 这类声明方式,将其转化为字符串 "a, b, c" */
        String res = field.getVariables().toString();
        // 正则表达式去除变量赋值部分,如 int a = 10,b;
        res = res.replaceAll("=.*", "").trim();
        return res.replace("[", "").replace("]", "");
    }

    public List<String> getAssociations() {
        return CommonUtil.parseType(type);
    }


    public String generateString() {
        String staticModifier = isStatic ? "{static} " : "";
        return getVisibility() + " " + staticModifier + getName() + ": " + type;
    }

    public int getCnt() {
        return cnt;
    }
}
