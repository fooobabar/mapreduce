## 需求：有大量的文本文档，如下所示：


a.txt
```
hello tom
hello jim
hello kitty
hello rose
```

b.txt
```
hello jerry
hello jim
hello kitty
hello jack
```

c.txt
```
hello jerry
hello java
hello c++
hello c++
```

## 需要得到以下结果
```
hello  a.txt-->4  b.txt-->4  c.txt-->4
java  c.txt-->1
jerry  b.txt-->1  c.txt-->1
....
```

## 解决思路
解决思路分为两部分，
第一部分是step1 计算出单词-文件名 出现了多少次，例如 <hello-a.txt,4>
第二部分是step2 以-切分字符串，然后拼接后面的部分，例如 <hello,a.txt-->4  b.txt-->4>

### step1
Mapper 用空格切分每一个输入的行，切分成单词， 并且用“单词-文件名” 的形式做为Key，用1 作为value。
Reducer 聚合Mapper 传入的结果。

### step2
Mapper 以 “-” 作为分隔符切分字符串，前面的单词为Key ，后面的字符串作为value，value中的替换制表符为“-->”
Reducer 利用StringBuilder对输入的字符串做追加，拼成制表符作为分隔符的字符串。

## 需要解决的问题
 1. 怎么知道Mapper的每行是来自什么文件呢？
context.inputsplit 方法，可以获取到处理文件信息。
inputsplit 用于描述每个maptask所处理的数据任务范围

如果maptask读的是文件：
  划分范围应该用如下信息描述：
    文件路径，偏移量范围
	
如果maptask读的是数据库表信息：
  划分范围应该用如下信息描述：
    库名，表名，行范围
所以inputsplit 返回的是抽象类。使用时，需要强转类型为Filesplit


