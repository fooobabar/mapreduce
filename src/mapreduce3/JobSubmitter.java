package mapreduce3;


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
		conf.addResource("topn-site.xml");
		
		Job job = Job.getInstance(conf);
		
		job.setJarByClass(JobSubmitter.class);
		job.setMapperClass(PageTopnMapper.class);
		job.setReducerClass(PageTopnReducer.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		FileInputFormat.setInputPaths(job, new Path("F:/PageCount/input"));
		FileOutputFormat.setOutputPath(job, new Path("F:/PageCount/output"));
		
		boolean res = job.waitForCompletion(true);
		
		System.exit(res?0:1);
		
	}

}
