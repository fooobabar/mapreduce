package mapreduce8;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class OrderIdGroupingComparator extends WritableComparator {

	//实现比较方法，这个WritableComparable 接口被OrderBean实现了， 所以可以强转，
	//获取到GroupId之后，使用compareTo 方法比较传入的两个对象，是否订单号相同。
	@Override
	public int compare(WritableComparable a, WritableComparable b) {
		OrderBean o1 = (OrderBean) a;
		OrderBean o2 = (OrderBean) b;

		return o1.getOrderId().compareTo(o2.getOrderId());
	}
	
	//需要调用父类的构造方法，创建出a和b两个对象
	public OrderIdGroupingComparator() {
		super(OrderBean.class, true);
	}
}
