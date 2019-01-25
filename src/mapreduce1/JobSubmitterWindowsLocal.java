package mapreduce1;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class JobSubmitterWindowsLocal {

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		
		conf.set("fs.defaultFS", "file:///");
		conf.set("mapreduce.framework.name", "local");
		Job job = Job.getInstance(conf);
		
		job.setJarByClass(JobSubmitterWindowsLocal.class);

		job.setMapperClass(WordcountMapper.class);
		job.setReducerClass(WordcountReducer.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		job.setNumReduceTasks(3);
		FileInputFormat.setInputPaths(job, new Path("f:/wordcount/input/"));
		FileOutputFormat.setOutputPath(job, new Path("f:/wordcount/output/"));
		
		boolean res = job.waitForCompletion(true);
		
		System.exit(res?0:1);
		
		
	}

}
