package com.example.hadoop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
public class MovieClassifyMapper extends Mapper<LongWritable, Text, Text, DistanceAndLabel> {
	private DistanceAndLabel distance_label=new DistanceAndLabel();
	private String splitter="";
	ArrayList<String> testData=new ArrayList<String>();
	private String testPath="";
	private String userID ="";
	@Override
	protected void setup(Mapper<LongWritable, Text, Text, DistanceAndLabel>.Context context)
			throws IOException, InterruptedException {
		Configuration conf=context.getConfiguration();
		splitter=conf.get("SPLITTER");
		testPath=conf.get("TESTPATH");
		userID=conf.get("userID");
		//读取测试数据存于列表testData中
		FileSystem fs=FileSystem.get(conf);
		FSDataInputStream is=fs.open(new Path(testPath));
		BufferedReader br=new BufferedReader(new InputStreamReader(is));
		String line="";
		while((line=br.readLine())!=null){
			testData.add(line);
		}
		is.close();
		br.close();
	}
	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, DistanceAndLabel>.Context context)
			throws IOException, InterruptedException {
		double distance=0.0;
		String[] val=value.toString().split(splitter);
		String[] singleTrainData=Arrays.copyOfRange(val, 5, val.length);
		String label=val[1];
		for (String td: testData) {
			String[] test=td.split(splitter);
			//判断登录用户的ID是否与该测试数据ID一致，一致则输出
			if (test[0].equals(userID)){
				String[] singleTestData=Arrays.copyOfRange(test, 5, test.length);
				distance=Distance(singleTrainData,singleTestData);
				distance_label.setDistance(distance);
				distance_label.setLabel(label);
				context.write(new Text(td), distance_label);
				break;
			}

		}
	}
	/**
	 * 计算训练数据与测试数据的距离
	 * @param singleTrainData
	 * @param singleTestData
	 * @return
	 */
	private double Distance(String[] singleTrainData, String[] singleTestData) {
		double sum=0.0;
		for(int i=0;i<singleTrainData.length;i++){
			sum+=Math.pow(Double.parseDouble(singleTrainData[i]), Double.parseDouble(singleTestData[i]));
		}
		return Math.sqrt(sum);
	}
}
