## 项目简介

设计并用Java代码实现一个 为Java代码自动生成plantUML类图的工具

## 项目结构

```shell
src
├── main                  # 项目主文件夹
│   ├── java              # Java 源代码
│   │   ├── Main.java     # 程序入口
│   │   │
│   │   ├── JClassDiagram.java
│   │   │
│   │   ├── command       # 命令行处理模块
│   │   │   ├── ClassOp.java
│   │   │   ├── CommandLineTool.java
│   │   │   ├── FieldOp.java
│   │   │   ├── FunctionOp.java
│   │   │   └── Query.java
│   │   │
│   │   ├── diagram       # 文件读入与类图生成
│   │   │   ├── ClassDiagram.java
│   │   │   ├── ClassDiagramGenerator.java
│   │   │   ├── ClassParser.java
│   │   │   └── SmellAnalyzer.java
│   │   │
│   │   ├── graph         # 图结构与类之间的关系
│   │   │   ├── ClassMap.java
│   │   │   └── Graph.java
│   │   │
│   │   ├── model         # 数据模型
│   │   │   ├── AbstractClassModel.java
│   │   │   ├── BaseModel.java
│   │   │   ├── ClassModel.java
│   │   │   ├── EnumModel.java
│   │   │   ├── FieldModel.java
│   │   │   ├── InterfaceModel.java
│   │   │   └── MethodModel.java
│   │   │
│   │   └── utils         # 工具函数
│   │       ├── AnalyzerUtil.java
│   │       ├── CommonUtil.java
│   │       ├── Factory.java
│   │       └── GlobalVar.java
│   │
└── test                  # 项目测试文件夹

```

