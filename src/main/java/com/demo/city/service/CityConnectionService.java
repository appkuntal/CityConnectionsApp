package com.demo.city.service;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.xml.ws.WebServiceException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CityConnectionService {
	
	@Value("${city.file}")
	private String cityFile;
	
	List<String[]> directConnections =null;
	
	private static final Logger logger =  LoggerFactory.getLogger(CityConnectionService.class);
	
	//Reading the file containing city connections and in-memory intialization of the same
	@PostConstruct
	public void init(){
		this.directConnections = new ArrayList<>();
		try (FileInputStream fi = new FileInputStream(cityFile); DataInputStream di = new DataInputStream(fi); BufferedReader br = new BufferedReader(new InputStreamReader(di))) { 
			Pattern pattern = Pattern.compile(","); 
			String line = null; 
			while (null != (line = br.readLine())) { 
				String[] cities = pattern.split(line); 
				if(null != cities && cities.length==2){
					this.directConnections.add(cities);
				}
				else{
					logger.error(String.format("Improper format. Initialization skipped for this record: %s", line)); 
				}
			}
			logger.info("Direct city connections initialized successfully.");
		} catch (IOException ex) { 
			logger.error(String.format("Exception occurred during initialization: %s", ExceptionUtils.getStackTrace(ex))); 
			throw new WebServiceException(ex);
		}
	}
	
	//Method to check if the given 2 cities are connected
	public String isConnected(String origin, String destination){
		boolean isConnected = false;
		if(null != this.directConnections && !this.directConnections.isEmpty() && StringUtils.isNotEmpty(origin) && StringUtils.isNotEmpty(destination)){
			//Origin & destination is same
			if(origin.equals(destination)){
				isConnected = true;
			}
			//Origin & destination is different
			else{
				boolean[] isProcessed = new boolean[this.directConnections.size()];
				isConnected = doDFS(isProcessed, origin, destination);
			}
		}
		return isConnected ? "yes" : "no";		
	}
	
	//Depth First Search over the undirected graph of city connections
	private boolean doDFS(boolean[] isProcessed, String currentCity, String citytoMatch){
		boolean matchFound = false;
		
		for(int index=0; index<this.directConnections.size(); index++){
			if(!isProcessed[index]){
				if(currentCity.equals(this.directConnections.get(index)[0])){
					isProcessed[index] = true;
					matchFound = citytoMatch.equals(this.directConnections.get(index)[1]) ? true: doDFS(isProcessed, this.directConnections.get(index)[1], citytoMatch);
				}
				else if(currentCity.equals(this.directConnections.get(index)[1])){
					isProcessed[index] = true;
					matchFound = citytoMatch.equals(this.directConnections.get(index)[0]) ? true: doDFS(isProcessed, this.directConnections.get(index)[0], citytoMatch);
				}
				if(matchFound){
					break;
				}
			}
		}
		return matchFound;
	}

}
