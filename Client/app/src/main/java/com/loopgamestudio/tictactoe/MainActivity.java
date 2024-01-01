package com.loopgamestudio.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;



import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Predicate;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arr= new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        ListView list = findViewById(R.id.listView);
        list.setAdapter(arr);

        getGames();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String gameId = (String) arr.getItem(position);
                AsyncHttpClient client = new AsyncHttpClient();
                client.get(NetworkCore.getBaseUrl() + "type=join_room&id=" + gameId, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            int move = Integer.valueOf(new String(responseBody));

                            PlayerInfo info = new PlayerInfo();
                            info.SetMoveType(move);

                            Intent intent = new Intent(MainActivity.this, GameActivity.class);
                            intent.putExtra("move", move);
                            intent.putExtra("id", gameId);

                            startActivity(intent);
                        }
                        catch (Exception ex){

                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
            }
        });
    }
    ArrayAdapter<String> arr;
    ListView list;
    public void getGames(){

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(NetworkCore.getBaseUrl() + "type=get_games", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String[] ids = new String(responseBody).split("`");
                    arr.clear();
                    for (int i = ids.length - 1; i >= 0; i--) {
                        if (ids[i].equals(currentId) == false) {
                            arr.add(ids[i]);
                        }
                    }
                }
                catch (Exception ex){

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                getGames();
            }
        }, 3000);
    }
    String currentId = "";
    public void waitStartGame(String id){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(NetworkCore.getBaseUrl() + "type=wait_game&id="+id, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    int move = Integer.valueOf(new String(responseBody));
                    PlayerInfo info = new PlayerInfo();
                    info.SetMoveType(move);

                    Intent intent = new Intent(MainActivity.this, GameActivity.class);
                    intent.putExtra("move", move);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
                catch(Exception ex)
                {
                    waitStartGame(id);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                waitStartGame(id);

            }
        });
    }



    public void createRoom(View v){

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(NetworkCore.getBaseUrl() + "type=create_room", new AsyncHttpResponseHandler() {
           @Override
           public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
               try {
                   String res = new String(responseBody);

                   currentId = res;
                   TextView text = (TextView) findViewById(R.id.textView2);
                   text.setText(res);
                   text.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           TextView t = (TextView) v;
                           ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                           ClipData clip = ClipData.newPlainText("RoomId", t.getText());
                           clipboard.setPrimaryClip(clip);
                           Toast.makeText(MainActivity.this, "Скопировано!", Toast.LENGTH_SHORT).show();
                       }
                   });

                   waitStartGame(res);
               }
               catch (Exception ex){

               }
           }

           @Override
           public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

           }
       });


    }
}