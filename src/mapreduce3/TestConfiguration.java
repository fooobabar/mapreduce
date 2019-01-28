package mapreduce3;

import org.apache.hadoop.conf.Configuration;

public class TestConfiguration {
	public static void main(String[] args) {
		Configuration conf = new Configuration();
		
		System.out.println(conf.get("name"));  // 没有值打印出来
		
		conf.addResource("xx-oo.xml");  // 需要手工加载配置文件
		System.out.println(conf.get("name"));  // 有值打印出来
	}
}
