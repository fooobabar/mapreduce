package exercise1userlist;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * 创建一个列表，处理一次需求文档中的内容，
 * <user,friends>  => <friend,users>
 * 
 * @author iiii
 *
 */
public class UserListMapper extends Mapper<LongWritable, Text, Text,Text> {
	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		
		String[] fields = value.toString().split(":");
		String userName = fields[0];
		String[] friendArray = fields[1].split(",");
		for (String friend : friendArray) {
			context.write(new Text(friend), new Text(userName));
		}
	}
}
