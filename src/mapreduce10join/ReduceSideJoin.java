package mapreduce10join;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class ReduceSideJoin {

	public static class ReduceSideJoinMapper extends Mapper<LongWritable, Text, Text, JoinBean> {

		// 定义类变量，专门存放文件名
		String filename = null;
		// 新创建一个变量，存放每个数据行
		JoinBean joinbean = new JoinBean();
		
		// 定义变量k，存放Key
		Text k = new Text();
		@Override
		protected void setup(Context context) throws IOException, InterruptedException {
			// 获取文件名
			FileSplit inputSplit = (FileSplit) context.getInputSplit();
			filename = inputSplit.getPath().getName();

		}

		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			//输入的数据类型是逗号列表，使用逗号分隔不同字段
			String[] fileds = value.toString().split(",");

			//判断order开头的数据文件
			if (filename.startsWith("order")) {
				//如果是以order开头，则设置JoinBean对象的orderId，userId，tableName属性，其他属性都是空
				joinbean.set(fileds[0], fileds[1], "NULL", -1, "NULL", "order");
			} else {
				//如果是其他文件，则设置userId，userName，userAge，userFriend 属性，其他属性为空
				joinbean.set("NULL", fileds[0], fileds[1], Integer.parseInt(fileds[2]), fileds[3], "user");
			}
			
			//设置key的值
			k.set(joinbean.getUserId());
			context.write(k, joinbean);

		}
	}
	
	public static class ReduceSideJoinReducer extends Reducer<Text, JoinBean, JoinBean, NullWritable> {

		@Override
		protected void reduce(Text key, Iterable<JoinBean> values, Context context)
				throws IOException, InterruptedException {

			//定义一个缓存，这个存放用户逗号列表
			ArrayList<JoinBean> userList = new ArrayList<>();
			JoinBean userBean = null;
			
			//BeanUtils 工具会抛出两个异常
			try {
				//遍历values，取出每个元素
				for (JoinBean user : values) {
					
					//判断对象是从哪个文件中读取的，order有多个，user有一个
					if ("order".equals(user.getTableName())) {
						
						//如果JoinBean是从order表中出来的，则复制给一个新的对象，然后add到userList中
						JoinBean newBean = new JoinBean();
						
						BeanUtils.copyProperties(newBean, user);
						userList.add(newBean);
						
					} else {
						userBean = new JoinBean();
						BeanUtils.copyProperties(userBean, user);
					}
				}

				//遍历订单列表,跟userBean做关联，补全订单信息。
				for (JoinBean joinBean : userList) {
					joinBean.setUserName(userBean.getUserName());
					joinBean.setUserFriend(userBean.getUserFriend());
					joinBean.setUserAge(userBean.getUserAge());

					context.write(joinBean, NullWritable.get());
				}
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}

		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();

		Job job = Job.getInstance(conf);

		job.setJarByClass(ReduceSideJoin.class);

		job.setMapperClass(ReduceSideJoinMapper.class);
		job.setReducerClass(ReduceSideJoinReducer.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(JoinBean.class);

		job.setOutputKeyClass(JoinBean.class);
		job.setOutputValueClass(NullWritable.class);

		FileInputFormat.setInputPaths(job, new Path("F:/join/input"));
		FileOutputFormat.setOutputPath(job, new Path("F:/join/output1"));

		boolean res = job.waitForCompletion(true);

		System.exit(res ? 0 : 1);
	}
}
