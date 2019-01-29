package mapreduce6;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class IndexStepTwo {
	
	/**
	 * Mapper 可以拿到文件信息，存放到上下文中。
	 * 通过getPath 再 getName 可以拿到文件名
	 * @author iiii
	 *
	 */
	public static class IndexStepOneMapper extends Mapper<LongWritable, Text, Text, Text> {
		protected void map(LongWritable key, Text value,Context context)
				throws java.io.IOException, InterruptedException {
			String[] line = value.toString().split("-");
			context.write(new Text(line[0]), new Text(line[1].replaceAll("\t", "-->")));
		}
 
	}

	public static class IndexStepOneReducer extends Reducer<Text, Text, Text, Text>{
		@Override
		protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			
			//不使用多字符串拼接， 可以提高性能，因为每拼一次字符串都会新产生一个字符串对象。
			StringBuilder sb = new StringBuilder();
			for (Text value : values) {
				sb.append(value).append("\t");
			}
			context.write(key, new Text(sb.toString()));
		}
	}
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		
		Job job = Job.getInstance(conf);
		
		job.setJarByClass(IndexStepTwo.class);
		
		job.setMapperClass(IndexStepOneMapper.class);
		job.setReducerClass(IndexStepOneReducer.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		FileInputFormat.setInputPaths(job, new Path("F:/index/output1"));
		FileOutputFormat.setOutputPath(job, new Path("F:/index/output2"));
		
		boolean res = job.waitForCompletion(true);
		
		System.exit(res?0:1);
	}

}
