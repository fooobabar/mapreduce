
# 多MapReduce任务协同

> 可以发现前面的练习中reducer 的实现类是单个进程，如果数据量较大，任务都堆积到一个reduce任务中，那么会面临两个问题，
> * 内存溢出
> * 任务完成，但是运行较慢

为了避免这个问题，可以使用两个或多个 mapreduce 任务做计算，
也就是说第一个mapreduce 计算结果作为第二个MapReduce的输入。

## 1. PageCount
这是一个只有两个属性的类，其中一个属性是网址，另外一个属性就是网址访问次数。
这个类实现了WritableComparable 接口，拥有compareTo、write 和read 方法

## 2. PageCountStep1

这是第一个MapReduce任务的类，这个类里包含了两个内部类，分别是Mapper的实现类和Reducer的实现类。而且是内部类。
这个类的两个内部类，其实就是之前做过的wordcount，这个类可以创建多个reduce任务，不管生成几个part文件，最终都会汇聚出非常少的数据。如果key的选择性很低，比如男女，年龄等等，那么可能由几百万的数据汇总成只有几十条。


### 2.1 PageCountStep1Mapper
类泛型参数 LongWritable,Text,Text,IntWritable

### 2.2 PageCountStep1Reducer 
类泛型参数 Text,IntWritable,Text,IntWritable


### 2.3 main
调用任务 

## 3. PageCountStep2
这个类同样包含了两个内部类，目的是对前面汇总过的数据进行排序， 首先需要用map任务把前面汇总过的数据都存放到实现了WritableComparable接口的类中。 
这个类包含了两个属性，其中一个属性是网站名称，第二个属性是网站的访问次数。
这个类作为context的key，以null为value。
reduce task会根据context排序。所以如果有排序需要的话，可以根据需求自己实现compareTo。
最后reduce的输出是Text,Intwritable

### 3.1 PageCountStep2Mapper
类泛型参数 LongWritable,Text,PageCount,NullWritable
PageCount 是只有网页名称和访问次数的类，这个类实现了**WritableComparable**接口。能排序和序列化反序列化。 把PageCount 对象写入到context中，reduce会针对这个集合进行排序。 

### 3.2 PageCountStep2Reducer
类泛型参数 PageCount,NullWritable,Text,IntWritable
直接把PageCount 对象中的属性写入到Context 中就行。
最终会按照顺序对文本输出到文件中。

### 3.3 main
调用任务 