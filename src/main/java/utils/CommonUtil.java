package utils;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import model.AbstractClassModel;
import model.InterfaceModel;

import java.util.List;
import java.util.Map;

public class CommonUtil {
    public static String extractVisibility(NodeList<Modifier> modifiers, String type) {
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
        //Interface在最后，其余类顺序不变
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
            case "int", "long", "float", "double", "boolean", "char", "byte", "short", "void",
                 "Integer", "Long", "Float", "Double", "Boolean", "Character", "Byte", "Short", "String" -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }


}