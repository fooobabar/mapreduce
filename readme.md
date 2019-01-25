* [启动 yarn集群](#启动-yarn集群)
	* [1.修改 yarn-site.xml，复制到其他节点](#1修改-yarn-sitexml复制到其他节点)
	* [2. 启动yarn集群](#2-启动yarn集群)
* [通过API添加任务到Yarn中](#通过api添加任务到yarn中)
	* [1. 导包到工程中](#1-导包到工程中)
	* [3. 设置yarn参数](#3-设置yarn参数)
	* [4. 设置job参数](#4-设置job参数)
	* [5. 从操作系统找到结果文件](#5-从操作系统找到结果文件)


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
1. yarn.resourcemanager.hostname  指定resource manager所在节点
2. yarn.nodemanager.aux-services  指定shuffle辅助服务

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
 3. yarn.nodemanager.resource.memory-mb  指定虚拟内存大小，单位是mb
 4. yarn.nodemanager.resource.cpu-vcores  指定虚拟cpu个数，

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
common  hdfs  httpfs  kms  mapreduce  tools  yarn
common 目录下的jar包
hdfs 目录下的jar包
mapreduce 目录下的jar包
yarn 目录下的jar包
```

## 3. 设置yarn参数

使用Configuration 设置参数
 1. __fs.defaultFS__ 设置job运行时要访问的默认文件系统，指定为HDFS
 2. __mapreduce.framework.name__ 设置job提交到哪里运行mr任务有两种方式运行，一种是直接在本地，启动模拟器运行，指定为local；另外一种是提交到yarn集群运行，指定为yarn
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
10. __setNumReduceTasks__  想要启动的reduce task的数量，
11. __job.waitForCompletion(true);__  向yarn 提交本次job

 
 ## 5. 从操作系统找到结果文件
 
 ```Shell
 [root@hdp-01 hadoop]# hadoop fs -ls /wordcount
Found 2 items
drwxr-xr-x   - root supergroup          0 2019-01-25 13:40 /wordcount/input
drwxr-xr-x   - root supergroup          0 2019-01-25 14:16 /wordcount/output
[root@hdp-01 hadoop]# hadoop fs -ls /wordcount/output
Found 3 items
-rw-r--r--   3 root supergroup          0 2019-01-25 14:16 /wordcount/output/_SUCCESS
-rw-r--r--   3 root supergroup         25 2019-01-25 14:16 /wordcount/output/part-r-00000
-rw-r--r--   3 root supergroup          8 2019-01-25 14:16 /wordcount/output/part-r-00001
[root@hdp-01 hadoop]# hadoop fs -cat /wordcount/output/part-r-00000
hanmeimei       1
lilei   1
sb      1
[root@hdp-01 hadoop]# hadoop fs -cat /wordcount/output/part-r-00001
hello   3
 ```







