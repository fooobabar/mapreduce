package mapreduce5;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
/**
 * 四个参数，输入和输出，输出是多值的，所以新创建了FlowBean
 */
public class FlowCountMapper extends Mapper<LongWritable, Text, Text, FlowBean>{
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		String line = value.toString();
		String[] fields = line.split("\t");
		
		String phone=fields[1];
		
		int upFlow = Integer.parseInt(fields[fields.length-3]);
		int dFlow = Integer.parseInt(fields[fields.length-2]);
		
		context.write(new Text(phone), new FlowBean(upFlow, dFlow, phone));
	}
}
