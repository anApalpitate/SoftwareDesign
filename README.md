## 项目简介

设计并用Java代码实现一个 为Java代码自动生成plantUML类图的工具

## 项目结构

src目录下：

├─main			项目主文件夹
│  └─java			
│      │  Main.java 	程序入口
│      │
│      ├─diagram		文件读入与类图生成
│      │      ClassDiagram.java
│      │      ClassDiagramGenerator.java
│      │
│      ├─model		数据模型
│      │      BaseModel.java
│      │      ClassModel.java
│      │      FieldModel.java
│      │      MethodModel.java
│      │
│      └─utils		工具函数
│              ModifierUtils.java
│
└─test		项目测试文件夹



