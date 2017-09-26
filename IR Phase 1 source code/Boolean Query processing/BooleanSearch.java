package BooleanSearch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class BooleanSearch {

	public static ArrayList<String> termsArray1 = new ArrayList<String>(Arrays.asList(""));
	public static ArrayList<String> termsArray2 = new ArrayList<String>(Arrays.asList(""));
    public static class BooleanSearchMap extends Mapper<Object,Text,Text,Text>{
    	private String boolWord = new String();

    	private String boolmethod = new String();
    	private int method;
        public void map( Object key, Text value, Context context )
            throws IOException, InterruptedException

        {
        	FileReader fileread = new FileReader("input/searchinput.txt");
	        BufferedReader bufferrd = new BufferedReader(fileread);           
	        String inputstring = null;
           try{  
            	while((inputstring = bufferrd.readLine()) != null) {
		        	String[] inputvalue = inputstring.split("\t");
		        	System.out.println(inputvalue[0]);
		        	System.out.println(inputvalue[1]);
		        	boolmethod = inputvalue[0];
		        	boolWord = inputvalue[1];
	            	if(boolmethod.equals("uniword")){ 
	            		method = 1;	
	            	}

	            	else if(boolmethod.equals("position")){ 
	            		method = 2;
	            		System.out.println("enter position!");
	            	}

	            	else if(boolmethod.equals("biword")){
	            		method = 3;
	            	}
		            if(boolWord.contains("and") || boolWord.contains("or") ||
		             boolWord.contains("not")){
		            	BooleanQueryProcess(method,boolWord);
		            }
		            else{
		            	GetBooleanSearchResult(method,boolWord);
		            }
		        }
            	bufferrd.close();

            }

            catch (Exception e) {

            	e.printStackTrace();

            }
        }  
    } 

    public static void GetBooleanSearchResult(int smethod,String searchword){
        try {
        	
	        String indexfilepath = new String();
	        if(smethod == 1){
	        	indexfilepath = "output/output18/UniwordOP";	        	
	        }
	        else if(smethod == 2){
	        	indexfilepath = "output/output19/BiwordOP";	
	        }
	        else if(smethod == 3){
	        	indexfilepath = "output/output20/PositionalOP";
	        }
	        else{
	        	System.out.println("invalid search method!");
	        	return;
	        }
	        FileReader reader = new FileReader(indexfilepath);
	        BufferedReader buffr = new BufferedReader(reader);

	        String str = null;

	        while((str = buffr.readLine()) != null) {
	        	  String[] indexelement = str.split("\t");
	        	  String indexkey = indexelement[0];
	              if(searchword.equals(indexkey)){	            	  
	            	  FileWriter writer = new FileWriter("output/searchresult.txt");
	                  BufferedWriter bufferWritter = new BufferedWriter(writer);
	                  String matchvalue = indexkey + "\t" + indexelement[1];
	                  bufferWritter.write(matchvalue);
	                  bufferWritter.close();
	                  writer.close();
	              }
	        }

	        buffr.close();
	        reader.close();

        }
        catch (Exception e) {
        	e.printStackTrace();

        }    
    }

    public static void BooleanQueryProcess(int smethod,String sword){
        try {

	        String indexfilepath = new String();
	        String[] firstposting = null;
	        String[] secondposting = null;
	        if(smethod == 1){
	        	indexfilepath = "output/output18/UniwordOP";	        	
	        }
	        else if(smethod == 2){
	        	indexfilepath = "output/output19/BiwordOP";	
	        }
	        else if(smethod == 3){
	        	indexfilepath = "output/output20/PositionalOP";
	        }
	        else{
	        	System.out.println("invalid search method!");
	        	return;
	        }

	        FileReader reader = new FileReader(indexfilepath);
	        BufferedReader buffread = new BufferedReader(reader);	       
	        String str = null;
	        String[] querypiece = sword.split(" ");
	        int i = 0;
	        while(i < querypiece.length-1){
	        	while((str = buffread.readLine()) != null) {
	        		
		        	  String[] indexelement = str.split("\t");
	  	        	  String indexkey = indexelement[0];
	  	        	  
	  	        	  if(querypiece[i].equals(indexkey) && termsArray1.isEmpty()){	            	  
	  	            	  String indexvalue1 = indexelement[1];	  	          
	  	            	  firstposting = indexvalue1.split(";");
	  	            	  
	  	            	  for(int p = 0; p< firstposting.length;p++){
	  	            		termsArray1.add(firstposting[p]);
	  	            	  } 
	  	              }

	  	              else if(querypiece[i+2].equals(indexkey)){	            	  
	  	            	  String indexvalue2 = indexelement[1];
	  	            	  secondposting = indexvalue2.split(";");

	  	            	  for(int q = 0; q< secondposting.length;q++){
	  	            		termsArray2.add(secondposting[q]);
	  	            	  } 
	  	              }
		        }

	        	if(querypiece[i+1].equals("and")){
	        		termsArray1.retainAll(termsArray2);
	        	}

	        	else if(querypiece[i+1].equals("or")){
	        		int m=0;
	        		while(m<termsArray2.size()){
	        			if(termsArray1.contains(termsArray2.get(i))){
	        				m++;

	        			}else{
	        				termsArray1.add(termsArray2.get(m));
	        				m++;
	        			}
	        		}
	        	}
	        	
	        	else if(querypiece[i+1].equals("not")){
	        		int n=0;
	        		
	        		while(n<termsArray2.size()){
	        			if(termsArray1.contains(termsArray2.get(n))){
	        				termsArray1.remove(termsArray2.get(n));
	        				n++;
	        			}else{
	        				n++;
	        			}
	        		}
	        	}	        	
	        	i = i+2; 

	        }

	        System.out.println(termsArray1);
	        FileWriter writer = new FileWriter("output/searchresult.txt");
            BufferedWriter bw = new BufferedWriter(writer);
            
	        for(int s=0;s<termsArray1.size();s++){
	            bw.write(termsArray1.get(i)+";");

	        }

            bw.close();
            writer.close();           
            buffread.close();
	        reader.close();  
        }
        catch (Exception e) {
        	e.printStackTrace();

        }    
    }

    public static class SearchwordReduce extends Reducer<Text,Text,Text,Text>{
        public void reduce(Text key, Iterable<Text> values,Context contex)
                throws IOException, InterruptedException {
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {

    	Configuration conf = new Configuration();

        Job job = new Job(conf,"SearchWord");

        job.setJarByClass(BooleanSearch.class);
        job.setMapperClass(BooleanSearchMap.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setCombinerClass(SearchwordReduce.class);
        job.setReducerClass(SearchwordReduce.class);
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true)?0:1);
    }

}
