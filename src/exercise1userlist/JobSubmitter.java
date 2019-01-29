package exercise1userlist;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class JobSubmitter {

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration(); 
		
		Job job = Job.getInstance(conf);
		
		job.setJarByClass(JobSubmitter.class);
		job.setMapperClass(UserListMapper.class);
		job.setReducerClass(UserListReducer.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		FileInputFormat.setInputPaths(job, new Path("F:/UserList/input"));
		FileOutputFormat.setOutputPath(job, new Path("F:/UserList/output"));
		
		boolean res = job.waitForCompletion(true);
		
		System.exit(res?0:1);
	}

}
