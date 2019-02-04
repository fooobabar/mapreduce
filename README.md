

MapReduce写完之后，跟单机环境不同，需要在yarn环境下运行。 
yarn是分布式系统，也有很多服务进程。

 * resource manager 有一台，用来接收客户端的请求，做任务调度的
 * node manager 有很多台，能够根据客户端（自己写的客户端）请求，在自己所在节点开辟任务，任务资源包括cpu和内存资源，自己写的客户端jar包会分别分配到不同的任务中。

直接安装到集群所在节点，各个角色的安装位置，**node manager**最好放到datenode 所在节点，因为数据传输要经过网络，而本地请求本地不需要走交换机。**resource manager**可以放到单独的节点中。

# 启动 yarn集群
yarn的所有节点软件，都在hadoop的安装包中。下面是各个节点的角色分配： 
  * hdp-01 安装 Resource Manager / Node Manager
  * hdp-02 Node Manager
  * hdp-03 Node Manager
  * hdp-04 Node Manager


## 1.修改 yarn-site.xml，复制到其他节点
1. yarn.resourcemanager.hostname 指定resource manager所在节点
2. yarn.nodemanager.aux-services 指定shuffle辅助服务

``` xml
<property>
 <name>yarn.resourcemanager.hostname</name>
 <value>hdp-01</value>
</property>
<property>
 <name>yarn.nodemanager.aux-services</name>
 <value>mapreduce_shuffle</value>
</property>
<property>
    <name>yarn.nodemanager.resource.memory-mb</name>
    <value>2048</value>
</property>
<property>
    <name>yarn.nodemanager.resource.cpu-vcores</name>
    <value>2</value>
</property>
```
 3. yarn.nodemanager.resource.memory-mb 指定虚拟内存大小，单位是mb
 4. yarn.nodemanager.resource.cpu-vcores 指定虚拟cpu个数，

## 2. 启动yarn集群

 **分为两种启动方式，一种是使用yarn-daemon指定角色启动，另外一种是自动启动**
 
1. 第一种：每个节点手工启动

 * 在resource manager中执行下面脚本

``` shell
[root@hdp-01 sbin]# cd $HADOOP_HOME/sbin
[root@hdp-01 sbin]# yarn-daemon.sh start resourcemanager
starting resourcemanager, logging to /opt/hadoop-2.8.5/logs/yarn-root-resourcemanager-hdp-01.out
[root@hdp-01 sbin]# jps
4055 Jps
3837 ResourceManager
[root@hdp-01 sbin]# yarn-daemon.sh start nodemanager
starting nodemanager, logging to /opt/hadoop-2.8.5/logs/yarn-root-nodemanager-hdp-01.out
[root@hdp-01 sbin]# jps
4198 Jps
3837 ResourceManager
4093 NodeManager
```

 * 在node manager 中执行下面脚本
``` shell
# yarn-daemon.sh start nodemanager
```

2. 第二种：使用自动脚本启动

在resourcemanager 节点输入下面命令，因为start-yarn.sh 没有配置文件，所以一定要在resourcemanager节点运行，可以启动resource manager和所有的node manager
``` shell
[root@hdp-01 sbin]# start-yarn.sh 
```

使用下面的url访问yarn 集群
http://hdp-01:8088/cluster

# 通过API添加任务到Yarn中

## 1. 导包到工程中
```shell
[root@hdp-01 hadoop]# 
[root@hdp-01 hadoop]# pwd
/opt/hadoop-2.8.5/share/hadoop
[root@hdp-01 hadoop]# ls
common hdfs httpfs kms mapreduce tools yarn
common 目录下的jar包
hdfs 目录下的jar包
mapreduce 目录下的jar包
yarn 目录下的jar包
```

## 3. 设置yarn参数

使用Configuration 设置参数
 1. __fs.defaultFS__ 设置job运行时要访问的默认文件系统，指定为HDFS
 2. __mapreduce.framework.name__ 设置job提交到哪里运行mr任务有两种方式运行，一种是直接在本地，启动模拟器运行，指定为local；另外一种是提交到yarn集群运行，指定为yarn，默认值是local。
 3. __yarn.resourcemanager.hostname__ 设置resourcemanager的地址
 4. __mapreduce.app-submission.cross-platform__ 设置跨平台提交，如果是Windows平台运行main方法，则需要设置这个参数


## 4. 设置job参数

生成job对象
Job job = Job.getInstance(conf);
1. __setJar__ 设置jar包位置
2. __setMapperClass__ 本次job所要调用的Mapper实现类
3. __setReducerClass__ 本次job所要调用的Reduce实现类
4. __setMapOutputKeyClass__ 本次job的Mapper实现类产生结果数据的Key，Value类型
5. __setMapOutputValueClass__ 
6. __setOutputKeyClass__ Reduce 实现类产生的结果数据的Key， VALUE类型
7. __setOutputValueClass__
8. __FileInputFormat.setInputPaths(job, "/wordcount/input");__ 本次job要处理的输入数据集所在路径
9. __FileOutputFormat.setOutputPath(job,output);__ 最终结果的输出路径
10. __setNumReduceTasks__ 想要启动的reduce task的数量，
11. __job.waitForCompletion(true);__ 向yarn 提交本次job

 
 ## 5. 从操作系统找到结果文件
 
 ```Shell
 [root@hdp-01 hadoop]# hadoop fs -ls /wordcount
Found 2 items
drwxr-xr-x - root supergroup 0 2019-01-25 13:40 /wordcount/input
drwxr-xr-x - root supergroup 0 2019-01-25 14:16 /wordcount/output
[root@hdp-01 hadoop]# hadoop fs -ls /wordcount/output
Found 3 items
-rw-r--r-- 3 root supergroup 0 2019-01-25 14:16 /wordcount/output/_SUCCESS
-rw-r--r-- 3 root supergroup 25 2019-01-25 14:16 /wordcount/output/part-r-00000
-rw-r--r-- 3 root supergroup 8 2019-01-25 14:16 /wordcount/output/part-r-00001
[root@hdp-01 hadoop]# hadoop fs -cat /wordcount/output/part-r-00000
hanmeimei 1
lilei 1
sb 1
[root@hdp-01 hadoop]# hadoop fs -cat /wordcount/output/part-r-00001
hello 3
 ```

## hadoop官方文档中对 mapred-default.xml 配置的描述
 * mapreduce.map.memory.mb map运行的最小内存，默认1g，如果设置内存小于这个，就会报错
 * mapreduce.reduce.memory.mb reduce运行的最小内存，默认1g，如果设置内存小于这个，就会报错
 * yarn.app.mapreduce.am.resource.mb  mrappmaster 的最小内存，默认1.5g，如果设置内存小于这个，就会报错


## 上传jar包到hadoop 节点运行
除了在eclipse中运行之外，还可以打包.class 文件到集群节点中运行， 比直接在eclipse中要方便。

1. 修改mapred-site.xml 文件
新增mapreduce.framework.name 参数，这个参数用来指定mr任务的运行位置， 默认是local，要指定成yarn

2. 调用jar 文件

对于Eclipse中的代码，可以直接打包成jar包，上传到hadoop集群所在服务器，服务器中已经包含各种依赖jar包。
如果使用java -cp 的方式运行，则会出现问题，找不到相关的依赖包。
java -cp xxx.jar:xxx.jar  需要把所有的jar包用冒号的方式拼起来，然后运行相应的类。

hadoop提供了一个hadoop 命令，用来执行.jar 文件。 hadoop jar 后面跟上目标jar包的名字，
会把这一台机器上hadoop安装目录里所有的jar包和配置文件加入到本次运行Java类的classpath中。

```shell 
[root@hdp-01 jar]# hadoop jar wc.jar mapreduce1.JobSubmitter2
19/01/25 16:33:22 INFO client.RMProxy: Connecting to ResourceManager at hdp-01/192.168.56.61:8032
19/01/25 16:33:22 WARN mapreduce.JobResourceUploader: Hadoop command-line option parsing not performed. Implement the Tool interface and execute your application with ToolRunner to remedy this.
19/01/25 16:33:22 INFO input.FileInputFormat: Total input files to process : 1
19/01/25 16:33:23 INFO mapreduce.JobSubmitter: number of splits:1
19/01/25 16:33:23 INFO mapreduce.JobSubmitter: Submitting tokens for job: job_1548384811152_0003
19/01/25 16:33:23 INFO impl.YarnClientImpl: Submitted application application_1548384811152_0003
19/01/25 16:33:23 INFO mapreduce.Job: The url to track the job: http://hdp-01:8088/proxy/application_1548384811152_0003/
19/01/25 16:33:23 INFO mapreduce.Job: Running job: job_1548384811152_0003
19/01/25 16:33:32 INFO mapreduce.Job: Job job_1548384811152_0003 running in uber mode : false
19/01/25 16:33:32 INFO mapreduce.Job: map 0% reduce 0%
19/01/25 16:33:38 INFO mapreduce.Job: map 100% reduce 0%
19/01/25 16:33:45 INFO mapreduce.Job: map 100% reduce 100%
19/01/25 16:33:45 INFO mapreduce.Job: Job job_1548384811152_0003 completed successfully
```

# local模式
mr有两种运行环境，一种是yarn一种是local，local不需要连接yarn环境，所以特别快，可以用来做测试使用。
而如果指定了输入路径和文件系统类型之后，甚至都可以不用打开hadoop集群。
  1. 安装windows版的hadoop 
  2. 设置HADOOP_HOME环境变量 
  3. 为HADOOP_HOME 目录下的bin指定到PATH变量中
  4. 重启eclipse

好处，测试，debug

# 报错汇总
```Java
Exception in thread "main" org.apache.hadoop.mapreduce.lib.input.InvalidInputException: Input path does not exist: file:/wordcount/input
```
重点不在路径不存在，而是在 file:/wordcount/input ，这里的file出卖了这句报错，说明我前面参数设置的有问题，
job没有连接到hdfs，而是连接到了本地文件。
解决方法是要检查conf参数。



# 对mapreduce各个包的功能描述

## mapreduce1
  实现简单的wordcount功能
  
## mapreduce2
  实现流量统计功能，统计手机号对应的上行流量和下行流量
  
## mapreduce3
  实现页面访问次数 topn
  
## mapreduce4
  多次mapreduce协作工作
  
## mapreduce5
  实现流量统计自定义结果文件分区规则，比如按照省份分，每个省份一个文件。

## mapreduce6
 倒排索引创建练习，了解如何在map中获取切片文件的信息，比如路径，文件名等等
 
## mapreduce7
 练习自定义对象排序
 
## mapreduce8
练习 Partitioner 中的getPartition() 方法

## mapreduce9
学习sequence文件的使用

# Mapreduce 欧诺个街

## 两个核心点：
### mapreduce编程模型

  把数据运算流程分成2个阶段：
  * 阶段1：读取原始数据，形成Key-Value 数据（map方法）
  * 阶段2：将阶段1的Key-Value数据按照相同Key分组聚合（reduce方法）

### mapreduce编程模型的具体实现
  * hadoop中的mapreduce框架，第一阶段是map task，第二阶段是reduce task

## map task

  ### 读数据
  * InputFormat --> TextInputFormat  读取文本文件
  * InputFormat --> SequenceFileInputFormat 读sequence文件
  * InputFormat --> DBInputFormat 读数据库

  ### 处理数据（自己处理）
 map task通过调用Mapper类的map() 方法，实现对数据的处理，这块代码是我们自己实现的。

  ### 分区
  将map阶段产生的Key-Value数据，分发给若干个reduce task来分担负载。一个reduce可以处理很多组，也可以只处理一组。map task会调用Partitioner类的getPartition() 方法来决定如何划分数据给不同的reduce task。
  
  ### 对Key-Value做排序
  调用key.conpareTo() 方法来实现对Key-Value 数据排序。修改Key的compareTo方法，可以修改排序规则。

## reduce task

  ### 读数据
  通过http方式，从maptask产生的数据文件中，下载属于自己的“区” 的数据。比如，0号reduce下载0号区的数据。
  然后将多个maptask 中获取到相同“区” 的文件内容做合并，合并的同时，对同一个“区”中的内容再进行排序。比如，从3个map task中读取到3组0号区的数据，那么需要对这3组数据进行排序。 
  
  ### 处理数据
  通过调用GroupingComparator的compare()方法，来判断文件中的那些key-value属于同一组。然后将这一组数据传给Reducer类的reduce() 方法聚合一次。
  
  ### 输出结果
  调用OutputFormat组件将结果key-value数据写出去。
  OutputFormat  --> TextOutputFormat 写文本文件（一对Key-Value写一行，分隔符用\t）
  OutputFormat  --> SequenceFileOutputFormat 写Sequence文件（直接将Key-Value对象序列化到这个文件中）
  OutputFormat  --> DBOutputFormat 写数据库，一般不会去往数据库里写，都是先存放到HDFS中，然后通过sqoop搞到数据库中。 
  