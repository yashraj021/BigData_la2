package com.yatan.yash;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

public class Counter {
	
	public static  class Map extends MapReduceBase implements Mapper<LongWritable,Text,Text,IntWritable>{
		private final static IntWritable writer=new IntWritable(1);
		public void map(LongWritable key,Text value,OutputCollector<Text,IntWritable> output,Reporter reporter)throws IOException{
			String row = value.toString();

			String[] cols = row.split(",");
			
			if(Integer.parseInt(cols[3])>60)
				output.collect(new Text("Number of Students having score in subject 1> 60:"),writer);
	
			if(cols[5].equals("PASS"))
				output.collect(new Text("Passed Students:"),writer);
	
       }
	}
	
	public static class Reduce extends MapReduceBase implements Reducer<Text,IntWritable,Text,IntWritable>{
		public void reduce(Text key,Iterator<IntWritable> values,OutputCollector<Text,IntWritable> output,Reporter reporter)throws IOException{
			int count=0;
			while(values.hasNext()){
				IntWritable value =values.next();
				count += value.get();	
			}
			output.collect(key, new IntWritable(count));	
		}	
	}

	public static void main(String[] args)throws Exception{
		JobConf conf = new JobConf(Counter.class);
		conf.setJobName("counter_job");

		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(IntWritable.class);

		conf.setMapperClass(Map.class);
		conf.setCombinerClass(Reduce.class);
		conf.setReducerClass(Reduce.class);

		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);

		FileInputFormat.setInputPaths(conf, new Path(args[0]));
		FileOutputFormat.setOutputPath(conf,new Path(args[1]));

		JobClient.runJob(conf);
	}
}
