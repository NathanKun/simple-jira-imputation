package com.catprogrammer.jira.config;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

/**
 * Configuration class for Gson
 * @author NathanKun
 *
 */
@Configuration
public class GsonAutoConfiguration {
	
	@Bean
	public Gson gson() {
		GsonBuilder builder = new GsonBuilder(); 

		// Register an adapter to manage the date types as long values 
		builder.registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() { 
		   public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
		      return LocalDateTime.ofInstant(
		    		  Instant.ofEpochMilli(json.getAsJsonPrimitive().getAsLong()), 
		    		  TimeZone.getDefault().toZoneId()); 
		   } 
		});

		return builder.create();
	}

}
