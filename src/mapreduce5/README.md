# 需求

用之前统计用户流量的例子来做修改， 之前统计的内容都是放到一起的，不管用户是哪个省份，都放到一个文件中。
现在想按照省份对统计结果做分区。

应该在mapper 分发的时候做处理。 自己重新定义一个分发机制。

默认使用key的 hashcode % reduce 数量，然后分发给余数的reduce。


 - 自己创建一个类，复写 Partitioner 父类里的getPartition 方法。
 - 修改jobsubmitter ，指定分区的类
 - 修改jobsubmitter，指定reduce数量

## ProvincePartitioner.java 
这个类实现了Partitioner 父类，复写了getPartition 方法， 
类中的使用静态变量是为了一次性从数据库中获取所有归属地数据，每个map task都会调用这个方法。