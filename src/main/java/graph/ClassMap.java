package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ClassMap {
    private final HashMap<String, HashSet<String>> map;

    public ClassMap() {
        this.map = new HashMap<>();
    }

    public boolean hasRelation(String src, String dst) {
        /*查询是否存在src对dst的联系*/
        return map.containsKey(src) && map.get(src).contains(dst);
    }

    public void add(String src, String dst) {
        /*添加src对dst的联系*/
        if (src == null || dst == null)
            return;
        if (src.equals(dst))
            return;
        map.putIfAbsent(src, new HashSet<>());
        map.get(src).add(dst);
    }

    public void addAll(String src, HashSet<String> dsts) {
        for (String dst : dsts)
            add(src, dst);
    }

    public HashSet<String> get(String className) {
        //找爸爸
        return map.getOrDefault(className, new HashSet<>());
    }

    public HashSet<String> getReverse(String className) {
        //找儿子
        HashSet<String> result = new HashSet<>();
        for (String src : map.keySet()) {
            if (map.get(src).contains(className)) {
                result.add(src);
            }
        }
        return result;
    }


    public String generateString(String connectionSymbol) {
        /*生产字符串形式的输出*/
        StringBuilder sb = new StringBuilder();
        for (String src : map.keySet()) {
            for (String dst : map.get(src)) {
                sb.append(dst).append(connectionSymbol).append(src).append("\n");
            }
        }
        return sb.toString();
    }

    public String generateStringWithFilter(String connectionSymbol, ClassMap filterMap) {
        /*生产字符串形式的输出，过滤掉filterMap中已经存在的关系*/
        StringBuilder sb = new StringBuilder();
        for (String src : map.keySet()) {
            for (String dst : map.get(src)) {
                if (!filterMap.hasRelation(src, dst))
                    sb.append(dst).append(connectionSymbol).append(src).append("\n");
            }
        }
        return sb.toString();
    }

    public List<String> getKeys() {
        return new ArrayList<>(map.keySet());
    }

    public ClassMap MergeWith(ClassMap other) {
        for (String key : other.getKeys()) {
            addAll(key, other.get(key));
        }
        return this;
    }
}
