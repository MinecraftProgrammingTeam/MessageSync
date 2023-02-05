# ~~MessageSync~~ 多功能插件
不仅仅是消息同步插件

# 概述
- 起源：[帖子](https://www.minept.top/p.php?id=30)
  这个插件其实是我学习Spigot插件写的，所有很多地方写的不好  
  **由于功能越写越多，所以该插件目前的功能已经不仅仅是消息同步了**
- 版本：我也不知道兼容哪个版本，测试兼容spigot-1.19.3

# 使用
相关视频教程：[【MC服务器消息同步 - 小猪比机器人】 ](https://www.bilibili.com/video/BV1JR4y1m7Ez/?buvid=XYF1C76FD8262BB081EC618D2B1C25CC1BDBE&is_story_h5=false&mid=HbSPY7IrFHktyJUZi%2BLvQQ%3D%3D&p=1&plat_id=116&share_from=ugc&share_medium=android&share_plat=android&share_session_id=059bb687-d121-4905-b8b2-7e3c6844e1bf&share_source=COPY&share_tag=s_i&timestamp=1675558331&unique_k=fv3rtH6&up_id=227711953)  
填写配置，位于`./plugins/MessageSync/config.yml`  
注释都很详细，全中文，看着填就行

# 已知问题
可能会与以下插件发生冲突：
- 其他的登录插件例如`AuthMe`
- `SkinsRestorer`
- `TitleManager-2.3.1` 预计加入TitleManager以实现并替代tm的功能

# 开发
- 日志  
  引入主类`import center.xzy.qb.messagesync.Main;`，然后使用`Main.instance.getLogger()`就可以获取到`logger`
- `event`事件监听  
  位于`center.xzy.qb.messagesync.event`包，`customEventHandler`是消息上报的监听器类，所有`event`操作都应该在`event`包中，在主类中定义监听器
- `commands`指令  
  位于`center.xzy.qb.messagesync.commands.impl`包中。
  - 创建指令：每一个指令都是一个类，继承自`commands`包中的`ICommand`，需要调用`super(插件名, 参数, 描述)`方法初始化插件
  - 注册指令：在`executor.CommandHandler`中的`initHandler`方法中添加`registerCommand(new 指令类名());`即可
  - 指令使用：在指令类的`onCommand(CommandSender sender, String[] args)`方法中写具体逻辑，在`permission`方法返回使用指令的权限组
  - 好处：使用`/ms 指令名[ 参数]`即可使用指令，有TAB自动补全
- `scheduler`定时任务
  位于`center.xzy.qb.messagesync.scheduler`
- `sqlite`数据库
  直接调用`Main.dbConn`就能获得数据库连接，使用`Main.dbConn.createStatement()`创建`Statement`对象。  
  您无需`dbConn.close()`，因为插件`onDisable`时会自动关闭数据库连接
