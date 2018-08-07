package com.catprogrammer.jira.util;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.catprogrammer.jira.constant.Auth;
import com.catprogrammer.jira.model.Worklog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Class to consume jira rest api
 * 
 * @author NathanKun
 *
 */
@Service
public class JiraUtil {
	final String timesheetUrl = Auth.getUrl() + "timesheet-gadget/1.0/raw-timesheet.json";
	final Type worklogListType = new TypeToken<ArrayList<Worklog>>(){}.getType();
	
	@Autowired
	OkHttpClient client;
	
	@Autowired
	Gson gson;
	
	private final Logger logger = LoggerFactory.getLogger(JiraUtil.class);
	
	public void test() {
		String url = timesheetUrl + "?targetUser=juhe&startDate=2018-07-01&endDate=2018-07-31";
		Request request = new Request.Builder().url(url).method("GET", null).build();
        Call call = client.newCall(request);
        
        try {
			Response res = call.execute();
			if (res.isSuccessful()) {
				String body = res.body().string();
				logger.debug(body);
				
				JsonParser parser = new JsonParser();
				JsonObject jsonObject = parser.parse(body).getAsJsonObject();
				if (jsonObject.has("worklog")) {
					JsonArray worklogJsonArray = jsonObject.getAsJsonArray("worklog");
					List<Worklog> worklogs = gson.fromJson(worklogJsonArray, worklogListType);
					
					logger.debug(worklogs.toString());
				} else {
					logger.error("Key worklog not found in response");
				}
				
			} else {
				logger.error("Request not successful");
			}
		} catch (IOException e) {
			logger.error("Call execution failed");
			e.printStackTrace();
		}
	}
}
