
## 需求

有订单（order)数据和用户（user）信息数据，都是文本数据，现在想关联订单数据中用户信息。

## sql实现

```sql
select * 
   from order a 
   join user b 
    on a.userid = b.userid;
```

## 需求分析



 1. mapreduce 的编程思想是计算key-value。如果想实现join，则应该使用关联字段做key。
在这个需求中，要求使用userid 进行关联，则使用userid作为key。


 2. 每行数据存储的信息量比较多，所以先创建实体类。存储关联之后的数据。
先创建joinbean对象，专门存储关联之后的对象。当使用Userid做Key的时候，每组数据只包含一行用户信息。若干行订单信息。

 3. 对于不同文件做不同的操作，比如数据来自用户表，则只需要存储用户相关信息到joinbean对象中。如果数据来自订单表，则只记录订单相关的数据到joinbean对象中。要对两个文件做join操作， 那么需要对输入文件的文件名做记录。

 4. 获取文件名称使用context.getInputSplit() 方法。


 5. 如果把context.getInputSplit() 放到map方法中，每次进来数据都要调用一次这个方法。而我们知道，每个maptask处理数据的时候，都是一个文件的数据切片，则说明文件名都是固定值，所以只需要get文件名一次就可以了。引入了setup() 方法。

 6. setup() : maptask 在做数据处理时，会先调用一次setup，只调用一次。调用之后，才会对每行数据反复调用map() 方法。

 7. 在setup()方法中获取文件名。从map中判断来自哪个文件。设置各自的tablename。那么输出结果中，以关联字段userid分组，每组来自order文件的数据会有多行，来自user表中的数据会有一行。reduce方法中Key就是userid，value是一个迭代器，其中包含了一行来自user文件的数据，若干行来自order文件的数据。


### 实现方法1（循环两次）
遍历迭代器，判断，来自Order文件的，则缓存到一个List中。来自User文件的，则放到一个joinbean对象中（只有一行来自User文件的数据）。
value 迭代器遍历完之后，再循环遍历一次List，把来自Order文件的对象，信息，用joinbean对象的信息补全，然后context.write()

### 实现方法2（循环一次）
对于每一组数据，都是有规律的，只包含一行来自User的数据，包含若干行来自Order的数据，跟前面的步骤比，需要先处理一次排序，让User的数据排在第一名，来自Order的数据排在后面，则只需要一次循环即可，循环values迭代器时，先取第一条缓存到joinbean中，然后遍历剩下的对象，剩下的对象都是缺少User数据的Order对象，只需要做set即可。 

修改排序，要考虑两个：
  * userId 
  * tablename

对于Key做了修改，但是分组也会变化。 所以也要考虑分组。