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
  // private static final String url = "http://127.0.0.1:5000/predict";
  // private static final String url = "http://127.0.0.1:5000/predict";

  public static String sendPostRequest(String jsonPayload, String url) throws Exception {
    try (CloseableHttpClient client = HttpClients.createDefault()) {
      HttpPost post = new HttpPost(url + "/predict");
      // System.out.println(post);
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

        // System.out.println(responseString);
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
