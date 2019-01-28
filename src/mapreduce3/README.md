## 需求

1.  __统计每个页面访问的次数__

	输出格式：
	163.com/ac 次数
	163.com/sport 次数
	
	分析：跟原来的实现方法类似
		
2.  __被访问最多的页面top n __

	用到了一个cleanup方法：
	map 循环读取目标文件
	reduce 循环处理目标文件
	cleanup reduce把文件循环好之后，最终要调用cleanup方法
	
	分析：使用一个reduce task 进程，把数据存入treemap，然后用cleanup方法做排序展示

## Mapper 实现类
__PageTopnMapper.java__

实现map方法

## Reducer 实现类
__PageTopnReducer.java__

1. 实现reduce方法，跟以往程序不同，不需要写入context中，而是写入一个类变量中。由于需要排序，那么将会用到具有排序功能的集合，这个集合可以是TreeMap或者ArrayList。所以存入集合之前先定义一个PageCount类，把reduce算好的数据存放到PageCount对象中。要求PageCount对象实现了Comparable 接口。

2. 实现cleanup方法，在类变量中进行排序，取topn，最后把需要的数据写入到Context中


其他注意
* PageCount不需要实现序列化接口，因为不是从Mapper到Reducer
* context可以通过getConfiguration() 方法拿到Configuration 对象。而Configuration 可以从配置文件中读取到配置信息，从而实现灵活的top n；通过TestConfiguration 测试类测试如何读取配置文件。




## TestConfiguration.java

这是一个测试类，用于测试hadoop 中的Configuration是否可以读取非core-site.xml / core-default.xml / hdfs-site.xml / hdfs-default.xml ....
 1. 在 src 目录中新创建一个xx-oo.xml 文件，添加name属性
 2. Configuration对象有 addResource 方法，可以添加xml格式的配置文件
 2. 运行TestConfiguration测试类看看是否可以打印配置文件中的属性。
 

