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
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL = "com.cih.credit";
    private static final String API_ENDPOINT = "http://localhost:3000/tokens";
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

                HashMap<String, String> tokens = getTokenFromApi();
                runOnUiThread(() -> result.success(tokens));
            } catch (IOException e) {
                runOnUiThread(() -> result.error("NETWORK_ERROR", e.getMessage(), null));
            }
        });
    }

    // Make API call to get token
    private
    HashMap<String, String> getTokenFromApi() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(API_ENDPOINT).openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
                Thread.sleep(1000);
            try {
                JSONObject jsonResponse = new JSONObject(response.toString());
                Log.d("TOKEN_FETCH", "getTokenFromApi: "+ jsonResponse);


                String accessToken = jsonResponse.getString("accessToken");
                String refreshToken = jsonResponse.getString("refreshToken");

                HashMap<String, String> tokens = new HashMap<>();
                tokens.put("accessToken", accessToken);
//                tokens.put("refreshToken", refreshToken);
                return tokens;

            } catch (JSONException e) {
                Log.e("TOKEN_FETCH", "Error parsing JSON response", e);
                return null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            connection.disconnect();
        }
    }
}
