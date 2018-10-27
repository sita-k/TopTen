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

public class Top10H1OccupationsStates {
	static long total = 0;
	public static void main(String[] args) {
		String inFile  = args[0]; //"input/H1B_FY_2015.csv";
		String occupationsOutFile = args[1]; //"output/top_10_occupations.txt";
		String statesOutfile = args[2]; //"output/top_10_states.txt";
		
		// This delimiter ensures that it does not split the string when the ";" is between quotes
		String delimiter = ";(?=[^\"]*(?:(?:\"[^\"]*){2})*$)";
		
		String occupationsHeader = "TOP_OCCUPATIONS;NUMBER_CERTIFIED_APPLICATIONS;PERCENTAGE";
		String statesHeader 	 = "TOP_STATES;NUMBER_CERTIFIED_APPLICATIONS;PERCENTAGE";
		
		int occupationsDrivingCol = 23;
		int statesDrivingCol = 12;
				
		Top10H1OccupationsStates top10 = new Top10H1OccupationsStates();
		top10.getTopTen(inFile, occupationsOutFile, delimiter, occupationsHeader, occupationsDrivingCol);
		top10.getTopTen(inFile, statesOutfile, delimiter, statesHeader, statesDrivingCol);			
	}
	
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
	
	public void outputToFile(LinkedHashMap<String, Long> results, String outFileName, String header){
		try{	
			total = 0;
			// Total applications for the top 10
			for( long l: results.values() ){
				 total += l;
			}	
			 
			PrintWriter writer = new PrintWriter(new FileWriter(outFileName));
			writer.println(header);
		    results.forEach((k,v) -> 
		    writer.printf("%s;%d;%.1f%%\n", k, v ,( (double)v/total )*100 )); 
		    writer.close();
		} catch (IOException io){
			io.printStackTrace();
		}		
	}

}
