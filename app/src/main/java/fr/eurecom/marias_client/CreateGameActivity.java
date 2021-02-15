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
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateGameActivity extends AppCompatActivity implements WebSocketService.OnServiceListener{
    private static final String TAG = "CreateGameActivity";
    WebSocketService mService;
    String gameID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create);

        Button addBotButton = findViewById(R.id.add_bot_button);
        addBotButton.setOnClickListener(handler);
        Button startGameButton = findViewById(R.id.start_game_button);
        startGameButton.setOnClickListener(handler);

        Intent intent = new Intent(getApplicationContext(), WebSocketService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unbindService(connection);
    }

    View.OnClickListener handler = new View.OnClickListener() {
        public void onClick(View v) {
            if (v.getId() == R.id.add_bot_button) {
                Log.i(TAG,"add_bot_called");
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String access_token = sharedPref.getString("access token", null);
                try {
                    mService.ws_send("add_bot", gameID, access_token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (v.getId() == R.id.start_game_button) {
                Intent gameIntent = new Intent(getApplicationContext(), GameActivity.class);
                gameIntent.putExtra("action", "started");
                gameIntent.putExtra("gameID", gameID);
                startActivity(gameIntent);
            }
        }
    };

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(TAG, "onServiceConnected");
            mService = ((WebSocketService.LocalBinder) iBinder).getService();
            mService.setOnServiceListener(CreateGameActivity.this);

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String access_token = sharedPref.getString("access token", null);
            try {
                mService.ws_send("create game", null, access_token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0){
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
                            TextView player1_text = findViewById(player1);
                            player1_text.setText(joinedUsername);
                        } else if (playerIndex == 1) {
                            int player2 = getResources().getIdentifier(
                                    "@id/player2", null, getPackageName());
                            TextView player2_text = findViewById(player2);
                            player2_text.setText(joinedUsername);
                        } else if (playerIndex == 2) {
                            int player3 = getResources().getIdentifier(
                                    "@id/player3", null, getPackageName());
                            TextView player3_text = findViewById(player3);
                            player3_text.setText(joinedUsername);
                        }
                    });
                    break;
                case "CreatedGameID":
                    gameID = jObject.getString("game_id");
                    Log.i(TAG, "Game id is" + gameID);
                    handler.post(() -> {
                        TextView created_game_id = findViewById(R.id.created_game_id);
                        created_game_id.setText("Game id: " + gameID);
                    });
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}