这个是自己完成的作业，肯定有很多垃圾代码和垃圾算法。
问题如下：
A:B,C,D,F,E,O
B:A,C,E,K
C:F,A,D,I
D:A,E,F,L
E:B,C,D,M,L
F:A,B,C,D,E,O,M
G:A,C,D,E,F
H:A,C,D,E,O
I:A,O
J:B,O
K:A,C,D
L:D,E,F
M:E,F,G
O:A,H,I,J


求：哪些用户两两之间有共同好友，及共同好友都是哪些人
A-B  C,E
A-C  D,F



## 1. Mapper 的任务
处理需求数据文件中的内容， 把原来 <user,friends> 这样的列表，转成<friend,user>，这里的user是单个字符串
也就是原来是用户对应的好友。现在改成好友对应了几个用户。

```
A:B,C,D,F,E,O
B:A,C,E,K

-------转成--------

B,A
C,A
D,A
F,A
E,A
O,A
A,B
C,B
E,B
K,B
```

## 2. Reducer 的任务
Reducer 分为两部分，第一部分在reduce中，第二部分在cleanup中
### 2.1 reduce
组合Mapper传回来的数据，为 friendMap<friend,users>，这里的users是逗号列表
A:B,C,D,F,G,H,I,K,O
B:A,E,F,J
上面的两行表示，拥有A好友的用户是B,C,D,F,G,H,I,K,O，拥有B好友的用户是A,E,F,J

### 2.2 cleanup
写一个双重for循环，内外循环同时遍历所有user，我是用TreeSet去重的。
内外循环分别有一个用户，这两个用户如果同时出现在 friendMap的value中，
则认为他们拥有共同好友，这个共同好友就是friendMap 的key

## 3. 类文件描述

### JobSubmitter
提交mr作业的类


### TestConbination
测试数据的切分和组合

### TestString
测试字符串如何判空

### UserListMapper
Mapper 子类

### UserListReducer
Reducer 子类



