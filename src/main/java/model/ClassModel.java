package model;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import graph.Graph;
import utils.CommonUtil;

import java.util.HashSet;
import java.util.Set;

public class ClassModel extends AbstractClassModel {
    private final boolean isAbstract;
    String GenericType;
    Set<String> AssociatedClasses;
    Set<String> DependedClasses;

    private int fieldCnt = 0; /*注意:field在处理int a,b,c的情况时应该是加3*/
    private int methodCnt = 0;/*注意区分构造方法*/

    public ClassModel(ClassOrInterfaceDeclaration decl) {
        super(decl);
        this.isAbstract = decl.isAbstract();
        this.GenericType = extractGenericTypes(decl);

        parseFields(decl);
        parseMethods(decl);
        SortFieldsAndMethods();
        addInheritancesAndImplementations(decl);
        addAssociations();
        addDependencies();

    }

    void parseFields(BodyDeclaration declaration) {
        AssociatedClasses = new HashSet<>();
        if (declaration instanceof ClassOrInterfaceDeclaration classDecl) {
            for (FieldDeclaration field : classDecl.getFields()) {
                FieldModel fieldModel = new FieldModel(field, "");
                fields.add(fieldModel);
                AssociatedClasses.addAll(fieldModel.getAssociations());
                fieldCnt += fieldModel.getCnt();
            }
        }
    }

    void parseMethods(BodyDeclaration declaration) {
        DependedClasses = new HashSet<>();
        if (declaration instanceof ClassOrInterfaceDeclaration classDecl) {
            for (MethodDeclaration method : classDecl.getMethods()) {
                MethodModel methodModel = new MethodModel(method, "");
                methods.add(methodModel);
                DependedClasses.addAll(methodModel.getDependencies());
                methodCnt += (methodModel.isConstructor() ? 0 : 1);
            }
        }
    }

    private void addInheritancesAndImplementations(ClassOrInterfaceDeclaration decl) {
        String srcName = getName();
        for (ClassOrInterfaceType relatedClass : decl.getExtendedTypes()) {
            String dstName = relatedClass.getNameAsString();
            Graph.addInheritance(srcName, dstName);
        }
        for (ClassOrInterfaceType relatedClass : decl.getImplementedTypes()) {
            String dstName = relatedClass.getNameAsString();
            Graph.addImplementation(srcName, dstName);
        }
    }

    private void addAssociations() {
        for (String associatedClass : AssociatedClasses) {
            if (CommonUtil.isBasicType(associatedClass))
                continue;
            Graph.addAssociation(getName(), associatedClass);
        }
    }

    private void addDependencies() {
        for (String dependedClass : DependedClasses) {
            if (CommonUtil.isBasicType(dependedClass))
                continue;
            Graph.addDependency(getName(), dependedClass);
        }
    }

    public String generateString() {
        String blank = "    ";
        StringBuilder sb = new StringBuilder();
        String AbstractStr = isAbstract ? "abstract " : "";
        sb.append(AbstractStr);
        sb.append("class ").append(getName()).append(GenericType).append(" {\n");

        for (FieldModel field : fields)
            sb.append(blank).append(field.generateString()).append("\n");
        for (MethodModel method : methods)
            sb.append(blank).append(method.generateString()).append("\n");

        sb.append("}\n");
        return sb.toString();
    }

    public boolean isGodClass() {
        return fieldCnt >= 20 || methodCnt >= 20;
    }

    public boolean isLazyClass() {
        return fieldCnt == 0 || methodCnt <= 1;
    }

    public boolean isDataClass() {
        if (isGodClass() || isLazyClass())
            return false;
        for (MethodModel method : methods) {
            if (!method.getName().startsWith("get") && !method.getName().startsWith("set"))
                return false;
        }
        return true;
    }
}
