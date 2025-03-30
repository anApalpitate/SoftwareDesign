// package diagram;

// import java.io.IOException;
// import java.nio.file.Path;

// public class ClassDiagramGenerator {
//     // 解析源文件路径，返回ClassDiagram对象
//     public ClassDiagram parse(Path sourcePath) throws IOException {
//         // 返回ClassDiagram对象
//         return null;
//     }
// }

package diagram;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class ClassDiagramGenerator {
    public ClassDiagram parse(Path sourcePath) throws IOException {
        // 读取文件内容
        String fileContent = Files.readString(sourcePath);
        
        // 创建JavaParser实例（非静态方式）
        JavaParser javaParser = new JavaParser();
        
        // 解析Java文件，得到ParseResult
        ParseResult<CompilationUnit> parseResult = javaParser.parse(fileContent);
        
        // 检查解析是否成功
        if (!parseResult.isSuccessful() || !parseResult.getResult().isPresent()) {
            throw new IOException("Failed to parse Java file");
        }
        
        // 获取解析结果中的CompilationUnit
        CompilationUnit cu = parseResult.getResult().get();
        
        ClassDiagram diagram = new ClassDiagram();

        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(type -> {
            if (type.isInterface()) {
                diagram.addInterface(type);
            } else {
                diagram.addClass(type);
            }

            // 处理继承关系
            if (type.getExtendedTypes().isNonEmpty()) {
                for (ClassOrInterfaceType extendedType : type.getExtendedTypes()) {
                    diagram.addRelationship(new ClassDiagram.Relationship(
                        extendedType.getNameAsString(),
                        "<|--",
                        type.getNameAsString()
                    ));
                }
            }

            // 处理实现关系
            if (type.getImplementedTypes().isNonEmpty()) {
                for (ClassOrInterfaceType implementedType : type.getImplementedTypes()) {
                    diagram.addRelationship(new ClassDiagram.Relationship(
                        implementedType.getNameAsString(),
                        "<|..",
                        type.getNameAsString()
                    ));
                }
            }
        });

        return diagram;
    }
}