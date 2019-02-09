
## 数据倾斜

**map计算完的数据是使用hashcode算法分组的，如果数据倾斜，则肯定会有一部分reduce任务分到的数据特别多，有一部分reduce分到的数据特别少。**

![数据倾斜](https://giec5j.nos-eastchina1.126.net/mapreduce/mapreduce%E6%95%B0%E6%8D%AE%E5%80%BE%E6%96%9C.png)

处理数据倾斜有两个思路
  * 使用Combiner
![数据倾斜处理思路](https://giec5j.nos-eastchina1.126.net/mapreduce/mapreduce%E6%95%B0%E6%8D%AE%E5%80%BE%E6%96%9C%E5%A4%84%E7%90%86%E6%80%9D%E8%B7%AF.png)
  * 使用多次mr任务


## Combiner处理思路
### WordcountCombiner
继承Reducer，实现reduce方法，让maptask调用，则聚合的内容是map内局部聚合。

* 进来的数据类型是map的输出数据类型
* 输出的数据类型是reduce的输入数据类型

 **WordcountCombiner.reduce**方法只是一个逻辑片段，写好之后，需要在job中设置参数，让maptask调用这个方法

```java
job.setCombinerClass(WordcountCombiner.class); //设置maptask端的局部聚合逻辑类
```


## 多次mr任务处理思路

对于多次mr任务，考虑的第一点就是打散输入数据的key，比如前面的练习中，如果a单词出现次数太多， 可以考虑使用随机数，拼上单词。形成 a-0 , a-1 , a-2 这种key。

这样就可以分散到多个reduce任务中。 然后第二次再做map，切分聚合后的数据。 形成目标数据。



