package fr.eurecom.marias_client;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


public class WebSocketService extends IntentService {
    private static final String TAG = "WebSocketService";

    private static final String LOCAL_SERVER = "10.0.2.2";  // Emulator IP
    private static final int LOCAL_PORT = 8000;
    String CONNECTION_LINK;

    private OnServiceListener mOnServiceListener = null;

    private WebSocket ws;
    private OkHttpClient client;
    SharedPreferences sharedPref;

    private final class WebSocketClient extends WebSocketListener {
        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            Log.i(TAG, "WebSocket connection established");
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            onDataReceived(text);
        }

        @Override
        public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket,
                              @NotNull Throwable t,
                              @Nullable Response response) {
            Log.i(TAG, "onFailure called");
            Log.e(TAG,"Error Response is: " + response);
            Log.e(TAG,"Error Throwable is: " + t.getMessage(), t);

            Intent reconnectIntent = new Intent(getApplicationContext(),
                    ReconnectActivity.class);

            reconnectIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(reconnectIntent);
        }

        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            webSocket.close(code, reason);
            Log.i(TAG, "WebSocket connection closed");
        }
    }

    public WebSocketService() {
        super("WebSocketService");
    }

    private void create_link() {
        Log.i(TAG, "connecting");
        Request request = new Request.Builder().url(
                CONNECTION_LINK).build();
        WebSocketClient listener = new WebSocketClient();
        ws = client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "service created");
        client = new OkHttpClient();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        CONNECTION_LINK = "ws://" + getApplicationContext().getString(R.string.server) + "/ws";
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.i(TAG, "service connected to an Intent");
        }
    }

    public interface OnServiceListener{
        void onDataReceived(String data);
    }

    public void setOnServiceListener(OnServiceListener serviceListener){
        mOnServiceListener = serviceListener;
    }

    public void onDataReceived(String data) {
        Log.i(TAG, "onDataReceived");
        if(mOnServiceListener != null){
            Log.i(TAG, "mOnServiceListener");
            mOnServiceListener.onDataReceived(data);
        }
    }

    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public WebSocketService getService() {
            // Return this instance of LocalService so clients can call public methods
            return WebSocketService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "service bound");
        create_link();
        return binder;
    }

    public void ws_send(String type, String data, String access_token) throws JSONException {
        JSONObject obj = new JSONObject();
        switch(type){
            case "connect":
                obj.put("type", "connect");
                break;
            case "disconnect":
                obj.put("type", "disconnect");
                obj.put("disconnected_access_token", data);
                break;
            case "create game":
                obj.put("type", "create game");
                break;
            case "prompt_game_ids":
                obj.put("type", "prompt_game_ids");
                break;
            case "join":
                obj.put("type", "join");
                obj.put("game_id", data);
                break;
            case "add_bot":
                obj.put("type", "add_bot");
                obj.put("game_id", data);
                break;
            case "start_game":
                obj.put("type", "start_game");
                obj.put("game_id", data);
                break;
            case "send_play":
                obj.put("type", "send_play");
                obj.put("play", data);
                break;
            case "reconnect":
                obj.put("type", "reconnect");
                break;
            case "leave_game":
                obj.put("type", "leave_game");
                break;
            default:
                Log.e("send error", "wrong type provided");
                break;
        }
        Log.i(TAG,"access_token is " + access_token);
        obj.put("access_token", access_token);
        String jsonText = obj.toString();
        Log.i(TAG, "sending message: " + jsonText);
        Log.i(TAG, "ws object is" + ws);
        ws.send(jsonText);
    }
}