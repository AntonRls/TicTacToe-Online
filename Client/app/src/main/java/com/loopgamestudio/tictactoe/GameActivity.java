package com.loopgamestudio.tictactoe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class GameActivity extends AppCompatActivity {

    int moveType = -1;
    Button[] buttons;
    String gameId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2);
        Bundle extras = getIntent().getExtras();

        moveType = (int) extras.get("move");
        gameId = (String) extras.get("id");

        TextView text = findViewById(R.id.textView4);
        if(moveType == 1){
            text.setText("Вы играете за: X");
        }else{
            text.setText("Вы играете за: O");
        }

        buttons = new Button[9];
        buttons[0] = findViewById(R.id.button2);
        buttons[1] = findViewById(R.id.button11);
        buttons[2] = findViewById(R.id.button12);

        buttons[3] = findViewById(R.id.button13);
        buttons[4] = findViewById(R.id.button14);
        buttons[5] = findViewById(R.id.button15);

        buttons[6] = findViewById(R.id.button16);
        buttons[7] = findViewById(R.id.button17);
        buttons[8] = findViewById(R.id.button18);

        for(int i = 0; i < 9; i++){
            buttons[i].setText("");
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button bt = (Button) v;
                    if (bt.getText().equals("")) {
                        bt.setText(getChar());
                        int y = 0;
                        int x = 0;

                        int count = 0;
                        for (int i = 0; i < 9; i++) {
                            if (bt == buttons[i]) {
                                break;
                            }
                            x++;
                            count++;
                            if (count == 3) {
                                y++;
                                count = 0;
                                x = 0;
                            }

                        }
                        offButtons();
                        AsyncHttpClient client = new AsyncHttpClient();
                        client.get(NetworkCore.getBaseUrl() + "type=create_action&id=" + gameId + "&x=" + String.valueOf(x) + "&y=" + String.valueOf(y) + "&type_move=" + String.valueOf(moveType), new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                waitHod();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                            }
                        });
                    }
                    checkWin();
                }
            });
        }
        offButtons();
        if(getChar() == "X"){
            waitHod();
        }else{
            onButtons();
        }
    }
    //0 1 2
    //3 4 5
    //6 7 8
    private void checkWin(){
        if(buttons[0].getText() == buttons[1].getText() && buttons[1].getText() == buttons[2].getText() && buttons[0].getText().equals("") == false){
            showDialog("Победили: " + buttons[0].getText());
        }
        if(buttons[3].getText() == buttons[4].getText() && buttons[4].getText() == buttons[5].getText() && buttons[5].getText().equals("") == false){
            showDialog("Победили: " + buttons[3].getText());
        }
        if(buttons[6].getText() == buttons[7].getText() && buttons[7].getText() == buttons[8].getText() && buttons[8].getText().equals("") == false){
            showDialog("Победили: " + buttons[6].getText());
        }

        if(buttons[0].getText() == buttons[3].getText() && buttons[3].getText() == buttons[6].getText() && buttons[6].getText().equals("") == false){
            showDialog("Победили: " + buttons[0].getText());
        }
        if(buttons[1].getText() == buttons[4].getText() && buttons[7].getText() == buttons[4].getText() && buttons[4].getText().equals("") == false){
            showDialog("Победили: " + buttons[1].getText());
        }
        if(buttons[2].getText() == buttons[5].getText() && buttons[5].getText() == buttons[8].getText() && buttons[8].getText().equals("") == false){
            showDialog("Победили: " + buttons[2].getText());
        }

        if(buttons[0].getText() == buttons[4].getText() && buttons[4].getText() == buttons[8].getText() && buttons[8].getText().equals("") == false){
            showDialog("Победили: " + buttons[0].getText());
        }

        if(buttons[2].getText() == buttons[4].getText() && buttons[4].getText() == buttons[6].getText() && buttons[6].getText().equals("") == false){
            showDialog("Победили: " + buttons[2].getText());
        }

        int count = 0;
        for(int i = 0; i < 9; i++){
            if(buttons[i].getText().equals("") == false){
                count++;
            }
        }
        if(count == 9){
            showDialog("Ничья!");
        }
    }
    private void showDialog(String text){
        new AlertDialog.Builder(GameActivity.this)
                .setTitle("Игра окончена!")
                .setMessage(text)

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }
    private String getEnemyChar(){
        if(moveType == 0){
            return "X";
        }
        return "O";
    }
    private void waitHod(){
        checkWin();
        offButtons();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(NetworkCore.getBaseUrl() + "type=long_get&id="+gameId, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String cords = new String(responseBody);
                    if (cords.length() != 3) {
                        waitHod();
                        return;
                    }

                    String[] finalCord = cords.split(";");
                    int x = Integer.valueOf(finalCord[0]);
                    int y = Integer.valueOf(finalCord[1]);
                    buttons[x + y*3].setText(getEnemyChar());
                    checkWin();
                    onButtons();
                }
                catch (Exception ex){
                    waitHod();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                waitHod();
            }
        });
    }
    public String getChar(){
        if(moveType == 1){
            return  "X";
        }
        return "O";
    }
    public void offButtons(){
        for(int i = 0; i < 9; i++){
            buttons[i].setEnabled(false);
        }
    }
    public void onButtons(){
        for(int i = 0; i < 9; i++){
            if(buttons[i].getText().equals("")) {
                buttons[i].setEnabled(true);
            }
        }
    }
}