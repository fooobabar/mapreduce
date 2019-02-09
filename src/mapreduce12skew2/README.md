# 打散Key + 多次mr任务



## WordCount
### setup
用来获取reduce的任务数量，整个map读取文件的过程中，只运行一次setup，而运行多次map

### map
通过拼接随机数的方式，打散各个文件中输入内容。做成不同的分片。

### reduce
打散的数据也很多，reduce任务会先聚合一次。 


## WordCount2
### map
切分reduce中的每行输入，只取期待结果中的key和value，写入context中。

### reduce
跟前面任务相同，只负责聚合。 所以没做改变。

