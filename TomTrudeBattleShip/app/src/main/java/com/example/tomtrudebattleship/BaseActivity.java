package com.example.tomtrudebattleship;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;


public class BaseActivity extends AppCompatActivity {

  final String BATTLE_SERVER_URL = "http://www.battlegameserver.com/";
  static User[] users;
  RequestQueue requestQueue;
  static String username, password;
  Gson gson;
  static UserPreferences userPrefs;
  static Attack attack;
  static int gameId;


  @Override
  public void onCreate( @Nullable Bundle savedInstanceState) {
    super.onCreate( savedInstanceState);
    setContentView( R.layout.activity_base );

    username = "tomtrude@live.com";
    password = "Turttle_67";

    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.setDateFormat( "yyyy-MM-dd'T'HH:mm:ssX" );
    gson = gsonBuilder.create();

    //Volley Library
    Cache cache = new DiskBasedCache( getCacheDir(), 1024 * 1024 );
    Network network = new BasicNetwork( new HurlStack(  ) );

    requestQueue = new RequestQueue( cache, network);
    requestQueue.start();

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate( R.menu.menu, menu );
    return true;
  }

  @Override
  public boolean onOptionsItemSelected( MenuItem item ) {
    Intent intent;

    switch( item.getItemId()){
      case R.id.menuLogout :

        String url = BATTLE_SERVER_URL + "api/v1/logout.json";

        StringRequest stringRequest = new StringRequest( Request.Method.GET, url,
            //CallBacks
            new Response.Listener<String>() {
              @Override
              public void onResponse( String response ) {
                // Doo something with the returned data
                Log.d( "INTERNET", response );

                Intent intent = new Intent( getApplicationContext(), MainActivity.class );
                startActivity( intent );
              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse( VolleyError error ) {
                //Do something with the error
                Log.d( "INTERNET", error.toString() );
                toastIt( "Internet Failure: " + error.toString() );
              }
            } ){
          // Have to add this code to each of the JSON requests
          @Override
          public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<String, String>( );
            String credentials = username + ":" + password;
            Log.d( "AUTH", "Login Info: " + credentials );
            String auth = "Basic " + Base64.encodeToString( credentials.getBytes(), Base64.NO_WRAP );
            headers.put( "Authorization", auth);
            return headers;
          }
        };
        requestQueue.add( stringRequest );
        return true;

      default :
        return super.onOptionsItemSelected( item );
    }
  }

  public void toastIt( String msg ) {
    Toast.makeText( getApplicationContext(),
        msg,Toast.LENGTH_SHORT ).show();
  }


}
