package utils;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;

public class ModifierUtils {
    public static String extractVisibility(NodeList<Modifier> modifiers, boolean isInterface) {
        if (modifiers.contains(Modifier.publicModifier())) {
            return "+"; //public
        } else if (modifiers.contains(Modifier.protectedModifier())) {
            return "#"; //protected
        } else if (modifiers.contains(Modifier.privateModifier())) {
            return "-"; //private
        }
        return isInterface ? "+" : "~"; //package-private
    }
}