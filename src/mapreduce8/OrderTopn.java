package mapreduce8;

import java.io.IOException;

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
	public static class OrderTopnMapper extends Mapper<LongWritable, Text, OrderBean,NullWritable >{
		
		OrderBean orderBean = new OrderBean();
		NullWritable v = NullWritable.get();
		
		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] field = value.toString().split(",");
			
			orderBean.set(field[0], field[1], field[2], Float.parseFloat(field[3]), Integer.parseInt(field[4]));
			
			context.write(orderBean, v);
		}
	}
	
	public static class OrderTopnReducer extends Reducer<OrderBean, NullWritable, OrderBean, NullWritable>{
		@Override
		protected void reduce(OrderBean key, Iterable<NullWritable> values, Context context)
				throws IOException, InterruptedException {
			int topn = context.getConfiguration().getInt("order.top.n", 3);
			int count = 0 ; 
			
			//每迭代一次，key都会变
			for (NullWritable v : values) {
				
				count++;
				if(count>topn ){
					return ;
				}
				context.write(key, v);
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
		
		job.setMapOutputKeyClass(OrderBean.class);
		job.setMapOutputValueClass(NullWritable.class);
		
		job.setGroupingComparatorClass(OrderIdGroupingComparator.class);
		job.setPartitionerClass(OrderIdPartitioner.class);
		
		job.setOutputKeyClass(OrderBean.class);
		job.setOutputValueClass(NullWritable.class);
		
		job.setNumReduceTasks(3);
		FileInputFormat.setInputPaths(job, new Path("F:/order/input"));
		FileOutputFormat.setOutputPath(job, new Path("F:/order/output2"));
		
		boolean res = job.waitForCompletion(true);
		
		System.exit(res?0:1);
	}
}
