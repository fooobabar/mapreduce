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


public class WordCount2 {

	public static class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
		IntWritable v = new IntWritable();
		Text k = new Text("");
		
		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			
			//切分前面步骤的输出步骤
			String[] wordAndCount = value.toString().split("\t");
			
			//以制表符为分隔符，取前面步骤每行的输出步骤第二个字段
			v.set(Integer.parseInt(wordAndCount[1]));
			
			//以制表符为分隔符，取前面步骤每行的输出步骤第一个字段，且以"\001"为分隔符，取第一个字段。
			k.set(wordAndCount[0].split("\001")[0]);
			
			context.write(k, v);
		}
		
	}
	
	/**
	 * 
	 * reduce任务跟前面任务相同，只负责聚合。 所以没做改变。
	 * @author iiii
	 *
	 */
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
		job.setJarByClass(WordCount2.class);
		
		
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
		FileInputFormat.setInputPaths(job, new Path("f:/mr/skew/output3/"));
		FileOutputFormat.setOutputPath(job, new Path("f:/mr/skew/output4/"));
		
		boolean res = job.waitForCompletion(true);
		
		System.exit(res?0:1);
	}
}
