package mapreduce6;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class IndexStepOne {
	
	/**
	 * Mapper 可以拿到文件信息，存放到上下文中。
	 * 通过getPath 再 getName 可以拿到文件名
	 * @author iiii
	 *
	 */
	public static class IndexStepOneMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
		protected void map(LongWritable key, Text value,Context context)
				throws java.io.IOException, InterruptedException {
			FileSplit fs = (FileSplit)context.getInputSplit();
			String fileName = fs.getPath().getName();
			
			String[] words = value.toString().split(" ");
			for (String word : words) { 
				context.write(new Text(word+"-"+fileName),new IntWritable(1));
			}
			
		}
 
	}

	public static class IndexStepOneReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
		@Override
		protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
			int count=0;
			for (IntWritable intWritable : values) {
				count+=intWritable.get();
			}
			context.write(key, new IntWritable(count));
		}
	}
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		
		Job job = Job.getInstance(conf);
		
		job.setJarByClass(IndexStepOne.class);
		
		job.setMapperClass(IndexStepOneMapper.class);
		job.setReducerClass(IndexStepOneReducer.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		FileInputFormat.setInputPaths(job, new Path("F:/index/input"));
		FileOutputFormat.setOutputPath(job, new Path("F:/index/output1"));
		
		boolean res = job.waitForCompletion(true);
		
		System.exit(res?0:1);
	}

}
