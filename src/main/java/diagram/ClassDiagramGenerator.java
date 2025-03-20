package diagram;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 解析 Java 源文件并提取类、接口和枚举信息。
 */
public class ClassDiagramGenerator {

    /**
     * 解析指定 Java 文件，提取类信息。
     * @param javaFilePath Java 源文件路径
     * @return 解析后的 ClassDiagram 对象
     * @throws IOException 读取文件失败时抛出
     */
    public ClassDiagram parse(Path javaFilePath) throws IOException {
        CompilationUnit cu = new JavaParser().parse(javaFilePath).getResult().orElse(null);
        List<ClassInfo> classInfos = new ArrayList<>();
        if (cu != null) {
            extractClassInfo(cu, classInfos);
        }
        return new ClassDiagram(classInfos);
    }

    /**
     * 提取类、接口和枚举的信息，包括字段和方法。
     */
    private void extractClassInfo(CompilationUnit cu, List<ClassInfo> classInfos) {
        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(decl -> {
            List<FieldInfo> fields = decl.findAll(FieldDeclaration.class).stream()
                    .flatMap(field -> field.getVariables().stream()
                            .map(var -> new FieldInfo(
                                    var.getNameAsString(),
                                    field.getElementType().asString(),
                                    field.getModifiers().isEmpty() ? null : field.getModifiers().iterator().next(),
                                    field.getModifiers().stream().anyMatch(mod -> mod.toString().replaceAll("\\s+", "").equals("static")) // 通过名称判断是否为静态字段
                            )))
                    .collect(Collectors.toList());

            List<MethodInfo> methods = decl.findAll(MethodDeclaration.class).stream()
                    .map(method -> new MethodInfo(
                            method.getNameAsString(),
                            method.getType().asString(),
                            method.getParameters().stream()
                                    .map(param -> param.getNameAsString() + ": " + param.getType().asString()) // name: type 格式
                                    .collect(Collectors.toList()),
                            method.getModifiers().isEmpty() ? (decl.isInterface() ? Modifier.publicModifier() : null) : method.getModifiers().iterator().next(),
                            method.getModifiers().stream().anyMatch(mod -> mod.toString().replaceAll("\\s+", "").equals("static")) // 通过名称判断是否为静态方法
                    ))
                    .collect(Collectors.toList());

            classInfos.add(new ClassInfo(decl.getNameAsString(), decl.isInterface(),
                    decl.getExtendedTypes().stream().map(t -> t.getNameAsString()).collect(Collectors.toList()),
                    decl.getImplementedTypes().stream().map(t -> t.getNameAsString()).collect(Collectors.toList()),
                    fields, methods));
        });
    }
}
