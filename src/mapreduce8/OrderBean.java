package mapreduce8;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class OrderBean implements WritableComparable<OrderBean> {
	private String orderId;
	private String userId;
	private String pdtName;
	private float price;
	private int number;
	private float amountFee;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPdtName() {
		return pdtName;
	}

	public void setPdtName(String pdtName) {
		this.pdtName = pdtName;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public float getAmountFee() {
		return amountFee;
	}

	public void set(String orderId, String userId, String pdtName, float price, int number) {
		this.orderId = orderId;
		this.userId = userId;
		this.pdtName = pdtName;
		this.price = price;
		this.number = number;
		this.amountFee = this.price * this.number;
	}

	/**
	 * hadoop 反序列化的方法
	 */
	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		this.orderId = in.readUTF();
		this.userId = in.readUTF();
		this.pdtName = in.readUTF();
		this.price = in.readFloat();
		this.number = in.readInt();
		this.amountFee = this.price * this.number;
	}

	/**
	 * hadoop 序列化的方法 对类的5个属性序列化，总价不用序列化，因为总价可以通过单价乘以数量算出来。
	 */
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(this.orderId);
		out.writeUTF(this.userId);
		out.writeUTF(this.pdtName);
		out.writeFloat(this.price);
		out.writeInt(this.number);
	}

	@Override
	public int compareTo(OrderBean o) {
		
		//如果订单ID相同，比较商品总金额，
		//如果商品总金额相同，比较商品名称
		//如果订单号相同，总金额相同，商品名相同，认为他们就相同，最终返回0
		if(this.getOrderId().equals(o.getOrderId())){
			return Float.compare(o.getAmountFee(), this.getAmountFee())==0?o.getPdtName().compareTo(this.getPdtName()):Float.compare(o.getAmountFee(), this.getAmountFee());
		}
		return 0;
	}

	@Override
	public String toString() {
		return "orderId=" + orderId + ", userId=" + userId + ", pdtName=" + pdtName + ", price=" + price + ", number="
				+ number + ", amountFee=" + amountFee;
	}

}
