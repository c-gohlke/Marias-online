package fr.eurecom.marias_client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PreferenceActivity extends AppCompatActivity {
    String TAG = "PreferenceActivity";
    String username;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    int coin;
    boolean gotBlack;
    boolean gotMarias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPref.edit();

        gotBlack = sharedPref.getBoolean("gotBlack", false);
        gotMarias = sharedPref.getBoolean("gotMarias", false);
        String theme = sharedPref.getString("theme", "classic");

        username = sharedPref.getString("username", null);
        coin = sharedPref.getInt("coin", 100);

        TextView tv1 = (TextView) findViewById(R.id.prefName);
        tv1.setText(username);
        TextView tv2 = (TextView) findViewById(R.id.coinValue);
        tv2.setText(": "+Integer.toString(coin));


        if(gotBlack){
            ImageView img2 = (ImageView) findViewById(R.id.deck2coin);
            TextView txt2 = (TextView) findViewById(R.id.deck2value);

            img2.setImageResource(R.drawable.validdeck);
            txt2.setText("");
        }
        if(gotMarias){
            ImageView img3 = (ImageView) findViewById(R.id.deck3coin);
            TextView txt3 = (TextView) findViewById(R.id.deck3value);

            img3.setImageResource(R.drawable.validdeck);
            txt3.setText("");
        }

        if(theme.equals("classic")){
            ImageView img1 = (ImageView) findViewById(R.id.deck1frame);
            img1.setImageResource(R.drawable.greenframe);
        }
        else if(theme.equals("black")){
            ImageView img2 = (ImageView) findViewById(R.id.deck2frame);
            img2.setImageResource(R.drawable.greenframe);
        }
        else if(theme.equals("marias")){
            ImageView img3 = (ImageView) findViewById(R.id.deck3frame);
            img3.setImageResource(R.drawable.greenframe);
        }
    }

    public void onRules(View v) {
        if (v.getId() == R.id.rules_button) {
            Intent rulesIntent = new Intent(getApplicationContext(),
                    RulesActivity.class);
            startActivity(rulesIntent);
        }
    }

    public void classicCard(View v) {
        ImageView img1 = (ImageView) findViewById(R.id.deck1frame);
        ImageView img2 = (ImageView) findViewById(R.id.deck2frame);
        ImageView img3 = (ImageView) findViewById(R.id.deck3frame);

        img1.setImageResource(R.drawable.greenframe);
        img2.setImageDrawable(null);
        img3.setImageDrawable(null);
        editor.putString("theme", "classic");
        editor.commit();
    }

    public void blackCard(View v) {
        if (!gotBlack && coin-30 >= 0) {
            coin -= 30;
            editor.putInt("coin", coin);
            TextView tv2 = (TextView) findViewById(R.id.coinValue);
            tv2.setText(": "+Integer.toString(coin));
            gotBlack = true;
            editor.putBoolean("gotBlack", gotBlack);
            editor.commit();

            ImageView img = (ImageView) findViewById(R.id.deck2coin);
            TextView txt = (TextView) findViewById(R.id.deck2value);

            img.setImageResource(R.drawable.validdeck);
            txt.setText("");
        }
        if (gotBlack) {
            ImageView img1 = (ImageView) findViewById(R.id.deck1frame);
            ImageView img2 = (ImageView) findViewById(R.id.deck2frame);
            ImageView img3 = (ImageView) findViewById(R.id.deck3frame);

            img2.setImageResource(R.drawable.greenframe);
            img1.setImageDrawable(null);
            img3.setImageDrawable(null);
            editor.putString("theme", "black");
            editor.commit();
        }
    }

    public void mariasCard(View v) {
        if (!gotMarias && coin-50 >= 0) {
            coin -= 50;
            editor.putInt("coin", coin);
            TextView tv2 = (TextView) findViewById(R.id.coinValue);
            tv2.setText(": " + Integer.toString(coin));
            gotMarias = true;
            editor.putBoolean("gotMarias", gotMarias);
            editor.commit();

            ImageView img = (ImageView) findViewById(R.id.deck3coin);
            TextView txt = (TextView) findViewById(R.id.deck3value);

            img.setImageResource(R.drawable.validdeck);
            txt.setText("");
        }
        if (gotMarias) {
            ImageView img1 = (ImageView) findViewById(R.id.deck1frame);
            ImageView img2 = (ImageView) findViewById(R.id.deck2frame);
            ImageView img3 = (ImageView) findViewById(R.id.deck3frame);

            img3.setImageResource(R.drawable.greenframe);
            img1.setImageDrawable(null);
            img2.setImageDrawable(null);
            editor.putString("theme", "marias");
            editor.commit();
        }
    }
}