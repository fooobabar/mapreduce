package exercise1userlist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.htrace.core.TraceExecutorService;

public class UserListReducer extends Reducer<Text, Text, Text, Text> {

	//Key是好友，Value是用户，跟文本文件中的内容相反
	HashMap<String, String> friendMap = new HashMap<>();
	TreeSet<String> userNameSet = new TreeSet<>();

	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		String userNameList = "";
		for (Text userName : values) {
			userNameSet.add(userName.toString());
			userNameList = userNameList + "," + userName;
		}
		friendMap.put(key.toString(), userNameList);
		// context.write(key, new Text(userNameList.substring(1)));
	}

	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		Object[] array = userNameSet.toArray();

		for (int i = 0; i < array.length; i++) {
			for (int j = i + 1; j < array.length; j++) {
				String user1 = (String) array[i];
				String user2 = (String) array[j];
				
				//变量commonFriendList 用来存放共同好友
				String commonFriendList = "";
				
				Set<Entry<String, String>> friends = friendMap.entrySet();
				for (Entry<String, String> entry : friends) {
					String string = entry.getValue(); 
					int inx_user1 = string.indexOf(user1);
					int inx_user2 = string.indexOf(user2);
					
					
					if (inx_user1 != -1 && inx_user2 != -1) {
						commonFriendList=commonFriendList+","+entry.getKey();
					}
				}
				
				//多个共同好友时，删掉字符串开头的逗号
				if(commonFriendList.startsWith(",")){
					commonFriendList = commonFriendList.substring(1);
				}
				
				//如果没有共同好友，则跳出本次循环，不写入
				if(commonFriendList.isEmpty()){
					break;
				}
				
				//写入两个用户的，以及用户的共同好友。 
				context.write(new Text(user1+"-"+user2), new Text(commonFriendList)); 
			}
		}

	}
}
