package com.cih.mobile.poc;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.MethodChannel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL = "com.cih.credit";
    private static final String API_ENDPOINT = "https://retoolapi.dev/Fg700C/get-api/1";
    private FlutterEngine flutterEngine;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupFlutterEngine();
        setupMethodChannel();
        setupViews();
    }

    // Initialize and cache Flutter engine
    private void setupFlutterEngine() {
        flutterEngine = new FlutterEngine(this);
        flutterEngine.getDartExecutor().executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault());
        FlutterEngineCache.getInstance().put("flutter_engine", flutterEngine);
    }

    // Set up the method channel for communication with Flutter
    private void setupMethodChannel() {
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler((call, result) -> {
                    if (call.method.equals("getToken")) {
                        fetchToken(result);
                    } else {
                        result.notImplemented();
                    }
                });
    }

    // Set up the button to launch the Flutter module
    private void setupViews() {
        Button launchFlutterButton = findViewById(R.id.launchFlutterButton);
        launchFlutterButton.setOnClickListener(v -> launchFlutterModule());
    }

    // Launch the Flutter module
    private void launchFlutterModule() {
        startActivity(FlutterActivity.withCachedEngine("flutter_engine").build(this));
    }

    // Fetch token from API when invoked from Flutter
    private void fetchToken(MethodChannel.Result result) {
        executor.execute(() -> {
            try {
                String token = getTokenFromApi();
                runOnUiThread(() -> result.success(token));
            } catch (IOException e) {
                runOnUiThread(() -> result.error("NETWORK_ERROR", e.getMessage(), null));
            }
        });
    }

    // Make API call to get token
    private String getTokenFromApi() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(API_ENDPOINT).openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
                Thread.sleep(2000);
            try {
                JSONObject jsonResponse = new JSONObject(response.toString());
                Log.d("TOKEN_FETCH", "getTokenFromApi: "+ jsonResponse);
                return jsonResponse.getString("accessToken");
            } catch (JSONException e) {
                Log.e("TOKEN_FETCH", "Error parsing JSON response", e);
                return null;
            }
//            return "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJLamxBaHpjZjFmNFppQUFhdnBacUU3WVhvTDh1N1FjV3h0QzlsTTJaRF80In0.eyJleHAiOjE3Mjg0MzA0NzAsImlhdCI6MTcyODM5NDQ3MCwianRpIjoiYWVhZDBlZTMtNjY0MS00NDgxLTg1ZTItNjUwMzYxZWQyOWNiIiwiaXNzIjoiaHR0cHM6Ly9kZXYta2MueWFrZWV5LmlvL3JlYWxtcy9ZYWtlZXlfTG9hbiIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiJiMTYyN2U1MC02YzAxLTQwNzYtODlmZi03ZjFmNzU5MTQzYWEiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJwb3JjaC1zZXJ2aWNlIiwic2Vzc2lvbl9zdGF0ZSI6IjM3NTM2MTc4LWQzMjMtNDYwYS1hZjdkLTZkYjU3MzI4NWNhNSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly9kZXYtcG9yY2gueWFrZWV5LmlvIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwiZGVmYXVsdC1yb2xlcy1ZYWtlZXlfTG9hbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3BlbmlkIiwic2lkIjoiMzc1MzYxNzgtZDMyMy00NjBhLWFmN2QtNmRiNTczMjg1Y2E1Iiwicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiIsImRlZmF1bHQtcm9sZXMtWWFrZWV5X0xvYW4iXSwicHJlZmVycmVkX3VzZXJuYW1lIjoiKzIxMjY1NTEyNDk2NyIsImdpdmVuX25hbWUiOiJldHMiLCJmYW1pbHlfbmFtZSI6InRlc3QifQ.qWVZu0C6EDPuwqIU6WTe4zv7AyW-5tTjvfAYZJrp6Dhbp3CfcQ9pP3oLksD6DCyEOlsC3kU40AOzTl-ngpf_vbcF7CMo0kT0QqIgxdCU4yxWU9UUDpONr_usfz6TXe7Du_aMHFOCVzpHYUi2Oa87Va3hnba_7nvSvAMbO4o7tfin49qfqjtX1NlO6V0xTOA_FwMmaVToRDqwCgs5TYDaOc_LlBlTEOrp06sqAaZLFrIte1Ra-bcoOk4ODZSXFzOersUJiouSSy78B3pW7TUSX84LyC2QR6AI5y7BUG68yJCMbAhsa3kZnVxNPKschADUvzMg_ypqQlHyfCqZl_4dGg";
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            connection.disconnect();
        }
    }
}
