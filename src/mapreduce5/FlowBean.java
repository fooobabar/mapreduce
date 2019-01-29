package mapreduce5;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import org.apache.hadoop.io.Writable;

public class FlowBean implements Writable {
	private int upFlow;
	private int dFlow;
	private int amountFlow;
	private String phone;
	public int getUpFlow() {
		return upFlow;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public void setUpFlow(int upFlow) {
		this.upFlow = upFlow;
	}
	public int getdFlow() {
		return dFlow;
	}
	public void setdFlow(int dFlow) {
		this.dFlow = dFlow;
	}
	public int getAmountFlow() {
		return amountFlow;
	}
    
	public FlowBean(int upFlow, int dFlow,String phone) {
		super();
		this.upFlow = upFlow;
		this.dFlow = dFlow;
		this.phone = phone;
		this.amountFlow = upFlow + dFlow;
	}  

	public void set(int upFlow, int dFlow,String phone) {
		this.upFlow = upFlow;
		this.dFlow = dFlow;
		this.phone = phone;
		this.amountFlow = upFlow + dFlow;
	}
	
	
	public FlowBean() {
	}
	
	@Override
	public String toString() {
		return " : "+upFlow + ";" + dFlow + ";" + amountFlow ;
	}
	/**
	 * hadoop系统在序列化该类的对象时要调用的方法
	 * @param arg0
	 * @throws IOException
	 */
	@Override
	public void readFields(DataInput in) throws IOException {
		this.upFlow = in.readInt();
		this.phone = in.readUTF();
		this.dFlow = in.readInt();
		this.amountFlow = in.readInt();
	}
	/**
	 * hadoop系统在反序列化该类的对象时要调用的方法
	 * @param arg0
	 * @throws IOException
	 */
	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		out.writeInt(upFlow);
		out.writeUTF(phone);
		out.writeInt(dFlow);
		out.writeInt(amountFlow);
	}  
	
}
