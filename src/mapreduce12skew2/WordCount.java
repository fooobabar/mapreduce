package mapreduce12skew2;

import java.io.IOException;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class WordCount {

	public static class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
		Random random = new Random();   //根据reduce数量，生成随机数
		IntWritable v = new IntWritable(1);
		Text k = new Text("");
		int reducenum = 0 ;  // 获取reduce task的数量
		
		@Override
		protected void setup(Context context)
				throws IOException, InterruptedException {

			reducenum = context.getNumReduceTasks();
		}
		
		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			
			String[] words = value.toString().split(" ");
			for (String word : words) {

				k.set(word+"\001"+random.nextInt(reducenum));
				
				context.write(k, v);
			}
			
		}
		
	}
	
	public static class WordCountReduce extends Reducer<Text, IntWritable, Text, IntWritable>{
		@Override
		protected void reduce(Text key, Iterable<IntWritable> values,Context context) throws IOException, InterruptedException {
			int count=0;
			for (IntWritable value : values) { 
				count=count+value.get();
			}
			context.write(key, new IntWritable(count));
			
		}
	}
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		
		Job job = Job.getInstance(conf);
		job.setJarByClass(WordCount.class);
		
		
		job.setMapperClass(WordCountMapper.class);
		job.setReducerClass(WordCountReduce.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		//这个是map阶段完成的任务，合并文件之前，先自己做一次聚合
		//之所以可以使用Reduce task 的实现类，是因为合并算法跟reduce task相同
		//如果不相同， 需要自己实现一个Reducer类，
		job.setCombinerClass(WordCountReduce.class);
		
		job.setNumReduceTasks(3);
		FileInputFormat.setInputPaths(job, new Path("f:/mr/skew/input/"));
		FileOutputFormat.setOutputPath(job, new Path("f:/mr/skew/output3/"));
		
		boolean res = job.waitForCompletion(true);
		
		System.exit(res?0:1);
	}
}
