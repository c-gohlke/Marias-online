package fr.eurecom.marias_client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JoinGameActivity extends AppCompatActivity implements WebSocketService.OnServiceListener, AdapterView.OnItemSelectedListener{
    private static final String TAG = "JoinGameActivity";
    WebSocketService mService;
    String join_game_id;
    String[] gamesIDs;
    String[] player1s;
    String[] player2s;
    String[] player3s;
    Spinner dropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join);
        Button joinGameButton = findViewById(R.id.join_game_button);
        joinGameButton.setOnClickListener(handler);

        join_game_id = "0"; // set default
        dropdown = findViewById(R.id.spinner_gameID);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        Log.i(TAG, "selected item #" + position);
        join_game_id = gamesIDs[position];

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            TextView text_player1 = findViewById(R.id.player1);
            String username1 = player1s[position];
            text_player1.setText(username1);

            TextView text_player2 = findViewById(R.id.player2);
            String username2 = player2s[position];
            text_player2.setText(username2);

            TextView text_player3 = findViewById(R.id.player3);
            String username3 = player3s[position];
            text_player3.setText(username3);
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String access_token = sharedPref.getString("access token", null);
        Intent intent = new Intent(getApplicationContext(), WebSocketService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(connection);
    }

    View.OnClickListener handler = new View.OnClickListener() {
        public void onClick(View v) {
            if (v.getId() == R.id.join_game_button) {
                Intent gameIntent = new Intent(getApplicationContext(),
                        GameActivity.class);
                gameIntent.putExtra("action", "joined");
                gameIntent.putExtra("gameID", join_game_id);
                startActivity(gameIntent);
            }
        }
    };

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(TAG, "onServiceConnected");
            mService = ((WebSocketService.LocalBinder) iBinder).getService();
            mService.setOnServiceListener(JoinGameActivity.this);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String access_token = sharedPref.getString("access token", null);
            try {
                mService.ws_send("prompt_game_ids", null, access_token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }

    };

    @Override
    public void onDataReceived(String text) {
        Log.i(TAG, "received:" + text);
        try {
            JSONObject jObject = new JSONObject(text);
            String type = jObject.getString("type");
            Handler handler = new Handler(Looper.getMainLooper());

            switch (type) {
                case "PlayerJoined":
                    String joinedUsername = jObject.getString("username");
                    int playerIndex = jObject.getInt("playerIndex");

                    handler.post(() -> {
                        if (playerIndex == 0) {
                            int player1 = getResources().getIdentifier(
                                    "@id/player1", null, getPackageName());
                            TextView text_player1 = findViewById(player1);
                            text_player1.setText(joinedUsername);
                        } else if (playerIndex == 1) {
                            int player2 = getResources().getIdentifier(
                                    "@id/player2", null, getPackageName());
                            TextView text_player2 = findViewById(player2);
                            text_player2.setText(joinedUsername);
                        } else if (playerIndex == 2) {
                            int player3 = getResources().getIdentifier(
                                    "@id/player3", null, getPackageName());
                            TextView text_player3 = findViewById(player3);
                            text_player3.setText(joinedUsername);
                        }
                    });
                    break;
                case "game_ids":
                    JSONArray games_arrJSON = jObject.getJSONArray("games");
                    gamesIDs = new String[games_arrJSON.length()];
                    player1s = new String[games_arrJSON.length()];
                    player2s = new String[games_arrJSON.length()];
                    player3s = new String[games_arrJSON.length()];

                    for (int i = 0; i < games_arrJSON.length(); i++) {
                        gamesIDs[i] = games_arrJSON.getJSONObject(i).getString("gameID");
                        player1s[i] = games_arrJSON.getJSONObject(i).getString("player1");
                        player2s[i] = games_arrJSON.getJSONObject(i).getString("player2");
                        player3s[i] = games_arrJSON.getJSONObject(i).getString("player3");
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_dropdown_item,
                            gamesIDs);

                    handler.post(() -> dropdown.setAdapter(adapter));
                    dropdown.setOnItemSelectedListener(this);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}