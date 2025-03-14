package com.example.go_backend;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.entity.ContentType;

public class JavaClient {
  private static final String url = "http://127.0.0.1:5000/predict";

  // public static String sendPostRequest(String jsonPayload) {
  //   try (CloseableHttpClient client = HttpClients.createDefault()) {
  //       HttpPost postRequest = new HttpPost(url);
  //       postRequest.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));

  //       try (CloseableHttpResponse response = client.execute(postRequest)) {
  //           return EntityUtils.toString(response.getEntity());
  //       }
  //   } catch (Exception e) {
  //       e.printStackTrace();
  //       return "Error: " + e.getMessage();
  //   }
  // }


   public static String sendPostRequest(String jsonPayload) throws Exception {
      try (CloseableHttpClient client = HttpClients.createDefault()) {
          HttpPost post = new HttpPost(url);
          post.setHeader("Content-Type", "application/json");
          post.setEntity(new StringEntity(jsonPayload, StandardCharsets.UTF_8));

          try (CloseableHttpResponse response = client.execute(post);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
              
              // Read response as a String
              StringBuilder responseString = new StringBuilder();
              String line;
              while ((line = reader.readLine()) != null) {
                  responseString.append(line);
              }

              // Parse JSON response to extract 'move'
              ObjectMapper objectMapper = new ObjectMapper();
              JsonNode jsonResponse = objectMapper.readTree(responseString.toString());
              String ai_move = jsonResponse.get("move").toString();
              // return jsonResponse.get("move").toString();
              return ai_move;
          }
      }
  }
}

