package diagram;

// import java.io.File;
// import java.nio.file.Path;
// import java.util.List;

// public class ClassDiagram {
//     private ClassParser classParser;

//     ClassDiagram(Path path) {
//         File file = path.toFile();
//         try {
//             this.classParser = new ClassParser(file);
//         } catch (Exception e) {
//             e.printStackTrace();
//             System.exit(1);
//         }
//     }

//     public String generateUML() {
//         return classParser.generateUML();
//     }
    
//     public List<String> getCodeSmells() {
//         return classParser.getCodeSmells();
//     }

// }

// pzj-0413-part3
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import command.FieldOp;
import command.FunctionOp;
import command.ClassOp;
import command.Query;

public class ClassDiagram {
    private ClassParser classParser;
    private List<String> enabledAnalyzers;

    // 支持单个文件的构造函数
    ClassDiagram(Path path) {
        File file = path.toFile();
        try {
            this.classParser = new ClassParser(file);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        // 默认启用所有分析器
        enabledAnalyzers = Arrays.asList("ClassAnalyzer", "InheritanceTreeAnalyzer", "CircularDependencyAnalyzer", "DesignPatternAnalyzer");
    }

    // 支持多个文件的构造函数
    ClassDiagram(List<Path> paths) {
        try {
            this.classParser = new ClassParser(paths);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        // 默认启用所有分析器
        enabledAnalyzers = Arrays.asList("ClassAnalyzer", "InheritanceTreeAnalyzer", "CircularDependencyAnalyzer", "DesignPatternAnalyzer");
    }

    public void addClass(String name, boolean isAbstract){
        classParser.saveSnapshot();
        ClassOp.addClass(name, isAbstract, classParser.class_list);
    }

    public void addInterface(String name){
        classParser.saveSnapshot();
        ClassOp.addInterface(name, classParser.class_list);
    }

    public void addEnum(String name, String ValueList){
        classParser.saveSnapshot();
        ClassOp.addEnum(name, ValueList, classParser.class_list);
    }

    public void deleteClassInterfaceEnum(String name){
        classParser.saveSnapshot();
        ClassOp.deleteClassInterfaceEnum(name, classParser.class_list, classParser.graph);
    }

    public void addField(String target, String fieldName, String type, String access, boolean isStatic) {
        classParser.saveSnapshot();
        FieldOp.addField(target, fieldName, type, access, isStatic, classParser.class_list);
    }

    public void addFunction(String target, String functionName, String returnType, String params, String access, boolean isStatic, boolean isAbstract) {
        classParser.saveSnapshot();
        FunctionOp.addFunction(target, functionName, returnType, params, access, isStatic, isAbstract, classParser.class_list);
    }

    public void deleteField(String target, String fieldName) {
        classParser.saveSnapshot();
        FieldOp.deleteField(target, fieldName, classParser.class_list);
    }


    public void deleteFunction(String target, String functionName) {
        classParser.saveSnapshot();
        FunctionOp.deleteFunction(target, functionName, classParser.class_list);
    }


    public void modifyField(String target, String fieldName, String newName, String newType, String newAccess, boolean isStatic) {
        classParser.saveSnapshot();
        FieldOp.modifyField(target, fieldName, newName, newType, newAccess, isStatic, classParser.class_list);
    }


    public void modifyFunction(String target, String functionName, String newName, String newParams, String newReturn, String newAccess, boolean isStatic, boolean isAbstract) {
        classParser.saveSnapshot();
        FunctionOp.modifyFunction(target, functionName, newName, newParams, newReturn, newAccess, isStatic, isAbstract, classParser.class_list);
    }

    public String undo(){
        if (!classParser.undo()) {
            String result = "No command to undo";
            return result;
        }
        return null;
    }

    public String queryClass(String name, String hide) {
        return Query.queryClass(name, hide, classParser.class_list);
    }


    public String queryInterface(String name, String hide){
        return Query.queryInterface(name, hide, classParser.class_list);
    }

    public String queryEnum(String name, String hide){
        return Query.queryEnum(name, hide, classParser.class_list);
    }

    public String relate(String elementA, String elementB){
        return Query.relate(elementA, elementB, classParser.graph);
    }

    public String smellDetail(String elementName) {
        return Query.smellDetail(elementName, classParser);
    }


    public String generateUML() {
        return classParser.generateUML();
    }

    public List<String> getCodeSmells() {
        List<String> allSmells = classParser.getCodeSmells();
        List<String> filteredSmells = new ArrayList<>();

        // 定义每个分析器对应的固定字段
        Map<String, List<String>> analyzerPatterns = new HashMap<>();
        analyzerPatterns.put("ClassAnalyzer", List.of("God Class:", "Lazy Class:", "Data Class:"));
        analyzerPatterns.put("InheritanceTreeAnalyzer", List.of("Too Many Children:", "Inheritance Abuse:"));
        analyzerPatterns.put("CircularDependencyAnalyzer", List.of("Circular Dependency:"));
        analyzerPatterns.put("DesignPatternAnalyzer", List.of("Possible Design Patterns: Singleton Pattern", "Possible Design Patterns: Strategy Pattern"));

        for (String smell : allSmells) {
            for (String analyzer : enabledAnalyzers) {
                List<String> patterns = analyzerPatterns.get(analyzer);
                if (patterns != null) {
                    for (String pattern : patterns) {
                        if (smell.contains(pattern)) {
                            filteredSmells.add(smell);
                            break;
                        }
                    }
                }
            }
        }
        return filteredSmells;
        // return allSmells;
    }

    public void loadConfig(String configFile) {
        String fileExtension = configFile.substring(configFile.lastIndexOf(".") + 1);
        switch (fileExtension.toLowerCase()) {
            case "xml":
                // 解析 XML 配置文件
                try {
                    File xmlFile = new File(configFile);
                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(xmlFile);
                    doc.getDocumentElement().normalize();
    
                    NodeList analyzerNodes = doc.getElementsByTagName("analyzer");
                    enabledAnalyzers = new ArrayList<>();
                    for (int i = 0; i < analyzerNodes.getLength(); i++) {
                        Element analyzerElement = (Element) analyzerNodes.item(i);
                        enabledAnalyzers.add(analyzerElement.getTextContent());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // 如果配置文件解析失败，默认启用所有分析器
                    enabledAnalyzers = Arrays.asList("ClassAnalyzer", "InheritanceTreeAnalyzer", "CircularDependencyAnalyzer", "DesignPatternAnalyzer");
                }
                break;
            case "json":
                // 这里可以添加 JSON 解析逻辑
                break;
            case "yaml":
                // 这里可以添加 YAML 解析逻辑
                break;
            default:
                // 如果文件扩展名不支持，默认启用所有分析器
                enabledAnalyzers = Arrays.asList("ClassAnalyzer", "InheritanceTreeAnalyzer", "CircularDependencyAnalyzer", "DesignPatternAnalyzer");
        }
    }
}