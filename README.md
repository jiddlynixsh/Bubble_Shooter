# Bubble_Shooter_JavaFX
A classic Bubble Shooter game built with Java and the JavaFX framework, featuring a customized My Melody aesthetic.

## 🎀 游戏简介 & 主题 Overview & Theme
本项目是一个基于 Java 和 JavaFX 开发的经典**泡泡龙（Bubble Shooter）**消除小游戏。
游戏整体采用了**美乐蒂粉色梦幻风格**进行视觉定制，包含粉色系的 UI 界面、萌系泡泡皮肤以及精心搭配的背景资源，融趣味性与视觉美感于一体。

## 🎬 演示与截图 Demo & Showcase
### 游戏运行演示 Demo Video
我们录制了完整的游戏操作演示视频，展示了泡泡发射、碰撞及消除的丝滑特效：
- **[👉 点击查看泡泡龙运行演示视频 (泡泡龙.mov)](泡泡龙.mov)**

## 🌟 游戏核心技术特点 Technical Features
- **碰撞检测 Collision Detection**：实现了发射泡泡与上方动态泡泡矩阵的精准几何碰撞、边界反弹及网格停靠吸附逻辑。
- **Match-3 消除算法 Match-3 & Drop Algorithm**：
  - 基于**广度优先搜索 BFS** 或**深度优先搜索 DFS** 算法，精准检索并消除相同颜色的相连泡泡组合（数量 $\ge 3$）。
  - 实现了“孤立悬空泡泡”的自动判定与下坠物理效果，完美还原经典泡泡龙机制。
- **动态关卡设计 Level Design**：支持多关卡数据加载（如以 `Level4Main.java` 作为核心关卡入口），具备良好的可扩展性。

## 🛠️ 技术栈 (Tech Stack)
- **语言**：Java (JDK 8 或更高)
- **图形界面框架**：JavaFX
- **视觉主题**：美乐蒂定制皮肤与 UI
- **开发工具**：Eclipse 

## 🚀 如何在本地运行 (How to Run)
1. 克隆本项目到本地：
   ```bash
   git clone [https://github.com/你的用户名/Bubble_Shooter_JavaFX.git](https://github.com/你的用户名/Bubble_Shooter_JavaFX.git)# Bubble_Shooter
A classic Bubble Shooter game built with Java and JavaFX framework
