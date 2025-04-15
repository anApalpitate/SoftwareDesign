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