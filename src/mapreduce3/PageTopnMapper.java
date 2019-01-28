package mapreduce3;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class PageTopnMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
	protected void map(LongWritable key, Text value,Context context)
			throws java.io.IOException, InterruptedException {
		String[] line = value.toString().split(" ");
		context.write(new Text(line[1]),new IntWritable(1));
	};
}
