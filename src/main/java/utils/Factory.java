package utils;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import graph.Graph;
import model.AbstractClassModel;
import model.ClassModel;
import model.EnumModel;
import model.InterfaceModel;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.EnumConstantDeclaration;



public class Factory {
    public static AbstractClassModel classFactory(BodyDeclaration declaration) {
        if (declaration instanceof ClassOrInterfaceDeclaration decl) {
            if (decl.isInterface()) {
                return new InterfaceModel(decl);
            } else {
                return new ClassModel(decl);
            }
        } else if (declaration instanceof EnumDeclaration decl) {
            return new EnumModel(decl);
        } else {
            System.out.println("Declaration is not a class or interface or enum");
            return null;
        }
    }

    public static BodyDeclaration<?> createTypeDeclaration(String name, String type, boolean isAbstract, String valueList) {
        switch (type.toLowerCase()) {
            case "class": {
                // 创建类
                ClassOrInterfaceDeclaration classDecl = new ClassOrInterfaceDeclaration();
                classDecl.setName(name);
                classDecl.setInterface(false); // 表示是 class
                classDecl.setModifiers(Modifier.Keyword.PUBLIC); // 先设为 public

                if (isAbstract) {
                    classDecl.addModifier(Modifier.Keyword.ABSTRACT);
                }

                return classDecl;
            }
            case "interface": {
                // 创建接口
                ClassOrInterfaceDeclaration interfaceDecl = new ClassOrInterfaceDeclaration();
                interfaceDecl.setName(name);
                interfaceDecl.setPublic(true);
                interfaceDecl.setInterface(true); // 表示是 interface
                return interfaceDecl;
            }
            case "enum": {
                // 创建枚举
                EnumDeclaration enumDecl = new EnumDeclaration();
                enumDecl.setName(name);
                enumDecl.setModifiers(Modifier.Keyword.PUBLIC);

                // 如果传入了 valueList，解析并添加枚举常量
                if (valueList != null && !valueList.isEmpty()) {
                    String[] values = valueList.split(",");
                    for (String value : values) {
                        enumDecl.addEntry(new EnumConstantDeclaration(value.trim()));
                    }
                }

                return enumDecl;
            }
            default:
                throw new IllegalArgumentException("不支持的类型: " + type + "（应为 class、interface 或 enum）");
        }
    }
    void f()
    {
        Graph a = GlobalVar.getGraph();
    }}
