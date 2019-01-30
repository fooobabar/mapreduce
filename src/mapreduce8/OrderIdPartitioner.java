package mapreduce8;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Partitioner;

/**
 * 希望可以通过OrderBean中的orderID 来区分不同的reduce task，
 * 而不是通过OrderBean 来区分不同的reduce task，
 * 这样，订单相同，但是价格不同的key也会被分到reduce  task中。
 * 
 * @author iiii
 *
 */
public class OrderIdPartitioner extends Partitioner<OrderBean, NullWritable> {

	@Override
	public int getPartition(OrderBean key, NullWritable value, int reducenum) {

		//根据订单ID模分reduce任务
		return (key.getOrderId().hashCode() & Integer.MAX_VALUE)%reducenum;
	}
 

}
