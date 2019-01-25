package mapreduce1;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
 

/**
 * 
 * 用于提交mapreduce job的客户端程序
 * 功能：
 *   1. 封装了本次job运行时所需要的必要参数
 *   2. 跟yarn进行交互，将mapreduce程序成功的启动、运行
 *   
 * @author iiii
 *
 */
public class JobSubmitterWindowsToYarn {

	public static void main(String[] args) throws Exception {
		
		//通过环境传入系统变量
		System.setProperty("HADOOP_USER_NAME", "root"); 
		
		Configuration conf = new Configuration();
		
		// 1. 设置job运行时要访问的默认文件系统
		conf.set("fs.defaultFS", "hdfs://hdp-01:9000");
		
		// 2. 设置job提交到哪去运行
		/*
		 * mr任务有两种方式运行，
		 * 一种是直接在本地，启动模拟器运行，指定为local
		 * 另外一种是提交到yarn集群运行，指定为yarn
		 */
		conf.set("mapreduce.framework.name", "yarn");
		conf.set("yarn.resourcemanager.hostname", "hdp-01");
		
		// 3. 如果想从windows系统上运行这个job，可以新增夸平台提交的参数
		conf.set("mapreduce.app-submission.cross-platform", "true");
		
		//获取job，需要传入conf信息
		Job job = Job.getInstance(conf);
		
		// 1. 封装参数： jar包所在的位置，下面是注释代码，jar包写死了，不方便扩展
		job.setJar("d:/wc.jar");
		
		// 根据正在运行的类找到jar包
		//job.setJarByClass(JobSubmitter.class);
		
		// 2. 封装参数：本次job所要调用的Mapper / Reduce实现类
		job.setMapperClass(WordcountMapper.class);
		job.setReducerClass(WordcountReducer.class);
		
		// 3. 封装参数：本次job的Mapper实现类产生结果数据的Key，Value类型
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		// Reduce 实现类产生的结果数据的Key， VALUE类型
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		FileSystem fs = FileSystem.get(new URI("hdfs://hdp-01:9000"),conf, "root");
		
		Path input = new Path("/wordcount/input");
		if(fs.exists(input)){
			System.out.println("目录存在");
		}
		// 4. 封装参数：本次job要处理的输入数据集所在路径
		// FileInputFormat 有两个包会包括这个类，选择长的包，短的包是mr1中的类
		FileInputFormat.setInputPaths(job, "/wordcount/input");
		
		Path output = new Path("/wordcount/output");
		
		if(fs.exists(output)){
			fs.delete(output, true);
		}
		
		fs.close();
		
		// 最终结果的输出路径，这个路径一定不存在，不然有异常
		FileOutputFormat.setOutputPath(job,output);
		
		// 5. 封装参数：想要启动的reduce task的数量，
		// map task 根据切片自动启动，reduce 需要手工指定，默认情况是1个reduce任务
		job.setNumReduceTasks(2);
		
		// 6. 向yarn 提交本次job
		boolean res = job.waitForCompletion(true);
		
		// 下面的submit也能提交任务到yarn，但是会立刻释放控制台，
		// 不管任务是否成功，所以使用waitForCompletion,等待MR返回运行结果
		//job.submit();
		
		//退出，返回res
		System.exit(res?0:1);
		
	}
	

}
