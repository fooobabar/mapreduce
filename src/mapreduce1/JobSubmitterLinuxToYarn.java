package mapreduce1;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 如果要在hadoop集群的某台机器上启动这个job提交客户端的话
 * conf 里面就不需要指定 fs.defaultFS  mapreduce.framework.name
 * 
 * 因为集群及其上用 hadoop jar xx.jar cn.edu360.mr.wc.JobSubmitter2 命令来启动客户端main方法时。
 * hadoop jar 这个命令会将所在及其上的hadoop安装目录中的jar包和配置文件加入到运行时的classpath中
 * 
 * 那么我们的客户端main方法中的new Configuration() 就会加载classpath中的配置文件
 * 自然就有 ：
 *     fs.defaultFS
 *     mapreduce.framework.name
 *     yarn.resourcemanager.hostname 
 *   这些参数配置。
 *   
 * @author iiii
 *
 */
public class JobSubmitterLinuxToYarn {

	public static void main(String[] args) throws Exception {
		// 这个类会上传到hadoop的节点运行，下面的configuration 在加载的时候会加载所有classpath中的配置文件。
		// 所以不需要写基本的配置参数。
		Configuration conf = new Configuration();
		
		
		//获取job对象
		Job job = Job.getInstance(conf);
		
		//设置jar包路径
		job.setJarByClass(JobSubmitterLinuxToYarn.class);
		
		
		//设置map类名
		job.setMapperClass(WordcountMapper.class);
		
		//设置reduce类名
		job.setReducerClass(WordcountReducer.class);
		
		//设置map输出kv类型
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		//设置reduce输出kv类型
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		
		//设置输入文件目录
		FileInputFormat.setInputPaths(job, new Path("/wordcount/input"));
		
		//设置输出文件目录
		FileOutputFormat.setOutputPath(job, new Path("/wordcount/output"));
		
		//提交jar包
		boolean res = job.waitForCompletion(true);
		
		//程序运行结束返回code
		System.exit(res?0:1);
	}

}
