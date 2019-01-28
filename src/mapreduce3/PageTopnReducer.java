package mapreduce3;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class PageTopnReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
	TreeMap<PageCount, Object> treeMap = new TreeMap<>();

	@Override
	protected void reduce(Text key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
		int count=0;
		
		for (IntWritable value : values) {
			count += value.get();
		}
		PageCount pageCount = new PageCount();
		pageCount.set(key.toString(), count);
		treeMap.put(pageCount, null);
		
		//context.write(key, new IntWritable(count));
	}
	
	@Override
	protected void cleanup(Context context)
			throws IOException, InterruptedException {
		
		Configuration conf = context.getConfiguration();
		
		int topn = conf.getInt("top.n", 5);
		int i = 0 ;
		
		Set<Entry<PageCount, Object>> entrySet = treeMap.entrySet();
		
		for (Entry<PageCount, Object> entry : entrySet) {
			
			String page = entry.getKey().getPage();
			
			int count = entry.getKey().getCount();
			
			context.write(new Text(page), new IntWritable(count));
			
			i++;
			
			if(i==topn){
				return;
			}
		}
	}
}
