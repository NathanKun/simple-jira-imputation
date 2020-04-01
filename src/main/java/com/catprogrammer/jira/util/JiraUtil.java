package com.catprogrammer.jira.util;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Class to consume jira rest api
 * 
 * @author NathanKun
 *
 */
@Service
public class JiraUtil {
    private final String timesheetUrl = Auth.getUrl() + "timesheet-gadget/1.0/raw-timesheet.json";
    private final String jqlSearchUrl = Auth.getUrl() + "api/2/search?jql=";

    private final Type worklogListType = new TypeToken<ArrayList<Worklog>>() {}.getType();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter longDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").withZone(ZoneId.of("Z"));

    private final JsonParser parser = new JsonParser();
    private final Logger logger = LoggerFactory.getLogger(JiraUtil.class);

    @Autowired
    OkHttpClient client;

    @Autowired
    Gson gson;

    /**
     * Find worklogs betweens two dates of a user
     * @param user  user
     * @param start start date
     * @param end   end date
     * @return  list of worklogs
     */
    public List<Worklog> findWorklogsBetween(String user, LocalDate start, LocalDate end) {
        String url = new StringBuilder().append(timesheetUrl)
                .append("?targetUser=")
                .append(user)
                .append("&startDate=")
                .append(start.format(dateFormatter))
                .append("&endDate=")
                .append(end.format(dateFormatter))
                .toString();
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);

        try {
            Response res = call.execute();
            if (res.isSuccessful()) {
                String body = res.body().string();
                logger.debug(body);

                JsonObject jsonObject = parser.parse(body).getAsJsonObject();
                if (jsonObject.has("worklog")) {
                    JsonArray worklogJsonArray = jsonObject.getAsJsonArray("worklog");
                    List<Worklog> worklogs = gson.fromJson(worklogJsonArray, worklogListType);

                    logger.debug(worklogs.toString());

                    return worklogs;
                } else {
                    logger.error("Key worklog not found in response");
                    return Collections.emptyList();
                }

            } else {
                logger.error("Request not successful");
                logger.error("Status = " + res.code());
                logger.error(res.body().string());
                return Collections.emptyList();
            }
        } catch (IOException e) {
            logger.error("Call execution failed");
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Add new worklog
     * @param key           issue
     * @param date          date to add
     * @param hour          worklog timespent in hour
     * @param startHour     hour of worklog start at 
     * @param startMinute   minute of worklog start at
     * @return  is successful
     */
    public boolean addWorklog(String key, LocalDate date, double hour, int startHour, int startMinute) {
        // POST /rest/api/2/issue/{issueIdOrKey}/worklog
        String url = Auth.getUrl() + "api/2/issue/" + key + "/worklog?notifyUsers=false";
        
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JsonObject params = new JsonObject();
        params.addProperty("comment", "");
        params.addProperty("started", date.atTime(startHour, startMinute).atZone(ZoneId.systemDefault()).format(longDateTimeFormatter));
        params.addProperty("timeSpent", hour + "h");

        okhttp3.RequestBody body = RequestBody.create(JSON, params.toString());
        
        Request request = new Request.Builder().post(body).url(url).build();
        Call call = client.newCall(request);
        
        try {
            Response response = call.execute();
            
            if (response.isSuccessful()) {
                if (response.code() == 201) {
                    return true;
                } else {
                    logger.error("response code: " + response.code());
                    return false;
                }
            } else {
                logger.error("Request not successful");
                return false;
            }
        } catch (IOException e) {
            logger.error("Call execution failed");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update a worklog's timespent
     * @param key       tocket
     * @param worklogId worklog id
     * @param hour      new worklog timespent in hour
     * @return is successful
     */
    public boolean updateWorklog(String key, String worklogId, double hour) {
        // PUT /rest/api/2/issue/{issueIdOrKey}/worklog/{id}
        String url = Auth.getUrl() + "api/2/issue/" + key + "/worklog/" + worklogId + "?notifyUsers=false";
        
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JsonObject params = new JsonObject();
        params.addProperty("timeSpent", hour + "h");

        okhttp3.RequestBody body = RequestBody.create(JSON, params.toString());
        
        Request request = new Request.Builder().put(body).url(url).build();
        Call call = client.newCall(request);
        
        try {
            Response response = call.execute();
            
            if (response.isSuccessful()) {
                if (response.code() == 200) {
                    return true;
                } else {
                    logger.error("response code: " + response.code());
                    return false;
                }
            } else {
                logger.error("Request not successful");
                return false;
            }
        } catch (IOException e) {
            logger.error("Call execution failed");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete a worklog
     * @param key       issue
     * @param worklogId worklog id
     * @return  is successful
     */
    public boolean deleteWorklog(String key, String worklogId) {
        // DELETE /rest/api/2/issue/{issueIdOrKey}/worklog/{id}
        String url = Auth.getUrl() + "api/2/issue/" + key + "/worklog/" + worklogId + "?notifyUsers=false";
        
        Request request = new Request.Builder().delete().url(url).build();
        Call call = client.newCall(request);
        
        try {
            Response response = call.execute();
            
            if (response.isSuccessful()) {
                if (response.code() == 204) {
                    return true;
                } else {
                    logger.error("response code: " + response.code());
                    return false;
                }
            } else {
                logger.error("Request not successful");
                return false;
            }
        } catch (IOException e) {
            logger.error("Call execution failed");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get issue id by it's key
     * @param key
     * @return
     */
    @SuppressWarnings("unused")
    private String issueKeyToId(String key) {
        if (key.equals("FNBCPV-11415")) {
            return "389651";
        }
        if (key.equals("FNBCPV-14761")) {
            return "466836";
        }
        if (key.equals("FNBAE-401")) {
            return "352739";
        }
        
        String url = jqlSearchUrl + "key=" + key + "&fields=id";
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);

        try {
            Response res = call.execute();
            if (res.isSuccessful()) {
                String body = res.body().string();
                logger.debug(body);
                
                JsonObject jsonObject = parser.parse(body).getAsJsonObject();
                try {
                    return jsonObject.getAsJsonArray("issues").get(0).getAsJsonObject().get("id").getAsString();
                } catch (NullPointerException | IllegalStateException e) {
                    logger.error("Json parse error");
                    e.printStackTrace();
                    return "";
                }
            } else {
                logger.error("Request not successful");
                return "";
            }
        } catch (IOException e) {
            logger.error("Call execution failed");
            e.printStackTrace();
            return "";
        } 
    }
}
