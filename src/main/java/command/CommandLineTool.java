package command;

import diagram.ClassDiagram;

public class CommandLineTool {
    private ClassDiagram diagram;

    public CommandLineTool(ClassDiagram diagram) {
        this.diagram = diagram;
    }

    /**
     * @param command 输入的命令
     * @return 如果是查询性质语句，将查询的结果保存在返回值中。Undo语句可能返回的信息也保存在返回值中。
     */
    public String execute(String command) {
        String[] parts = command.trim().split("\\s+");
        if (parts.length == 0) return "";

        switch (parts[0]) {
            case "add":
                switch (parts[1]) {
                    case "-c": {
                        //add -c <name> [--abstract]
                        String name = parts[2];
                        boolean isAbstract = command.contains("--abstract");
                        diagram.addClass(name, isAbstract);
                        break;
                    }
                    case "-i": {
                        //add -i <name>
                        String name = parts[2];
                        diagram.addInterface(name);
                        break;
                    }
                    case "-e": {
                        //add -e <name> [--values=<constant list>]
                        String name = parts[2];
                        String valueList = null;
                        for (String part : parts) {
                            if (part.startsWith("--values=")) {
                                valueList = part.substring("--values=".length());
                                break;
                            }
                        }
                        diagram.addEnum(name, valueList);
                        break;
                    }
                    case "field": {
                        //add field <target-name> -n <field-name> -t <type> [--access=<modifier>] [--static]
                        String target = parts[2];
                        String fieldName = getArg(parts, "-n");
                        String type = getArg(parts, "-t");
                        String access = getOptionalArg(parts, "--access", "-");
                        boolean isStatic = command.contains("--static");
                        diagram.addField(target, fieldName, type, access, isStatic);
                        break;
                    }
                    case "function": {
                        //add function <target-name> -n <function-name> -t <ret-type>
                        // [--params=<params>] [--access=<modifier>] [--static] [--abstract]
                        String target = parts[2];
                        String functionName = getArg(parts, "-n");
                        String retType = getArg(parts, "-t");
                        String params = getOptionalArg(parts, "--params", "");
                        String access = getOptionalArg(parts, "--access", "-");
                        boolean isStatic = command.contains("--static");
                        boolean isAbstract = command.contains("--abstract");
                        diagram.addFunction(target, functionName, retType, params, access, isStatic, isAbstract);
                        break;
                    }
                }
                break;

            case "delete":
                switch (parts[1]) {
                    case "-c":
                    case "-i":
                    case "-e":
                        //delete -e <name>
                        //delete -i <name>
                        //delete -c <name>
                        String ClassName = parts[2];
                        diagram.deleteClassInterfaceEnum(ClassName);
                        break;
                    case "field": {
                        //delete field <target-name> -n <field-name>
                        String target = parts[2];
                        String fieldName = getArg(parts, "-n");
                        diagram.deleteField(target, fieldName);
                        break;
                    }
                    case "function": {
                        //delete function <target-name> -n <function-name>
                        String target = parts[2];
                        String functionName = getArg(parts, "-n");
                        diagram.deleteFunction(target, functionName);
                        break;
                    }
                }
                break;

            case "modify":
                switch (parts[1]) {
                    case "field": {
                        //modify field <target-name> -n <field-name> [--new-name=<name>]
                        // [--new-type=<type>] [--new-access=<modifer>] [--static]
                        String target = parts[2];
                        String fieldName = getArg(parts, "-n");
                        String newName = getOptionalArg(parts, "--new-name", null);
                        String newType = getOptionalArg(parts, "--new-type", null);
                        String newAccess = getOptionalArg(parts, "--new-access", null);
                        boolean isStatic = command.contains("--static");
                        diagram.modifyField(target, fieldName, newName, newType, newAccess, isStatic);
                        break;
                    }
                    case "function": {
                        //modify function <target-name> -n <function-name> [--new-name=<name>]
                        // [--new-params=<params>] [--new-return=<ret-type>] [--new-access=<modifier>] [--static] [--abstract]
                        String target = parts[2];
                        String functionName = getArg(parts, "-n");
                        String newName = getOptionalArg(parts, "--new-name", null);
                        String newParams = getOptionalArg(parts, "--new-params", null);
                        String newReturn = getOptionalArg(parts, "--new-return", null);
                        String newAccess = getOptionalArg(parts, "--new-access", null);
                        boolean isStatic = command.contains("--static");
                        boolean isAbstract = command.contains("--abstract");
                        diagram.modifyFunction(target, functionName, newName, newParams, newReturn, newAccess, isStatic, isAbstract);
                        break;
                    }
                }
                break;

            case "undo":
                //撤回
                return diagram.undo();

            case "query":
                switch (parts[1]) {
                    case "-c": {
                        //query -c <name> [--hide=<field|method>]
                        String name = parts[2];
                        String hide = getOptionalArg(parts, "--hide", "");
                        return diagram.queryClass(name, hide);
                    }
                    case "-i": {
                        //query -i <name> [--hide=<method>]
                        String name = parts[2];
                        String hide = getOptionalArg(parts, "--hide", "");
                        return diagram.queryInterface(name, hide);
                    }
                    case "-e": {
                        //query -e <name> [--hide=<constant|field|method>]
                        String name = parts[2];
                        String hide = getOptionalArg(parts, "--hide", "");
                        return diagram.queryEnum(name, hide);
                    }
                }
                break;

            case "relate": {
                //relate <elementA> <elementB>
                String elementA = parts[1];
                String elementB = parts[2];
                return diagram.relate(elementA, elementB);
            }

            case "smell":
                //smell detail <elementName>
                if ("detail".equals(parts[1])) {
                    String elementName = parts[2];
                    return diagram.smellDetail(elementName);
                }
                break;
        }

        return "";
    }

    private String getArg(String[] parts, String key) {
        return getArg(parts, key, 1);
    }

    private String getArg(String[] parts, String key, int skip) {
        for (int i = skip; i < parts.length - 1; i++) {
            if (parts[i].equals(key)) {
                return parts[i + 1];
            }
        }
        return null;
    }

    private String getOptionalArg(String[] parts, String key, String defaultValue) {
        for (String part : parts) {
            if (part.startsWith(key + "=")) {
                return part.substring((key + "=").length());
            }
        }
        return defaultValue;
    }
}