package fr.eurecom.marias_client;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GameActivity extends AppCompatActivity implements WebSocketService.OnServiceListener {
    private static final String TAG = "GameActivity";
    WebSocketService mService;
    public String access_token;
    public String username;
    public int[] cards_in_hand = new int[3];
    public String[] players = new String[3];
    public String[] player_scores = new String[3];
    String player1;
    String player2;
    String player3;
    int dealer;
    long animation_duration = 400;

    // ads
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Log.i(TAG, "onCreate called");

        ImageView card1 = findViewById(R.id.card1);
        card1.setOnClickListener(handler);
        card1.setClickable(false);
        ImageView card2 = findViewById(R.id.card2);
        card2.setOnClickListener(handler);
        card2.setClickable(false);
        ImageView card3 = findViewById(R.id.card3);
        card3.setOnClickListener(handler);
        card3.setClickable(false);
        ImageView card4 = findViewById(R.id.card4);
        card4.setOnClickListener(handler);
        card4.setClickable(false);
        ImageView card5 = findViewById(R.id.card5);
        card5.setOnClickListener(handler);
        card5.setClickable(false);
        ImageView card6 = findViewById(R.id.card6);
        card6.setOnClickListener(handler);
        card6.setClickable(false);
        ImageView card7 = findViewById(R.id.card7);
        card7.setOnClickListener(handler);
        card7.setClickable(false);
        ImageView card8 = findViewById(R.id.card8);
        card8.setOnClickListener(handler);
        card8.setClickable(false);
        ImageView card9 = findViewById(R.id.card9);
        card9.setOnClickListener(handler);
        card9.setClickable(false);
        ImageView card10 = findViewById(R.id.card10);
        card10.setOnClickListener(handler);
        card10.setClickable(false);
        ImageView card11 = findViewById(R.id.card11);
        card11.setOnClickListener(handler);
        card11.setClickable(false);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        access_token = sharedPref.getString("access token", null);
        username = sharedPref.getString("username", null);

        Intent intent = new Intent(this, WebSocketService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        // Create the InterstitialAd and set the adUnitId (defined in values/strings.xml).
        mInterstitialAd = newInterstitialAd();
        loadInterstitial();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unbindService(connection);
        Log.i(TAG, "onDestroy called");
    }

    View.OnClickListener handler = new View.OnClickListener() {
        public void onClick(View v) {
            if (v.getId() == R.id.card1) {
                try {
                    mService.ws_send("send_play", "0", access_token);
                    resetValidCards();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (v.getId() == R.id.card2) {
                try {
                    mService.ws_send("send_play", "1", access_token);
                    resetValidCards();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (v.getId() == R.id.card3) {
                try {
                    mService.ws_send("send_play", "2", access_token);
                    resetValidCards();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (v.getId() == R.id.card4) {
                try {
                    mService.ws_send("send_play", "3", access_token);
                    resetValidCards();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (v.getId() == R.id.card5) {
                try {
                    mService.ws_send("send_play", "4", access_token);
                    resetValidCards();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (v.getId() == R.id.card6) {
                try {
                    mService.ws_send("send_play", "5", access_token);
                    resetValidCards();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (v.getId() == R.id.card7) {
                try {
                    mService.ws_send("send_play", "6", access_token);
                    resetValidCards();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (v.getId() == R.id.card8) {
                try {
                    mService.ws_send("send_play", "7", access_token);
                    resetValidCards();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (v.getId() == R.id.card9) {
                try {
                    mService.ws_send("send_play", "8", access_token);
                    resetValidCards();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (v.getId() == R.id.card10) {
                try {
                    mService.ws_send("send_play", "9", access_token);
                    resetValidCards();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (v.getId() == R.id.card11) {
                try {
                    mService.ws_send("send_play", "10", access_token);
                    resetValidCards();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void onDataReceived(String text) {
        handle_data(text);
    }

    public void handle_data(String text){
        try {
            JSONObject jObject = new JSONObject(text);
            String type = jObject.getString("type");

            Log.i(TAG, username);
            Log.i(TAG, type);
            Handler main_handler = new Handler(Looper.getMainLooper());
            switch (type) {
                case "PromptAction":
                    String RequestedAction = jObject.getString("action");
                    Log.i(TAG, RequestedAction);
                    JSONArray play_validityJSON = jObject.getJSONArray("play_validity");
                    boolean[] play_validity = new boolean[play_validityJSON.length()];
                    for (int i = 0; i < play_validityJSON.length(); i++) {
                        play_validity[i] = play_validityJSON.getBoolean(i);
                    }
                    main_handler.post(() -> {
                        Toast t = Toast.makeText(getApplicationContext(),
                                RequestedAction,
                                Toast.LENGTH_SHORT);
                        t.show();
                        updateValidCards(play_validity);
                    });
                    break;
                case "GameStart":
                    player1 = jObject.getString("player1");
                    player2 = jObject.getString("player2");
                    player3 = jObject.getString("player3");
                    //dealer value 0 means player1 is the dealer. 1 means player2 is dealer etc.
                    dealer = jObject.getInt("dealer");

                    //make user players[0], following player players[1] and last players[2]
                    if (player1.equals(username)){
                        players[0] = player1;
                        players[1] = player2;
                        players[2] = player3;

                        if(dealer == 2){
                            main_handler.post(() -> {
                                updatePlayerName(2, player2, "red");
                                updatePlayerName(3, player3, "red");
                            });
                        }
                        else if(dealer == 0){
                            main_handler.post(() -> {
                                updatePlayerName(2, player2, "red");
                                updatePlayerName(3, player3, "green");
                            });
                        }
                        else if(dealer == 1){
                            main_handler.post(() -> {
                                updatePlayerName(2, player2, "green");
                                updatePlayerName(3, player3, "red");
                            });
                        }
                        else{
                            Log.i(TAG, "dealer input invalid");
                        }
                    } else if (player2.equals(username)) {
                        players[0] = player2;
                        players[1] = player3;
                        players[2] = player1;

                        if(dealer == 2){
                            main_handler.post(() -> {
                                updatePlayerName(2, player3, "green");
                                updatePlayerName(3, player1, "red");
                            });
                        }
                        else if(dealer == 0){
                            main_handler.post(() -> {
                                updatePlayerName(2, player3, "red");
                                updatePlayerName(3, player1, "red");
                            });
                        }
                        else if(dealer == 1){
                            main_handler.post(() -> {
                                updatePlayerName(2, player3, "red");
                                updatePlayerName(3, player1, "green");
                            });
                        }
                    } else if (player3.equals(username)) {
                        players[0] = player3;
                        players[1] = player1;
                        players[2] = player2;

                        if(dealer == 0){
                            main_handler.post(() -> {
                                updatePlayerName(2, player1, "green");
                                updatePlayerName(3, player2, "red");
                            });
                        }
                        else if(dealer == 1){
                            main_handler.post(() -> {
                                updatePlayerName(2, player1, "red");
                                updatePlayerName(3, player2, "red");
                            });
                        }
                        else if(dealer == 2){
                            main_handler.post(() -> {
                                updatePlayerName(2, player1, "red");
                                updatePlayerName(3, player2, "green");
                            });
                        }
                    } else {
                        Log.i(TAG, "no matching username");
                    }

                    player_scores[0] = "0";
                    player_scores[1] = "0";
                    player_scores[2] = "0";

                    cards_in_hand[0] = 10;
                    cards_in_hand[1] = 10;
                    cards_in_hand[2] = 10;

                    main_handler.post(() -> {
                        upDatePlayerScore(player_scores);
                        setTrump(-1);
                        updateNumberofCard(2,10);
                        updateNumberofCard(3,10);
                    });
                    break;
                case "CreatedGameID":
                    int game_id = jObject.getInt("game_id");
                    Log.i(TAG, Integer.toString(game_id));
                    main_handler.post(() -> {
                        int text_created_game_id = getResources().getIdentifier(
                                "@id/created_game_id", null, getPackageName());
                        TextView text_created_game_text = findViewById(text_created_game_id);
                        text_created_game_text.setText(String.valueOf("Game id: " + game_id));
                    });
                    break;
                case "Hand":
                    Log.i(TAG, "updating hand");
                    JSONArray hand_arr = jObject.getJSONArray("hand");
                    int[] hand = new int[hand_arr.length()];
                    for (int i = 0; i < hand_arr.length(); i++) {
                        hand[i] = hand_arr.getInt(i);
                    }
                    main_handler.post(() -> updatePlayerHand(hand));
                    break;
                case "BroadcastTrumpCard":
                    Log.i(TAG, "Setting trump card");
                    setTrump(Integer.parseInt(jObject.getString("card_val")));
                    break;
                case "PlayedCard1": //same as PlayedCard2 for now
                case "PlayedCard2":
                    Log.i(TAG, "playedcard received");
                    String username_card = jObject.getString("username");
                    boolean marias_card = jObject.getBoolean("marias");

                    if (username_card.equals(players[0])) {
                        main_handler.post(() -> {
                            cards_in_hand[0] -= 1;
                            try {
                                updatePlayedCard(1, jObject.getInt("card_val"));
                                if(marias_card){
                                    addMarias(1, jObject.getInt("card_val"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    } else if (username_card.equals(players[1])) {
                        main_handler.post(() -> {
                            cards_in_hand[1] -= 1;
                            try {
                                updateNumberofCard(2, cards_in_hand[1]);
                                updatePlayedCard(2, jObject.getInt("card_val"));
                                if(marias_card){
                                    addMarias(2, jObject.getInt("card_val"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    } else if (username_card.equals(players[2])) {
                        cards_in_hand[2] -= 1;
                        main_handler.post(() -> {
                            try {
                                updateNumberofCard(3, cards_in_hand[2]);
                                updatePlayedCard(3, jObject.getInt("card_val"));
                                if(marias_card){
                                    addMarias(3, jObject.getInt("card_val"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                    break;
                case "PlayedCard3":
                    Log.i(TAG, "playedcard3 received");
                    String username_card3 = jObject.getString("username");
                    boolean marias_card3 = jObject.getBoolean("marias");

                    if (username_card3.equals(players[0])) {
                        main_handler.post(() -> {
                            cards_in_hand[0] -= 1;
                            try {
                                updatePlayedCard(1, jObject.getInt("card_val"));
                                if(marias_card3){
                                    addMarias(1, jObject.getInt("card_val"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    } else if (username_card3.equals(players[1])) {
                        main_handler.post(() -> {
                            cards_in_hand[1] -= 1;
                            try {
                                updateNumberofCard(2, cards_in_hand[1]);
                                updatePlayedCard(2, jObject.getInt("card_val"));
                                if(marias_card3){
                                    addMarias(2, jObject.getInt("card_val"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    } else if (username_card3.equals(players[2])) {
                        cards_in_hand[2] -= 1;
                        main_handler.post(() -> {
                            try {
                                updateNumberofCard(3, cards_in_hand[2]);
                                updatePlayedCard(3, jObject.getInt("card_val"));
                                if(marias_card3){
                                    addMarias(3, jObject.getInt("card_val"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                    //after 1 second, reset the cards to blank
                    main_handler.postDelayed(() -> {
                        updatePlayedCard(1, -1);
                        updatePlayedCard(2, -1);
                        updatePlayedCard(3, -1);
                    }, 1500);
                    Log.i(TAG, "playedcard finished");
                    break;
                case "EndHand":
                    Log.i(TAG,jObject.getString("points0"));
                    Log.i(TAG,jObject.getString("points1"));
                    Log.i(TAG,jObject.getString("points2"));
                    if (player1.equals(username)) {
                        player_scores[0] = jObject.getString("points0");
                        player_scores[1] = jObject.getString("points1");
                        player_scores[2] = jObject.getString("points2");
                    } else if (player2.equals(username)) {
                        player_scores[2] = jObject.getString("points0");
                        player_scores[0] = jObject.getString("points1");
                        player_scores[1] = jObject.getString("points2");
                    } else if (player3.equals(username)) {
                        player_scores[1] = jObject.getString("points0");
                        player_scores[2] = jObject.getString("points1");
                        player_scores[0] = jObject.getString("points2");
                    }
                    main_handler.post(() -> upDatePlayerScore(player_scores));
                    break;
                case "EndSet":
                    String SetWinner = jObject.getString("winner");

                    player_scores[0] = jObject.getString("points0");
                    player_scores[1] = jObject.getString("points1");
                    player_scores[2] = jObject.getString("points2");

                    main_handler.post(() -> {
                        updatePlayedCard(1, -1);
                        updatePlayedCard(2, -1);
                        updatePlayedCard(3, -1);

                        addMarias(1, -1);
                        addMarias(2, -1);
                        addMarias(3, -1);

                        upDatePlayerScore(player_scores);

                        FragmentManager fm = getSupportFragmentManager();
                        EndGameFragment fragment = EndGameFragment.newInstance(SetWinner);
                        fragment.show(fm, "end_game_fragment");
                    });
                    main_handler.postDelayed(() -> {
                        showInterstitial();
                    }, 1000);
                    break;
                case "disconnect":
                    String disconnectedUser = jObject.getString("username");
                    Log.i(TAG, disconnectedUser);
                    break;
                case "Reconnect":
                    //make user players[0], following player players[1] and last players[2]

                    player_scores[0] = jObject.getString("points0");
                    player_scores[1] = jObject.getString("points1");
                    player_scores[2] = jObject.getString("points2");

                    //TODO: not hardcoded
                    cards_in_hand[0] = 10;
                    cards_in_hand[1] = 10;
                    cards_in_hand[2] = 10;

                    JSONArray played_cards_arr = jObject.getJSONArray("hand");
                    int[] played_cards = new int[played_cards_arr.length()];

                    for (int i = 0; i < 3; i++) {
                        played_cards[i] = played_cards_arr.getInt(i);
                    }

                    int trump = jObject.getInt("trump");

                    player1 = jObject.getString("player1");
                    player2 = jObject.getString("player2");
                    player3 = jObject.getString("player3");
                    dealer = jObject.getInt("dealer");

                    //make user players[0], following player players[1] and last players[2]
                    if (player1.equals(username)){
                        players[0] = player1;
                        players[1] = player2;
                        players[2] = player3;

                        if(dealer == 2){
                            main_handler.post(() -> {
                                updatePlayerName(2, player2, "red");
                                updatePlayerName(3, player3, "red");
                            });
                        }
                        else if(dealer == 0){
                            main_handler.post(() -> {
                                updatePlayerName(2, player2, "red");
                                updatePlayerName(3, player3, "green");
                            });
                        }
                        else if(dealer == 1){
                            main_handler.post(() -> {
                                updatePlayerName(2, player2, "green");
                                updatePlayerName(3, player3, "red");
                            });
                        }
                        else{
                            Log.i(TAG, "dealer input invalid");
                        }
                    } else if (player2.equals(username)) {
                        players[0] = player2;
                        players[1] = player3;
                        players[2] = player1;

                        if(dealer == 2){
                            main_handler.post(() -> {
                                updatePlayerName(2, player3, "green");
                                updatePlayerName(3, player1, "red");
                            });
                        }
                        else if(dealer == 0){
                            main_handler.post(() -> {
                                updatePlayerName(2, player3, "red");
                                updatePlayerName(3, player1, "red");
                            });
                        }
                        else if(dealer == 1){
                            main_handler.post(() -> {
                                updatePlayerName(2, player3, "red");
                                updatePlayerName(3, player1, "green");
                            });
                        }
                    } else if (player3.equals(username)) {
                        players[0] = player3;
                        players[1] = player1;
                        players[2] = player2;

                        if(dealer == 0){
                            main_handler.post(() -> {
                                updatePlayerName(2, player1, "green");
                                updatePlayerName(3, player2, "red");
                            });
                        }
                        else if(dealer == 1){
                            main_handler.post(() -> {
                                updatePlayerName(2, player1, "red");
                                updatePlayerName(3, player2, "red");
                            });
                        }
                        else if(dealer == 2){
                            main_handler.post(() -> {
                                updatePlayerName(2, player1, "red");
                                updatePlayerName(3, player2, "green");
                            });
                        }
                    } else {
                        Log.i(TAG, "no matching username");
                    }

                    if(dealer == 0){
                        main_handler.post(() -> {
                            updatePlayedCard(1, played_cards[0]);
                            updatePlayedCard(2, played_cards[1]);
                            updatePlayedCard(3, played_cards[2]);
                        });
                    }
                    else if(dealer == 1){
                        main_handler.post(() -> {
                            updatePlayedCard(1, played_cards[2]);
                            updatePlayedCard(2, played_cards[0]);
                            updatePlayedCard(3, played_cards[1]);
                        });
                    }
                    else if(dealer == 2){
                        main_handler.post(() -> {
                            updatePlayedCard(1, played_cards[1]);
                            updatePlayedCard(2, played_cards[2]);
                            updatePlayedCard(3, played_cards[0]);
                        });
                    }
                    else{
                        Log.i(TAG, "dealer input invalid");
                    }

                    main_handler.post(() -> {
                        upDatePlayerScore(player_scores);
                        updateNumberofCard(2, cards_in_hand[1]);
                        updateNumberofCard(3, cards_in_hand[2]);
                    });

                    JSONArray player_cards_arr = jObject.getJSONArray("player_cards");
                    int[] player_cards = new int[player_cards_arr.length()];

                    for (int i = 0; i < player_cards_arr.length(); i++) {
                        player_cards[i] = player_cards_arr.getInt(i);
                    }
                    main_handler.post(() -> {
                        updatePlayerHand(player_cards);
                        setTrump(trump);
                    });
                    break;
                case "error":
                    String errorMessage = jObject.getString("error_message");
                    Log.i(TAG, errorMessage);
                default:
                    Log.i(TAG, "type not matched");
                    break;
            }
        } catch(JSONException e){
            Log.i(TAG, "jsonerror");
            e.printStackTrace();
        }
    }

    public void updatePlayerHand(int[] cardHand) {
        int[] drawable = numbersToDrawables(cardHand);
        for (int i = 1; i < 13; i++) {
            int imageId = getApplication().getBaseContext().getResources().getIdentifier(
                    "@id/card"+i, null, getApplication().getPackageName());
            ImageView image = findViewById(imageId);
            if ( i <= drawable.length) {
                image.setImageResource(drawable[i-1]);
            } else {
                image.setImageResource(R.drawable.emptycard);
            }
        }
    }

    public void upDatePlayerScore(String[] score) {
        int textId1 = getApplication().getBaseContext().getResources().getIdentifier(
                "@id/scoreplayer1", null, getApplication().getPackageName());
        TextView txt1 = findViewById(textId1);
        txt1.setText(score[0]);

        int textId2 = getApplication().getBaseContext().getResources().getIdentifier(
                "@id/scoreplayer2", null, getApplication().getPackageName());
        TextView txt2 = findViewById(textId2);
        txt2.setText(score[1]);

        int textId3 = getApplication().getBaseContext().getResources().getIdentifier(
                "@id/scoreplayer3", null, getApplication().getPackageName());
        TextView txt3 = findViewById(textId3);
        txt3.setText(score[2]);
    }

    public void updatePlayedCard(int player, int card) {
        int imageId = getApplication().getBaseContext().getResources().getIdentifier(
                "@id/played" + player, null, getApplication().getPackageName());
        ImageView image = findViewById(imageId);

        if (card == -1) {
            image.setImageResource(numberToDrawable(card));
            return;
        }

        if (player == 1) {
            image.setImageResource(numberToDrawable(card));
            return;
        }

        int[] locImage = new int[2];
        image.getLocationOnScreen(locImage);
        int TargetX = locImage[0]+43;
        int TargetY = locImage[1]-6;
        float animX;
        float animY;
        ViewGroup.LayoutParams layoutParams;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (player == 2) {
            ImageView animate = findViewById(R.id.animationP2);
            ImageView tarAnimate = findViewById(R.id.tarAnimP2);


            String theme = sharedPref.getString("theme", "classic");
            if(theme.equals("black")){
                int drawableId = getApplication().getBaseContext().getResources().getIdentifier(
                        "backcardblack", "drawable", getApplication().getPackageName());
                animate.setImageResource(drawableId);
                tarAnimate.setImageResource(drawableId);
            }
            else if(theme.equals("marias")){
                int drawableId = getApplication().getBaseContext().getResources().getIdentifier(
                        "backcardmarias", "drawable", getApplication().getPackageName());
                animate.setImageResource(drawableId);
                tarAnimate.setImageResource(drawableId);
            }

            animX = animate.getX();
            animY = animate.getY();
            layoutParams = animate.getLayoutParams();

            float scaleX = (float)image.getWidth()/(float)animate.getWidth();
            float scaleY = (float)image.getHeight()/(float)animate.getHeight();
            ObjectAnimator animatorX = ObjectAnimator.ofFloat(animate,View.X,TargetX);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(animate,View.Y,TargetY);
            ObjectAnimator animscaleX = ObjectAnimator.ofFloat(animate,View.SCALE_X,scaleX);
            ObjectAnimator animscaleY = ObjectAnimator.ofFloat(animate,View.SCALE_Y,scaleY);
            animatorX.setDuration(animation_duration);
            animatorY.setDuration(animation_duration);
            animscaleX.setDuration(animation_duration);
            animscaleY.setDuration(animation_duration);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animatorX,animatorY,animscaleX,animscaleY);
            animate.setAlpha(1f);
            animatorSet.start();

            Handler main_handler = new Handler(Looper.getMainLooper());
            main_handler.postDelayed(() -> {
                image.setImageResource(numberToDrawable(card));
                animate.setAlpha(0f);
                animate.setX(animX);
                animate.setY(animY);
                animate.setLayoutParams(layoutParams);
            }, animation_duration+50);
        }
        else if (player == 3) {
            ImageView animate = findViewById(R.id.animationP3);
            ImageView tarAnimate = findViewById(R.id.tarAnimP3);

            String theme = sharedPref.getString("theme", "classic");
            if(theme.equals("black")){
                int drawableId = getApplication().getBaseContext().getResources().getIdentifier(
                        "backcardblack", "drawable", getApplication().getPackageName());
                animate.setImageResource(drawableId);
                tarAnimate.setImageResource(drawableId);
            }
            else if(theme.equals("marias")){
                int drawableId = getApplication().getBaseContext().getResources().getIdentifier(
                        "backcardmarias", "drawable", getApplication().getPackageName());
                animate.setImageResource(drawableId);
                tarAnimate.setImageResource(drawableId);
            }

            animX = animate.getX();
            animY = animate.getY();
            layoutParams = animate.getLayoutParams();

            float scaleX = (float)image.getWidth()/(float)animate.getWidth();
            float scaleY = (float)image.getHeight()/(float)animate.getHeight();
            ObjectAnimator animatorX = ObjectAnimator.ofFloat(animate,View.X,TargetX);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(animate,View.Y,TargetY);
            ObjectAnimator animscaleX = ObjectAnimator.ofFloat(animate,View.SCALE_X,scaleX);
            ObjectAnimator animscaleY = ObjectAnimator.ofFloat(animate,View.SCALE_Y,scaleY);
            animatorX.setDuration(animation_duration);
            animatorY.setDuration(animation_duration);
            animscaleX.setDuration(animation_duration);
            animscaleY.setDuration(animation_duration);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animatorX,animatorY,animscaleX,animscaleY);
            animate.setAlpha(1f);
            animatorSet.start();

            Handler main_handler = new Handler(Looper.getMainLooper());
            main_handler.postDelayed(() -> {
                image.setImageResource(numberToDrawable(card));
                animate.setAlpha(0f);
                animate.setX(animX);
                animate.setY(animY);
                animate.setLayoutParams(layoutParams);
            }, animation_duration+50);
        }
    }

    public void addMarias(int player, int card) {
        int frontId = getApplication().getBaseContext().getResources().getIdentifier(
                "@id/mariasfrontp"+player, null, getApplication().getPackageName());
        int backId = getApplication().getBaseContext().getResources().getIdentifier(
                "@id/mariasbackp"+player, null, getApplication().getPackageName());

        ImageView imageFront = findViewById(frontId);
        ImageView imageBack = findViewById(backId);

        if(card == -1){
            //reset marias
            imageBack.setImageResource(R.drawable.emptycardsideways);
            imageFront.setImageResource(R.drawable.emptycard);
        }
        else {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String theme = sharedPref.getString("theme", "classic");
            if(theme.equals("black")){
                imageBack.setImageResource(R.drawable.backcardsidewaysblack);
            }
            else if(theme.equals("marias")){
                imageBack.setImageResource(R.drawable.backcardsidewaysmarias);
            }
            else if(theme.equals("classic")){
                imageBack.setImageResource(R.drawable.backcardsideways);
            }
            imageFront.setImageResource(numberToDrawable(card));
        }
    }

    public void setTrump(int card) {
        ImageView image = findViewById(R.id.trump);
        image.setImageResource(numberToDrawable(card));
    }

    public void updateNumberofCard(int player, int cardNumber) {
        String drawableName = "";
        int imageId = getApplication().getBaseContext().getResources().getIdentifier(
                "cardbackp"+player, "id", getApplication().getPackageName());
        ImageView image = findViewById(imageId);

        if (cardNumber == 12)
            drawableName = "twelve"+"backcard";
        else if (cardNumber == 11)
            drawableName = "eleven"+"backcard";
        else if (cardNumber == 10)
            drawableName = "ten"+"backcard";
        else if (cardNumber == 9)
            drawableName = "nine"+"backcard";
        else if (cardNumber == 8)
            drawableName = "eight"+"backcard";
        else if (cardNumber == 7)
            drawableName = "seven"+"backcard";
        else if (cardNumber == 6)
            drawableName = "six"+"backcard";
        else if (cardNumber == 5)
            drawableName = "five"+"backcard";
        else if (cardNumber == 4)
            drawableName = "four"+"backcard";
        else if (cardNumber == 3)
            drawableName = "three"+"backcard";
        else if (cardNumber == 2)
            drawableName = "two"+"backcard";
        else if (cardNumber == 1)
            drawableName = "one"+"backcard";
        else if (cardNumber == 0)
            drawableName = "empty"+"backcard";

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String theme = sharedPref.getString("theme", "classic");
        if(theme.equals("black")){
            drawableName += "black";
        }
        else if(theme.equals("marias")){
            drawableName += "marias";
        }

        int drawableId = getApplication().getBaseContext().getResources().getIdentifier(
                drawableName, "drawable", getApplication().getPackageName());

        image.setImageResource(drawableId);
    }

    private int[] numbersToDrawables(int[] cardHand) {
        int[] drawable = new int[cardHand.length];
        for (int i = 0; i < cardHand.length; i++) {
            drawable[i] = numberToDrawable(cardHand[i]);
        }
        return drawable;
    }

    private int numberToDrawable(int cardVal) {
        String drawableName = "";

        if(cardVal == -1){
            drawableName = "emptycard";
        }
        else {
            int value = cardVal % 13;
            if (value == 0)
                drawableName += "king";
            else if (value == 12)
                drawableName += "queen";
            else if (value == 11)
                drawableName += "jack";
            else if (value == 1)
                drawableName += "ace";
            else if (value == 10)
                drawableName += "ten";
            else if (value == 9)
                drawableName += "nine";
            else if (value == 8)
                drawableName += "eight";
            else if (value == 7)
                drawableName += "seven";

            int color = (cardVal - 1) / 13;
            if (color == 0)
                drawableName += "hearts";
            else if (color == 1)
                drawableName += "diamonds";
            else if (color == 2)
                drawableName += "clubs";
            else if (color == 3)
                drawableName += "spades";

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String theme = sharedPref.getString("theme", "classic");
            if(theme.equals("black")){
                drawableName += "black";
            }
            else if(theme.equals("marias")){
                drawableName += "marias";
            }
        }
        Log.d("drawable name is",drawableName);
        return getApplication().getBaseContext().getResources().getIdentifier(
                "@drawable/"+drawableName, null, getApplication().getPackageName());
    }

    public void updatePlayerName(int playerIndex, String username, String color) {
        int textId = getApplication().getBaseContext().getResources().getIdentifier(
                "@id/player"+playerIndex, null, getApplication().getPackageName());
        TextView text = findViewById(textId);
        text.setText(username);
        int colorInt;
        if(color.equals("red")){
            colorInt = 0xFFFF0000;
        }
        else{
            colorInt = 0xFF00FF00;
        }
        text.setTextColor(colorInt);
    }

    public void updateValidCards(boolean[] playValidity) {
        for(int i = 0; i < playValidity.length; i++){
            int cardId = getApplication().getBaseContext().getResources().getIdentifier(
                    "@id/card" + (i+1), null, getApplication().getPackageName());

            ImageView valid_card = findViewById(cardId);

            if(playValidity[i]) {
                valid_card.setColorFilter(Color.argb(
                        100, 0, 255, 0
                ));
                valid_card.setClickable(true);
            }
            else{
                valid_card.setColorFilter(Color.argb(
                        100, 0, 0, 0
                ));
            }
        }
    }

    public void resetValidCards() {
        for(int i =0; i<12; i++) {
            int cardId = getApplication().getBaseContext().getResources().getIdentifier(
                    "card" + (i + 1), "id", getApplication().getPackageName());
            ImageView valid_card = findViewById(cardId);
            valid_card.setColorFilter(Color.argb(
                    0, 0, 0, 0
            ));
            valid_card.setClickable(false);
        }
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(TAG, "onServiceConnected");
            mService = ((WebSocketService.LocalBinder) iBinder).getService();
            mService.setOnServiceListener(GameActivity.this);
            try {
                mService.ws_send("connect", null, access_token);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent intent = getIntent();
            String action = intent.getStringExtra("action");
            String game_id = intent.getStringExtra("gameID");

            if(action.equals("joined")) {
                try {
                    mService.ws_send("join", game_id, access_token);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                FragmentManager fm = getSupportFragmentManager();
                JoinDialogFragment joinDialogFragment = JoinDialogFragment.newInstance();
                joinDialogFragment.show(fm, "fragment_join");
            }
            else if(action.equals("started")) {
                try {
                    mService.ws_send("start_game", game_id, access_token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else if(action.equals("reconnected")) {
                try {
                    mService.ws_send("reconnect", null, access_token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0){
            Log.i(TAG, "onServiceDisconnected called");
        }
    };

    public void onLeave(View v) {
        if (v.getId() == R.id.leave_game_button) {
            try {
                mService.ws_send("leave_game", null, access_token);
                Intent menuIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(menuIntent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private InterstitialAd newInterstitialAd() {
        InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
                //load next ad
                mInterstitialAd = newInterstitialAd();
                loadInterstitial();
                //TODO: close the fragment instead of hiding it
                Log.i(TAG, "Ad was hidden");
//                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                ft.hide(endGameFragment);
//                ft.commit();
            }
        });
        return interstitialAd;
    }

    private void loadInterstitial() {
        // loads the ad.
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        mInterstitialAd.loadAd(adRequest);
    }

    public void showInterstitial(View view) {
        // Show the ad if it"s ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            Log.i(TAG, "showInterstitial successful");
            mInterstitialAd.show();
        } else {
            Log.i(TAG, "showInterstitial failed");
            mInterstitialAd = newInterstitialAd();
            loadInterstitial();
        }
    }

    public void showInterstitial() {
        // Show the ad if it"s ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            Log.i(TAG, "showInterstitial successful");
            mInterstitialAd.show();
        } else {
            Log.i(TAG, "showInterstitial failed");
            mInterstitialAd = newInterstitialAd();
            loadInterstitial();
        }
    }
}
