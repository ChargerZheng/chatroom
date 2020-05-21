# chatroom
chatroom demo

一个聊天工具demo，Spring Boot项目，提供多人聊天功能。
用户可以创建聊天室，设置名字和密码。 
也可以加入聊天室。 

参考了一下网上的资料，根据自己的需求做的一个demo。
有许多问题还没有好的解决方案，比如广播消息，调用session时出现的并发问题，目前加了ReentrantLock锁。
