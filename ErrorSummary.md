# 报错汇总
```Java
Exception in thread "main" org.apache.hadoop.mapreduce.lib.input.InvalidInputException: Input path does not exist: file:/wordcount/input
```
重点不在路径不存在，而是在 file:/wordcount/input ，这里的file出卖了这句报错，说明我前面参数设置的有问题，
job没有连接到hdfs，而是连接到了本地文件。
解决方法是要检查conf参数。