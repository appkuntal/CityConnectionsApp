package com.demo.city.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.city.service.CityConnectionService;

@RestController
public class CityConnectionController {
	
	@Autowired
	CityConnectionService cityConnectionService; 
	
	@RequestMapping(value = "/connected", method = RequestMethod.GET)
	public String isConnected(@RequestParam(value="origin") String originCity, @RequestParam(value="destination")String destinationCity){
		 return cityConnectionService.isConnected(originCity, destinationCity);
	}
}
