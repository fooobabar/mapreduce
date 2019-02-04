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

## sequence文件解决问题
不采用之前纯文本的方式解决，使用sequence 文件读写
从之前的例子中了解到，读取mapreduce文件都是使用textinputformat 这个类。那是因为源文件都使用text文本文件。 

有一种文件跟text文件不同，是sequence文件，是序列化文件，跟普通文件不同之处，**读取文本文件的时候需要用特殊分隔符切分，而读取sequence对象的时候，直接读取就行了**。 不需要关注分隔符是什么。
如果处理链条很长，有多个mr任务时，各个处理中间结果都可以使用这种文件。好处是中间mr任务不需要切分。

文件里存放序列化后的对象。 格式大体如下：
org.apache.io.Text cn.edu360.mr.flow.FlowBean
key:value key:value key:value key:value ...

## 修改第一个步骤中的输出
可以使用倒排索引的例子做修改，需要修改job中的setOutputFormatClass 属性， 
默认值 TextOutputFormat.class
修改后 SequenceFileOutputFormat.class
导包的时候会有两个选择， 要选择包名长的。
org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;


## 修改第二个步骤的输入
如果第一个步骤的输出是sequence文件，则第二个文件就不能使用text的方式进行切分了。 

* 修改main方法中job的setInputFormatClass 属性。
	* 默认值：TextInputFormat.class
	* 修改后：SequenceFileInputFormat.class

* 修改Mapper实现类的泛型参数，由于读取的文件是sequence文件，所以第一个泛型参数就不是LongWritable了。应该是Key的类型。 之前步骤的输出格式。 这里是text，如果是自定义对象，则可以使用自定义对象的类型。 
	* 修改前：LongWritable
	* 修改后：Text

* 修改map方法的参数类型，由于泛型参数已经修改了， 这里要求对map方法的参数类型也做修改。
