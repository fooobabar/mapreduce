package exercise1userlist;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

/**
 * 写测试类，试试如何切分字符串
 * @author iiii
 *
 */
public class TestConbination {

	public static void main(String[] args) throws Exception {
		
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("E:\\git\\mapreduce\\src\\exercise1userlist\\xx.txt")));
		String line = null;
		while((line=br.readLine())!=null){
			String[] field = line.split(":");
			String userName = field[0];
			String[] friendArray = field[1].split(",");
			for (String friend : friendArray) {
				System.out.println(friend+" , "+userName);
			}
		}
	}

}
