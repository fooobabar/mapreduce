
# 需求
有一组订单数据，现在要求按照订单分组，取订单中商品金额最大的3个商品，
比如 order001 中商品金额最大的是小米6，防水电脑包，经典红双喜
显示格式不限

![enter description here](https://giec5j.nos-eastchina1.126.net/githubimage_mapreduce/mapreduce%E8%AE%A1%E7%AE%97%E8%AE%A2%E5%8D%95topn-%E8%80%81%E6%96%B9%E6%B3%95.png)
这个需求跟前面mapreduce7的需求相同，但是实现的方式不同。
前面mapreduce7在reduce方法中使用了ArrayList缓存，
如上图所示，Map任务输出的格式是 text和OrderBean两个，经过shuffle之后，相同订单的数据会分发给同一个reduce任务。
然后reduce创建arrayList 存储OrderBean对象。 再通过sort方法排序。输出金额最大的数据。
这种思路做，reduce除了序列化数据到文件还要存储对象，排序对象。而reduce本来就要做分组排序，能不能把额外的工作交给自己本身的排序做呢？


## 面临的问题

 *. reduce 阶段用到了ArrayList缓存，以及sort方法，如何通过reduce自有的排序就能完成需求？
![enter description here](https://giec5j.nos-eastchina1.126.net/githubimage_mapreduce/mapreduce%E8%AE%A1%E7%AE%97%E8%AE%A2%E5%8D%95topn-%E6%96%B0%E6%96%B9%E6%B3%95.png)

关于这个问题可以考虑把OrderBean当做Key，value不用给， reduce task在工作之前，会对自己手里每组数据先进行排序。从小到大处理，这个我们可以实现GroupingComparator 中的compare方法，让大的在前面。 
但是又面临一个问题，就是现在Key是一个对象， 对象的数据不一样，怎么把他们分到相同的reduce中。 
解决这个问题可以去搞Partitioner 类。

## OrderIdGroupingComparator.java

继承 WritableComparator 父类，实现compare 方法。比较两个WritableComparable 对象。 
使用OrderBean 强转，获取到 GroupId 。使用compare方法比较。


## OrderIdPartitioner.java 

用来控制分组，那组数据用来分给哪个reduce

