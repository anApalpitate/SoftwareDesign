package utils;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;

import java.util.Map;

public class ModifierUtils {
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
}