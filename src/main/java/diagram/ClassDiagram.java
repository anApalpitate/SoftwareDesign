package diagram;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import command.FieldOp;
import command.FunctionOp;
import command.ClassOp;
import command.Query;

public class ClassDiagram {
    private ClassParser classParser;

    ClassDiagram(Path path) {
        File file = path.toFile();
        try {
            this.classParser = new ClassParser(file);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
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
        return classParser.getCodeSmells();
    }

}