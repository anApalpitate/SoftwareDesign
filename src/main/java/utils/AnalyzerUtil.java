package utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import graph.ClassMap;
import graph.Graph;

public class AnalyzerUtil {
    public void DfsCheckDepth(String className, LinkedList<String> path, final Graph graph, List<String> output) {
        // 过深继承树
        path.add(className);
        if (path.size() >= 6) {
            StringBuilder sb = new StringBuilder("Inheritance Abuse: ");
            sb.append(path.get(0));
            for (int i = 1; i < path.size(); i++) {
                sb.append(" <|-- ");
                sb.append(path.get(i));
            }
            output.add(sb.toString());
        }
        for (String child : graph.getReverseInheritance(className)) {
            DfsCheckDepth(child, path, graph, output);
        }
        path.removeLast();
    }

    public boolean IsCycleByDFS(final String node, final ClassMap classMap,
                                Set<String> visited, Set<String> stack, LinkedList<String> path) {
        if (stack.contains(node)) /*栈内节点重复，存在环*/
        {    
            path.add(node);
            return true;
        }
        if (visited.contains(node))/*节点已访问，跳过*/
            return false;
        visited.add(node);//当前节点已访问
        stack.add(node);//当前节点入栈
        path.add(node);
        for (String adj : classMap.get(node)) {
            // System.out.println(node + "de dad are:" +adj + "\n");
            if (IsCycleByDFS(adj, classMap, visited, stack, path))
                return true;
        }
        stack.remove(node);
        path.removeLast();
        return false;
    }

    public LinkedList<String> extractTrailingSequence(LinkedList<String> path) {
        if (path == null || path.isEmpty()) {
            return new LinkedList<>();
        }
    
        String lastElement = path.getLast();
        int startIndex = -1;
    
        // 从后向前找第一个与最后一个元素相同的元素的位置
        for (int i = path.size() - 2; i >= 0; i--) {
            if (path.get(i).equals(lastElement)) {
                startIndex = i;
                break;
            }
        }
    
        if (startIndex == -1) {
            return new LinkedList<>();
        }
    
        // 获取子列表
        LinkedList<String> subList = new LinkedList<>(path.subList(startIndex, path.size()));
        
        return subList;
    }

    // public String formatCycleOutput(List<String> path) {
    //     // path: [..., X, ..., Y, Z, X]
    //     // 该函数中将path并格式化为 Circular Dependency: <A类名> <.. <B类名> <.. <C类名> <.. ... <.. <A类名>
    //     StringBuilder sb = new StringBuilder("Circular Dependency: ");
    //     int n = path.size();
    //     int startIdx = path.get(0).equals("X") ? 1 : 0; //起始位置可以自行更改
    //     sb.append(path.get(startIdx));
    //     for (int i = 0; i < n; i++) {
    //         sb.append(" <.. ");
    //         //沿path向前
    //         startIdx = (startIdx + n - 1) % n;
    //         sb.append(path.get(startIdx));
    //     }
    //     return sb.toString();
    // }

    public String formatCycleOutput(List<String> path) {
        // path: [X, ..., Y, Z, X]
        // 该函数中将path并格式化为 Circular Dependency: <A类名> <.. <B类名> <.. <C类名> <.. ... <.. <A类名>
        StringBuilder sb = new StringBuilder("Circular Dependency: ");
        int n = path.size();
        sb.append(path.get(n-1));
        for (int i = n-2; i >= 0; i--) {
            sb.append(" <.. ");
            sb.append(path.get(i));
        }
        return sb.toString();
    }
}
