package com.example.go_backend;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.ContentType;

public class JavaClient {
  public static void main(String[] args){
    String url = "http://127.0.0.1:5000/predict";
    String jsonPayload = "{\"key\":\"value\"}"; // Example JSON payload
    // String jsonPayload = "[[0,0,0].[1,1,1]]";

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost postRequest = new HttpPost(url);
            postRequest.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));

            // Execute the request
            try (CloseableHttpResponse response = client.execute(postRequest)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println("Response: " + responseBody);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


  }
}
