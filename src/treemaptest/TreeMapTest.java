package treemaptest;

import java.util.Map.Entry;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeMap;

public class TreeMapTest {

	public static void main(String[] args) {
		User u1 = new User("u01", "xiaoming", 10, 1000f);
		User u2 = new User("u02", "xiaoming", 50, 1000f);
		User u3 = new User("u03", "xiaoming", 90, 13000f);
		User u4 = new User("u04", "xiaoming", 2, 12200f);
		User u5 = new User("u05", "xiaoming", 50, 5000f);
		
		/**
		 * 创建TreeMap 之前，可以传入一个比较器。用来指定某个类型是如何比较的。
		 * 如果比较结果相同，则TreeMap会多相同数据做去重。
		 */
		TreeMap<User, String> tm1 = new TreeMap<>(new Comparator<User>() {

			@Override
			public int compare(User o1, User o2) {

				if (o1.getAge()==o2.getAge()){
					return o1.getId().compareTo(o2.getId());
				}
				
				return o1.getAge() - o2.getAge();
			}
		});
		
		tm1.put(u1, null);
		tm1.put(u2, null);
		tm1.put(u3, null);
		tm1.put(u4, null);
		tm1.put(u5, null);
		
		Set<Entry<User, String>> users = tm1.entrySet();
		for (Entry<User, String> user : users) {
			System.out.println(user.toString());
		}
	}

}
