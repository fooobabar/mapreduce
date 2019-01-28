package mapreduce4;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class PageCountStep2 {
	public static class PageCountStep2Mapper extends Mapper<LongWritable, Text, PageCount, NullWritable>{
		@Override
		protected void map(LongWritable key, Text value,Context context)
				throws IOException, InterruptedException {
			String[] line = value.toString().split("\t");
			PageCount pageCount = new PageCount();
			pageCount.set(line[0], Integer.parseInt(line[1]));
			context.write(pageCount, NullWritable.get()); 
		}
		
		
	}
	
	public static class PageCountStep2Reducer extends Reducer<PageCount, NullWritable, Text, IntWritable>{
		
		@Override
		protected void reduce(PageCount key, Iterable<NullWritable> values, Context context)
				throws IOException, InterruptedException {
			context.write(new Text(key.getPage()),new IntWritable(key.getCount()));
		}
		
	}
	
	public static void main(String[] args) throws Exception{
		Configuration conf = new Configuration();
		
		Job job = Job.getInstance(conf);
		
		job.setJarByClass(PageCountStep2.class);
		
		job.setMapperClass(PageCountStep2Mapper.class);
		
		job.setReducerClass(PageCountStep2Reducer.class);
		
		job.setMapOutputKeyClass(PageCount.class);
		job.setMapOutputValueClass(NullWritable.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		job.setNumReduceTasks(1);
		
		FileInputFormat.setInputPaths(job, new Path("F:/pagecountsort/output"));
		FileOutputFormat.setOutputPath(job, new Path("F:/pagecountsort/output2"));
		
		boolean res = job.waitForCompletion(true);
		
		System.exit(res?0:1);
	}
}
