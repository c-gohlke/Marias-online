package fr.eurecom.marias_client;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ReconnectActivity extends AppCompatActivity{
    String TAG = "ReconnectActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reconnect);
    }

    public void onReconnect(View v) {
        if (v.getId() == R.id.reconnect_button) {
            Intent gameIntent = new Intent(getApplicationContext(), GameActivity.class);
            gameIntent.putExtra("action", "reconnected");
            startActivity(gameIntent);
        }
    }
}