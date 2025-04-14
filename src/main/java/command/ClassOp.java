package command;

import com.github.javaparser.ast.body.BodyDeclaration;
import graph.Graph;
import model.AbstractClassModel;
import utils.CommonUtil;
import utils.Factory;

import java.util.List;

public class ClassOp {
    public static void addClass(String name, boolean isAbstract, List<AbstractClassModel> class_list){
        BodyDeclaration<?> decl = Factory.createTypeDeclaration(name, "class", isAbstract, null);
        AbstractClassModel classModel = Factory.classFactory(decl);
        class_list.add(classModel);
        CommonUtil.sortClassList(class_list);
    }

    public static void addInterface(String name, List<AbstractClassModel> class_list){
        BodyDeclaration<?> decl = Factory.createTypeDeclaration(name, "interface", false,null);
        AbstractClassModel classModel = Factory.classFactory(decl);
        class_list.add(classModel);
        CommonUtil.sortClassList(class_list);
    }

    public static void addEnum(String name, String ValueList, List<AbstractClassModel> class_list){
        BodyDeclaration<?> decl = Factory.createTypeDeclaration(name, "enum", false, ValueList);
        AbstractClassModel classModel = Factory.classFactory(decl);
        class_list.add(classModel);
        CommonUtil.sortClassList(class_list);
    }

    public static void deleteClassInterfaceEnum(String name, List<AbstractClassModel> class_list, Graph graph){
        AbstractClassModel target = null;
        for (AbstractClassModel Class : class_list){
            if(Class.getName().equals(name)){
                target = Class;
                break;
            }
        }
        if(target == null){
            return;
        }
        // 级联删除与该类相关的关系
        graph.deleteAll(target.getName());
        class_list.remove(target);
        CommonUtil.sortClassList(class_list);
    }

    public static void deleteInterface(String name, List<AbstractClassModel> class_list, Graph graph){
        AbstractClassModel target = null;
        for (AbstractClassModel Class : class_list){
            if(Class.getName().equals(name)){
                target = Class;
                break;
            }
        }
        if(target == null){
            return;
        }
        // 级联删除与该类相关的关系
        graph.deleteAll(target.getName());
        class_list.remove(target);
        CommonUtil.sortClassList(class_list);
    }

    public static void deleteEnum(String name, List<AbstractClassModel> class_list, Graph graph){
        AbstractClassModel target = null;
        for (AbstractClassModel Class : class_list){
            if(Class.getName().equals(name)){
                target = Class;
                break;
            }
        }
        if(target == null){
            return;
        }
        // 级联删除与该类相关的关系
        graph.deleteAll(target.getName());
        class_list.remove(target);
        CommonUtil.sortClassList(class_list);
    }
}
