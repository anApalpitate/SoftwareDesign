package graph;

import java.util.HashMap;
import java.util.HashSet;

public class ClassMap {
    private HashMap<String, HashSet<String>> map;

    public ClassMap() {
        this.map = new HashMap<>();
    }

    public boolean hasRelation(String src, String dst) {
        return map.containsKey(src) && map.get(src).contains(dst);
    }

    public void add(String src, String dst) {
        if (src == null || dst == null)
            return;
        if (src.equals(dst))
            return;
        map.putIfAbsent(src, new HashSet<>());
        map.get(src).add(dst);
    }

    public HashSet<String> get(String className) {
        return map.getOrDefault(className, new HashSet<>());
    }

    public String generateString(String connectionSymbol) {
        StringBuilder sb = new StringBuilder();
        for (String src : map.keySet()) {
            for (String dst : map.get(src)) {
                sb.append(dst).append(connectionSymbol).append(src).append("\n");
            }
        }
        return sb.toString();
    }

    public String generateStringWithFilter(String connectionSymbol, ClassMap classMap) {
        StringBuilder sb = new StringBuilder();
        for (String src : map.keySet()) {
            for (String dst : map.get(src)) {
                if (!classMap.hasRelation(src, dst))
                    sb.append(dst).append(connectionSymbol).append(src).append("\n");
            }
        }
        return sb.toString();
    }
}
