package com.insight;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.*;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public class TopTenH1bStatistics {
	static long total = 0;
	/*
	 * Main method
	 * 1. reads input args, first arg for input file and next two for output file names
	 * 2. calls getTopTen() which reads the files into a map and calls outputToFile()
	 */
	public static void main(String[] args) {
		String inFile  = args[0]; //"input/H1B_FY_2015.csv";
		String occupationsOutFile = args[1]; //"output/top_10_occupations.txt";
		String statesOutfile = args[2]; //"output/top_10_states.txt";
		
		// This delimiter ensures that it does not split the string when the ";" is between quotes
		String delimiter = ";(?=[^\"]*(?:(?:\"[^\"]*){2})*$)";
		
		String occupationsHeader = "TOP_OCCUPATIONS;NUMBER_CERTIFIED_APPLICATIONS;PERCENTAGE";
		String statesHeader 	 = "TOP_STATES;NUMBER_CERTIFIED_APPLICATIONS;PERCENTAGE";
		
		int occupationsDrivingCol = 23;
		int statesDrivingCol = 50;
				
		TopTenH1bStatistics top10 = new TopTenH1bStatistics();
		top10.getTopTen(inFile, occupationsOutFile, delimiter, occupationsHeader, occupationsDrivingCol);
		top10.getTopTen(inFile, statesOutfile, delimiter, statesHeader, statesDrivingCol);			
	}
	
	
	/*
	 * 1. Read the file
	 * 2. Load the data into a map and filter for "CERTIFIED"
	 * 3. Sort the data and store sorted data in LinkedHashMap 
	 * 
	 */
	public void getTopTen(String inFile, String outFile, String delimiter, String header, int drivingColumn){
		try{		
			Stream<String> lines = Files.lines(Paths.get(inFile));
			
			Map<String, Long> map = lines
										.map(x->x
												.trim()
												.split(delimiter)
											)
										.filter(x->x[2].equals("CERTIFIED"))
										.collect(Collectors.groupingBy(x->x[drivingColumn], Collectors.counting() ));
			
			// sort the map
			LinkedHashMap<String, Long> results = map.entrySet()
													  .stream()
		                							  .sorted(Map.Entry
		                									    .comparingByValue(Comparator.reverseOrder()))
		                							  .limit(10)
		                							  .collect(Collectors.toMap(Map.Entry::getKey, 
		                									       				Map.Entry::getValue,
		                									       				(oldValue, newValue) -> oldValue, LinkedHashMap::new));
			 	
  		    outputToFile(results, outFile, header);
		
			lines.close();
		}
		catch (IOException io){
			io.printStackTrace();
		}	
	}
	
	/*
	 * 1.Output the header followed by data to the given filename
	 */
	public void outputToFile(LinkedHashMap<String, Long> results, String outFileName, String header){
		try{	
			total = 0;
			// Total applications for the top 10
			for( long l: results.values() ){
				 total += l;
			}	
			 
			PrintWriter writer = new PrintWriter(new FileWriter(outFileName));
			writer.print(header);
		    results.forEach((k,v) -> 
		    writer.printf("\n%s;%d;%.1f%%", k, v ,( (double)v/total )*100 )); 
		    writer.close();
		} catch (IOException io){
			io.printStackTrace();
		}		
	}

}
