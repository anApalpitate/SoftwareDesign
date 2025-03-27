package utils;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import model.AbstractClassModel;
import model.InterfaceModel;

import java.util.*;

public class CommonUtil {
    public static String extractVisibility(NodeList<Modifier> modifiers, String type) {
        /*依据输入的修饰符列表，返回对应的可访问性字符串
         * type: 表示类型的补充说明符
         */
        if (modifiers.contains(Modifier.publicModifier())) {
            return "+"; //public
        } else if (modifiers.contains(Modifier.protectedModifier())) {
            return "#"; //protected
        } else if (modifiers.contains(Modifier.privateModifier())) {
            return "-"; //private
        }
        /*默认情况分类*/
        return switch (type) {
            case "interface", "enum_method" -> "+";
            case "enum_field" -> "-";
            default -> "~";
        };
    }

    public static int visibilityOrder(String visibility1, String visibility2) {
        /*比较访问符的优先级，用于对访问符的排序*/
        Map<String, Integer> visibilityOrder = Map.of(
                "-", 0,
                "#", 1,
                "~", 2,
                "+", 3
        );
        return Integer.compare(visibilityOrder.getOrDefault(visibility1, 4),
                visibilityOrder.getOrDefault(visibility2, 4));
    }

    public static void sortClassList(List<AbstractClassModel> classList) {
        /*目标：将Interface排在最后，其余类顺序不变*/
        classList.sort((model1, model2) -> {
            if (model1 instanceof InterfaceModel && !(model2 instanceof InterfaceModel)) {
                return 1;
            } else if (!(model1 instanceof InterfaceModel) && model2 instanceof InterfaceModel) {
                return -1;
            }
            return 0;
        });
    }

    public static boolean isBasicType(String type) {
        switch (type) {
            //基础类型
            case "int", "long", "float", "double", "boolean", "char", "byte", "short", "void",
                 "Integer", "Long", "Float", "Double", "Boolean", "Character", "Byte", "Short", "String" -> {
                return true;
            }
            //内置容器类
            case "Set", "List", "Map", "ArrayList", "Stack", "LinkedList", "HashSet", "HashMap", "Queue " -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    public static List<String> parseType(String type) {
        /*将类型字符串解析成类型列表
         * 例: List<Map<String, Student>> → [List, Map, String, Student]
         */
        Set<String> TypeSet = new HashSet<String>() {
        };
        if (type == null || type.isEmpty()) {
            return new ArrayList<>();
        }
        StringBuilder sb = new StringBuilder();
        /*线性读取并拆分出完整的类型*/
        for (char c : type.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                sb.append(c);
            } else {
                if (!sb.isEmpty()) {
                    String name = sb.toString();
                    TypeSet.add(name);
                    sb.setLength(0);
                }
            }
        }
        if (!sb.isEmpty()) {
            String finalType = sb.toString();
            TypeSet.add(finalType);
        }
        return new ArrayList<>(TypeSet);
    }
}