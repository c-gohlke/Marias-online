package fr.eurecom.marias_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity implements WebSocketService.OnServiceListener {
    private static final String TAG = "MainActivity";
    WebSocketService mService;
    String access_token;
    String username;
    String mockUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate called");

        setContentView(R.layout.game_menu);

        //find authentication details
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // if access token or username keys not found in preferences, use mockUsername
        access_token = sharedPref.getString("access token", null);
        username = sharedPref.getString("username", null);

        if(access_token == null || username == null){
            //if couldn't load access_token or username from SharedPref
            mockUsername = "Guest" + (int) (Math.random() * 10000);
            access_token = mockUsername;
            username = mockUsername;
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("access_token", access_token);
            editor.putString("username", username);
            editor.commit();
        }

        Intent intent = new Intent(this, WebSocketService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        Log.i(TAG, "service bound");

        TextView tv1 = findViewById(R.id.display_username);
        tv1.setText(username);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.i(TAG, "onDestroy called");
        unbindService(connection);
    }

    public void onLogin(View v) {
        if (v.getId() == R.id.login_button) {
            Intent loginIntent = new Intent(getApplicationContext(),
                    LoginActivity.class);
            startActivity(loginIntent);
        }
    }

    public void onRegister(View v) {
        if (v.getId() == R.id.register_button) {
            Intent registerIntent = new Intent(getApplicationContext(),
                    RegisterActivity.class);
            startActivity(registerIntent);
        }
    }

    public void onLogout(View v) {
        if (v.getId() == R.id.logout_button) {
            try {
                logout();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void onCreateGame(View v) {
        if (v.getId() == R.id.create_game_button) {
            Intent createGameIntent = new Intent(getApplicationContext(),
                    CreateGameActivity.class);
            startActivity(createGameIntent);
        }
    }

    public void onPreferences(View v) {
        if (v.getId() == R.id.preferences_button) {
            Intent preferenceIntent = new Intent(getApplicationContext(),
                    PreferenceActivity.class);
            startActivity(preferenceIntent);
        }
    }

    public void onJoin(View v) {
        if (v.getId() == R.id.join_game_button) {
            Intent joinIntent = new Intent(getApplicationContext(),
                    JoinGameActivity.class);
            startActivity(joinIntent);
        }
    }

    public void onReconnectView(View v) {
        if (v.getId() == R.id.reconnect_view_button) {
            Intent reconnectIntent = new Intent(getApplicationContext(),
                    ReconnectActivity.class);
            startActivity(reconnectIntent);
        }
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(TAG, "onServiceConnected");
            mService = ((WebSocketService.LocalBinder) iBinder).getService();
            mService.setOnServiceListener(MainActivity.this);
            try {
                Log.i(TAG, "onServiceConnected, connecting to ws");
                mService.ws_send("connect", null, access_token);
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
    }

    public void logout() throws JSONException {
        mockUsername = "Guest" + (int) (Math.random() * 10000);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        access_token = mockUsername;
        username = mockUsername;
        editor.putString("access token", access_token);
        editor.putString("username", username);

        editor.commit();

        //connect with new new access token
        mService.ws_send("connect", null, access_token);
        TextView tv1 = findViewById(R.id.display_username);
        tv1.setText(username);
    }
}