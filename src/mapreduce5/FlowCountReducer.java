package mapreduce5;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * key 是某个手机号，
 * values:是这个手机号所产生的所有访问记录中流量数量
 * @author iiii
 *
 */
public class FlowCountReducer extends Reducer<Text, FlowBean, Text, FlowBean> {
	@Override
	protected void reduce(Text key, Iterable<FlowBean> values, Context context)
			throws IOException, InterruptedException {
		int upflowSum = 0 ;
		int dflowSum = 0 ; 
		for (FlowBean value : values) {
			upflowSum += value.getUpFlow();
			dflowSum += value.getdFlow(); 
		}
		FlowBean flowBean = new FlowBean(upflowSum,dflowSum,key.toString());
		context.write(key, flowBean);
	}
}
