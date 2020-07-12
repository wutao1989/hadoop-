package com.example.hadoop;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.Writable;
public class DistanceAndLabel implements Writable{
	private double distance;
	private String label;
	public DistanceAndLabel() {
	}
	public DistanceAndLabel(double distance,String label) {
		this.distance=distance;
		this.label=label;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 *先读取距离，再读取类别
	 */
	@Override
	public void readFields(DataInput in) throws IOException {
		this.distance=in.readDouble();
		this.label=in.readUTF();

	}
	/**
	 * 先把distnce写入out输出流
	 * 再把label写入out输出流
	 */
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeDouble(distance);
		out.writeUTF(label);

	}
	/**
	 * 使用空格将距离和类别连接成字符串
	 */
	@Override
	public String toString() {
		return this.distance+","+this.label;
	}
}
