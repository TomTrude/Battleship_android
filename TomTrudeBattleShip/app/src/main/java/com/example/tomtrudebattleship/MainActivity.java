package com.example.tomtrudebattleship;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity {
  Button btnLogin;
  EditText edtUsername, edtPassword;
  ListView lstViewUsers;
  ArrayAdapter<User> adapter;

  public List nameList = new ArrayList(  );

  @Override
  public void onCreate( @Nullable Bundle savedInstanceState ) {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_main );
    btnLogin = findViewById( R.id.btnLogin );
    edtUsername = findViewById( R.id.edtUsername );
    edtPassword = findViewById( R.id.edtPassword );
    lstViewUsers = findViewById( R.id.lstViewUsers );

    String url = BATTLE_SERVER_URL + "api/v1/all_users.json";

    StringRequest stringRequest = new StringRequest( Request.Method.GET, url,
        //CallBacks
        new Response.Listener<String>() {
          @Override
          public void onResponse( String response ) {
            // Doo something with the returned data
            Log.d( "INTERNET", response );
            users = gson.fromJson( response, User[].class );

            for (int i=0; i < users.length; i++ ){
              if(users[i].getAvatarName() == null){
                nameList.add("user id: " + users[i].getId().toString() + " has no avatar name");
              }else {
                nameList.add( users[i].getAvatarName() );
              }
            }

            // take data to display on the view
            adapter = new ArrayAdapter<User>( getApplicationContext(), R.layout.activity_listview,
            R.id.textView4, nameList);
            lstViewUsers.setAdapter( adapter );
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
        String auth = "Basic " + Base64.encodeToString( credentials.getBytes(), android.util.Base64.NO_WRAP );
        headers.put( "Authorization", auth);
        return headers;
      }
    };
    requestQueue.add( stringRequest );

  }

  public void loginOnClick( View v ) {
    // Save the credentials into our base activity variables
    username = edtUsername.getText().toString();
    password = edtPassword.getText().toString();

    // try to login -

    String url = BATTLE_SERVER_URL + "api/v1/login.json";

    StringRequest stringRequest = new StringRequest( Request.Method.GET, url,
        //CallBacks
        new Response.Listener<String>() {
          @Override
          public void onResponse( String response ) {
            // Doo something with the returned data
            Log.d( "INTERNET", response );
            userPrefs = gson.fromJson( response,UserPreferences.class );

            Intent intent = new Intent( getApplicationContext(), GameLobby.class );
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
  }
}
