package com.catprogrammer.jira.util;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
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
    private final String timesheetUrl = Auth.getUrl() + "timesheet-gadget/1.0/raw-timesheet.json";
    private final String jqlSearchUrl = Auth.getUrl() + "api/2/search?jql=";

    private final Type worklogListType = new TypeToken<ArrayList<Worklog>>() {}.getType();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final JsonParser parser = new JsonParser();
    private final Logger logger = LoggerFactory.getLogger(JiraUtil.class);

    @Autowired
    OkHttpClient client;

    @Autowired
    Gson gson;


    public List<Worklog> findWorklogsBetween(String user, LocalDate start, LocalDate end) {
        String url = new StringBuilder().append(timesheetUrl)
                .append("?targetUser=")
                .append(user)
                .append("&startDate=")
                .append(start.format(formatter))
                .append("&endDate=")
                .append(end.format(formatter))
                .toString();
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);

        try {
            Response res = call.execute();
            if (res.isSuccessful()) {
                String body = res.body().string();
                logger.info(body);

                JsonObject jsonObject = parser.parse(body).getAsJsonObject();
                if (jsonObject.has("worklog")) {
                    JsonArray worklogJsonArray = jsonObject.getAsJsonArray("worklog");
                    List<Worklog> worklogs = gson.fromJson(worklogJsonArray, worklogListType);

                    logger.info(worklogs.toString());

                    return worklogs;
                } else {
                    logger.error("Key worklog not found in response");
                    return Collections.emptyList();
                }

            } else {
                logger.error("Request not successful");
                return Collections.emptyList();
            }
        } catch (IOException e) {
            logger.error("Call execution failed");
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public String issueKeyToId(String key) {
        String url = jqlSearchUrl + "key=" + key + "&fields=id";
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);

        try {
            Response res = call.execute();
            if (res.isSuccessful()) {
                String body = res.body().string();
                logger.info(body);
                
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
