package mapreduce5;

import java.util.HashMap;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;
/**
 * 本类是提供给MapTask用的
 * MapTask通过这个类的getPartition方法，来计算它所产生的每一对kv数据该分发给哪一个reduce task
 *   Text  是key
 *   FlowBean 是value
 * 
 */
public class ProvincePartitioner extends Partitioner<Text, FlowBean> {

	/**
	 * HashMap<String, Integer> 
	 *  String 存放电话号码前缀
	 *  Integer 存放分给第几个reduce
	 */
	static HashMap<String, Integer> codeMap = new HashMap<>();
	
	/**
	 * 静态初始化块用来初始化静态变量，这个变量会给不同的maptask使用
	 */
	static {
		//这部分的put，是为了模拟从数据库里抓出数据
		codeMap.put("135", 0);
		codeMap.put("136", 1);
		codeMap.put("137", 2);
		codeMap.put("138", 3);
		codeMap.put("139", 4);
	}
	@Override
	public int getPartition(Text key, FlowBean value, int numPartitions) {
		
		Integer code = codeMap.get(key.toString().substring(0, 3));
		return code ==null?5:code;
	}

}
