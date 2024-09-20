package com.cih.mobile.poc;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends AppCompatActivity {
    private static final String FLUTTER_ENGINE_ID = "flutter_engine";
    private static final String CHANNEL = "com.cih.mobile/token";
    private FlutterEngine flutterEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFlutterEngine();
        setupMethodChannel();
        setContentView(R.layout.activity_main);

        Button launchFlutterButton = findViewById(R.id.launchFlutterButton);
        launchFlutterButton.setOnClickListener(v -> launchFlutterModule());
    }

    private void setupFlutterEngine() {
        createAndConfigureFlutterEngine();
        FlutterEngineCache
                .getInstance()
                .put(FLUTTER_ENGINE_ID, flutterEngine);
    }

    private void createAndConfigureFlutterEngine() {
        flutterEngine = new FlutterEngine(this);
        flutterEngine.getDartExecutor().executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
        );
    }

    private void setupMethodChannel() {
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler((call, result) -> {
                    if (call.method.equals("getToken")) {
                        String token = getTokenFromSession();
                        result.success(token);
                    } else {
                        result.notImplemented();
                    }
                });
    }

    private void launchFlutterModule() {
        startActivity(getFlutterIntent());
    }

    private Intent getFlutterIntent() {
        return FlutterActivity
                .withCachedEngine(FLUTTER_ENGINE_ID)
                .build(this);
    }

    private String getTokenFromSession() {
        return "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJLamxBaHpjZjFmNFppQUFhdnBacUU3WVhvTDh1N1FjV3h0QzlsTTJaRF80In0.eyJleHAiOjE3MjY3Nzg1OTEsImlhdCI6MTcyNjc0MjU5MSwianRpIjoiZTYwZWIzYWYtNjhjZS00YzEwLWI3Y2EtZjYyZjM3Y2FkNTg1IiwiaXNzIjoiaHR0cHM6Ly9kZXYta2MueWFrZWV5LmlvL3JlYWxtcy9ZYWtlZXlfTG9hbiIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiJkZjhmM2E5Ni1kOTg2LTQ0ZjktYWRmNS04MzcxZTRkNTNmMzMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJwb3JjaC1zZXJ2aWNlIiwic2Vzc2lvbl9zdGF0ZSI6ImI5ZWVmNzk2LTdjMzAtNGMzNC1hYmQ2LTJhYWE4MzA2ODRiZSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly9kZXYtcG9yY2gueWFrZWV5LmlvIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwiZGVmYXVsdC1yb2xlcy1ZYWtlZXlfTG9hbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3BlbmlkIiwic2lkIjoiYjllZWY3OTYtN2MzMC00YzM0LWFiZDYtMmFhYTgzMDY4NGJlIiwicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiIsImRlZmF1bHQtcm9sZXMtWWFrZWV5X0xvYW4iXSwicHJlZmVycmVkX3VzZXJuYW1lIjoiKzIxMjY1NTEyNDk2NyIsImdpdmVuX25hbWUiOiJ0ZXN0IiwiZmFtaWx5X25hbWUiOiJ0ZXN0In0.ZlhuQt8kZeynAUoSGVrQEUouwWsexdOLSYCbpUnMOQUuYGeW4NTZpUNiKgXvpUMC9b3g9SsYqhV02-fFT3hsmM0YQhCjT52qMNzV_sB8gLMYPWi16youO2V5fngC6ZA_sdKnlyStzqb5BGsPM_2gKBdqNpczKlTnmSFPpwFZvyR9K7FvlaXe_nKH3os9D4OdyOEteH3wSv0-1tR0CZ2uBhw3sfXoO9d1scu5pT7eiqYyYvGvXno_gg0HxAYKLh7C6gFszZgWk81TmbDQwSA3FNIHSww8VTBo2OGajdq3mq0OPflklDmPvL52UKyVSAhzctlBsMuqzkOuOcjflXXGyg";
    }
}
