package com.loopgamestudio.tictactoe;

public class NetworkCore {
    public static String getHost(){
        return "http://localhost/tictactoe_gameserver/";
    }
    public static String getBaseUrl(){
        return getHost()+ "main.php?";
    }

}
