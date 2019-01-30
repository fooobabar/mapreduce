package mapreduce7;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;



public class OrderTopn {
	public static class OrderTopnMapper extends Mapper<LongWritable, Text, Text, OrderBean>{
		
		//在map方法外创建一个OrderBean对象，避免每行数据都调用map，不需要创建多个OrderBean对象
		OrderBean orderBean = new OrderBean();
		Text k = new Text();
		
		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {

			String[] field = value.toString().split(",");
			
			//每次修改好orderBean对象，都会序列化到文件中，所以不必担心对象指针问题。
			orderBean.set(field[0], field[1], field[2], Float.parseFloat(field[3]), Integer.parseInt(field[4]));
			k.set(field[0]);
			
			//序列化到文件
			context.write(k, orderBean);
		}
	}
	
	public static class OrderTopnReducer extends Reducer<Text, OrderBean, OrderBean, NullWritable>{
		
		@Override
		protected void reduce(Text key, Iterable<OrderBean> values, Context context)
				throws IOException, InterruptedException {
			// 通过context 拿到配置文件中的配置信息
			int topn = context.getConfiguration().getInt("order.top.n", 3);
			
			// 存放每组订单中的订单对象
			ArrayList<OrderBean> arrayBean = new ArrayList<>();
			for (OrderBean orderBean : values) {
				//每多遍历一个订单对象，则会新创建一个OrderBean对象，所以List中存放的对象每次都不同。
				OrderBean newOrderBean = new OrderBean();
				newOrderBean.set(orderBean.getOrderId(), orderBean.getUserId(), orderBean.getPdtName(), orderBean.getPrice(), orderBean.getNumber());
				
				arrayBean.add(newOrderBean);
			}
			
			//排序List
			Collections.sort(arrayBean);
			
			//只序列化感兴趣的数据，比如topn配置3，那么每组订单序列化前3行数据。
			for (int i = 0; i < topn; i++) {
				context.write(arrayBean.get(i), NullWritable.get());
			}
		}
	}
	
	public static void main(String[] args) throws Exception{
		Configuration conf = new Configuration();
		
		//模拟从配置文件中获取配置信息。
		conf.setInt("order.top.n", 3);
		Job job = Job.getInstance(conf);
		
		job.setJarByClass(OrderTopnMapper.class);
		
		job.setMapperClass(OrderTopnMapper.class);
		job.setReducerClass(OrderTopnReducer.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(OrderBean.class);
		
		job.setOutputKeyClass(OrderBean.class);
		job.setOutputValueClass(NullWritable.class);
		
		FileInputFormat.setInputPaths(job, new Path("F:/order/input"));
		FileOutputFormat.setOutputPath(job, new Path("F:/order/output1"));
		
		boolean res = job.waitForCompletion(true);
		
		System.exit(res?0:1);
	}
}
