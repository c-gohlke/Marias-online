package fr.eurecom.marias_client;

import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends MainActivity {
    String TAG = "LoginActivity";
    String loginUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ActivityCompat.requestPermissions(LoginActivity.this,
                new String[]{Manifest.permission.INTERNET}, 0);

        loginUrl = "http://" + getApplicationContext().getString(R.string.server) + "/login";
    }

    public void login(View v) {
        EditText usernameView = findViewById(R.id.usernameText);
        EditText passwordView = findViewById(R.id.passwordText);

        String username = usernameView.getText().toString().trim();
        String password = passwordView.getText().toString().trim();

        if (username.length() == 0 || password.length() == 0) {
            Toast.makeText(getApplicationContext(),
                    "Something is wrong. Please check your inputs.",
                    Toast.LENGTH_LONG)
                    .show();
            return;
        }

        JSONObject loginForm = new JSONObject();
        try {
            loginForm.put("subject", "login");
            loginForm.put("username", username);
            loginForm.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(loginForm.toString(),
                MediaType.parse("application/json; charset=utf-8"));
        postRequest(loginUrl, body);
    }

    public void postRequest(String postUrl, RequestBody postBody) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();
                Log.d("FAIL", e.getMessage());

                runOnUiThread(() -> {
                    TextView responseTextLogin = findViewById(R.id.responseTextLogin);
                    responseTextLogin.setText("Failed to Connect to Server. Please Try Again.");
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                runOnUiThread(() -> {
                    TextView responseTextLogin = findViewById(R.id.responseTextLogin);
                    try {
                        String loginResponseString = response.body().string().trim();
                        JSONObject JSONresponse = new JSONObject(loginResponseString);
                        String username = JSONresponse.getString("username");
                        String access_token = JSONresponse.getString("access_token");
                        Log.i(TAG, "access token: " + access_token);
                        SharedPreferences sharedPref = PreferenceManager
                                .getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("access token", access_token);
                        editor.putString("username", username);
                        editor.commit();
                        //connect with new access token
                        mService.ws_send("connect", null, access_token);
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                        responseTextLogin.setText("Something went wrong.");
                    }
               });
            }
        });
    }
}