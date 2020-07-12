package com.example.hadoop;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
public class MovieClassify extends Configured implements Tool{
	@Override
	public int run(String[] args) throws Exception {
		if(args.length!=6){
			System.err.println("demo.MovieClassify <testinput> <traininput> <output> <k> <splitter> <userID>");
			System.exit(-1);
		}
		Configuration conf=new Configuration();
		conf.setInt("K", Integer.parseInt(args[3]));
		conf.set("SPLITTER",args[4]);
		conf.set("TESTPATH", args[0]);
		//增加用户ID参数
		conf.set("userID",args[5]);
		Job job=Job.getInstance(conf, "movie_knn");
		job.setJarByClass(MovieClassify.class);//设置主类
		job.setMapperClass(MovieClassifyMapper.class);//设置Mapper类
		job.setReducerClass(MovieClassifyReducer.class);//设置Reducer类
		job.setMapOutputKeyClass(Text.class);//设置Mapper输出的键类型
		job.setMapOutputValueClass(DistanceAndLabel.class);//设置Mapper输出的值类型
		job.setOutputKeyClass(Text.class);//设置Reducer输出的键类型
		job.setOutputValueClass(NullWritable.class);//设置Reducer输出的值类型
		FileInputFormat.addInputPath(job, new Path(args[1]));//设置输入路径
		FileSystem.get(conf).delete(new Path(args[2]), true);//删除输出路径
		FileOutputFormat.setOutputPath(job, new Path(args[2]));//设置输出路径
		return job.waitForCompletion(true)?-1:1;//提交任务
	}
	public void main(Configuration conf,String userID) {
		String[] myArgs={
				"/movie/testData",
				"/movie/trainData",
				"/movie/knnout",
				"3",
				",",
				userID  //增加用户参数用户ID
		};
		try {
			ToolRunner.run(conf, new MovieClassify(), myArgs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
