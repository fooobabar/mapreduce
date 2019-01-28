package mapreduce2;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class JobSubmitter {

	public static void main(String[] args) throws Exception{
		Configuration conf = new Configuration();
		
		Job job = Job.getInstance(conf);
		job.setMapperClass(FlowCountMapper.class);
		job.setReducerClass(FlowCountReducer.class);
		
		job.setJarByClass(JobSubmitter.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(FlowBean.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(FlowBean.class);

		job.setNumReduceTasks(3);
		
		FileInputFormat.setInputPaths(job, new Path("F:/flow/input"));
		FileOutputFormat.setOutputPath(job, new Path("F:/flow/output"));
		
		boolean res = job.waitForCompletion(true);
		System.exit(res?0:1);
	}

}
