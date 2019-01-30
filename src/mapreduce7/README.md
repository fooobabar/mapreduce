
# 需求
有一组订单数据，现在要求按照订单分组，取订单中商品金额最大的3个商品，
比如 order001 中商品金额最大的是小米6，防水电脑包，经典红双喜
显示格式不限

## 数据如下

```Text
orderId,userId,pdtName,price,number    ------  第一行是表头，使用时可以删掉
order001,u001,小米6,1999.9,2
order001,u001,雀巢咖啡,99.0,2
order001,u001,安慕希,250.0,2
order001,u001,经典红双喜,200.0,4
order001,u001,防水电脑包,400.0,2
order002,u002,小米手环,199.0,3
order002,u002,榴莲,15.0,10
order002,u002,苹果,4.5,20
order002,u002,肥皂,10.0,40
order003,u001,小米6,1999.9,2
order003,u001,雀巢咖啡,99.0,2
order003,u001,安慕希,250.0,2
order003,u001,经典红双喜,200.0,4
order003,u001,防水电脑包,400.0,2
```

## OrderBean.java

订单类，实现了WritableComparable 接口，可以在Map 和 Reduce之间序列化和反序列化。

**compareTo** 

比较方法先比较总金额，总金额相同，按照名称倒叙排序。
由于总金额是float类型， 所以使用Float.compare 方法进行比较。

## OrderTopn.java 

Topn 的实现类，包含了两个内部类和一个main方法。

 * Mapper 的输出属性是订单号和订单对象。
 * Reduce的输出属性是订单对象和空（NullWritable）

### OrderTopnMapper

1. 切分输入字符串，生成OrderBean对象
2. 通过context 序列化到文件

代码优化的小细节
128M 数据可能有几百万行数据，MapTask 会调用map方法几百万次，那么需要new 几百万个对象。
把创建对象的语法拿到map方法外面去，只new一个对象。每次这个对象的值有改变都会序列化到文件里。所以不必担心对象指针问题。
```Java 
public class OrderTopn {
	public static class OrderTopnMapper extends Mapper<LongWritable, Text, Text, OrderBean>{
		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {

			String[] field = value.toString().split(",");
			OrderBean orderBean = new OrderBean();
			
			orderBean.set(field[0], field[1], field[2], Float.parseFloat(field[3]), Integer.parseInt(field[4]));
			context.write(new Text(field[0]), orderBean);
		}
	}
}
```

每次调用orderBean.set 方法都会修改对象中的值。
而多次修改会导致orderBean 中存的数据是最后一次修改的数据。
更多细节，在于context.write 方法，这是一个序列化方法，
每次修改orderBean 之后，context都会把值序列化到文件中，
能这么用的原因是：下次修改orderBean值跟上一次序列化是没关系的。
因为序列化已经把值存放到文件里了。 

```Java
public class OrderTopn {
	public static class OrderTopnMapper extends Mapper<LongWritable, Text, Text, OrderBean>{
		OrderBean orderBean = new OrderBean();
		Text k = new Text();
		
		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {

			String[] field = value.toString().split(",");
			
			orderBean.set(field[0], field[1], field[2], Float.parseFloat(field[3]), Integer.parseInt(field[4]));
			k.set(field[0]);
			context.write(new Text(field[0]), orderBean);
		}
	}
}
```

### OrderTopnReducer

1. 新建一个List存放每组订单中的OrderBean对象
2. 使用Collections.sort 方法排序List
3. 通过context 序列化到文件



