package com.gym.gymmanagementsystem.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.gymmanagementsystem.service.ChatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class ChatServiceImpl implements ChatService {

    @Value("${deepseek.api.key}")
    private  String apiKeyFromConfig;

    @Value("${deepseek.api.url:https://api.deepseek.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${deepseek.api.model:deepseek-chat}")
    private String defaultModel;

    @Override
    public String queryChat(String content, String model) {
        String userContent = content == null ? "": content.trim();
        if(userContent.isEmpty()){
            return "我还没有收到你的问题，请重新提问";
        }

        // 处理模型
        String resolvedModel = (model == null || model.trim().isEmpty()) ? defaultModel : model.trim();

        String apikey = apiKeyFromConfig ;

        if (apikey == null || apikey.trim().isEmpty()){
            apikey = System.getenv("DEEPSEEK_API_KEY");
        }
        if (apikey == null || apikey.trim().isEmpty()){
            throw new IllegalStateException("未配置DeepSeek API KEY");
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            HashMap<String, Object> payload = new HashMap<>();
            payload.put("model", resolvedModel);
            payload.put("stream",false);

            ArrayList<Map<String, String>> messages = new ArrayList<>();
            messages.add(new HashMap<String,String>(){{
                put("role","system");
                put("content","你是一个健身房训练与饮食建议助手。回答要具体、可执行，并给出需要的注意事项。"
                        + "如果用户消息中包含“系统补充信息”里列出的会员课程，请先用简短的中文总结出“该会员报名了哪些课程”，"
                        + "然后再结合这些课程安排来回答用户的后续问题。");
            }});

            messages.add(new HashMap<String,String>(){{
                put("role","user");
                put("content",userContent);
            }});
            payload.put("messages",messages);

             String requestBody =objectMapper.writeValueAsString(payload);

            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput( true);
            connection.setRequestProperty("Authorization", "Bearer " + apikey);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            OutputStream os = connection.getOutputStream();
            os.write(requestBody.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();

            int statusCode = connection.getResponseCode() ;
            InputStream is =statusCode >= 200 && statusCode < 300 ? connection.getInputStream() : connection.getErrorStream();

            String responseBOdey = readAll( is);

            if(statusCode < 200 || statusCode >= 300){
                throw  new RuntimeException(("DeepSeek Api 请求失败,status" + statusCode + ",body = " + responseBOdey));

            }

            JsonNode root = objectMapper.readTree(responseBOdey) ;
            JsonNode choices = root.path("choices");
            if(choices.isArray()&& choices.size()>0){
                JsonNode message = choices.get(0).path("message");
                String reply = message.path("content").asText(null);
                if(reply != null && !reply.trim().isEmpty()){
                    return reply;
                }
            }

            throw new RuntimeException("DeepSeek Api 返回无法解析:choices[0].message.content 为空");
        }catch(Exception e){
            throw new RuntimeException("DeepSeek 调用失败");
        }

    }

    private String readAll(InputStream is) throws IOException {
        if(is == null) return "";
        StringBuilder sb = new StringBuilder();
        BufferedReader br =new BufferedReader(new InputStreamReader(is,StandardCharsets.UTF_8));
        String line;
        while((line = br.readLine()) != null){
            sb.append(line);
        }
        br.close();
        return sb.toString();

    }
}
