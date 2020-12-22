package com.example.tomtrudebattleship;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BoardSetup extends BaseActivity {

  Spinner shipsSpinner, directionsSpinner, rowsSpinner, colsSpinner;
  ArrayAdapter shipSpinnerArrayAdapter, directionsSpinnerArrayAdapter, rowsSpinnerArrayAdapter, colsSpinnerArrayAdapter;
  String[] shipsArray, directionsArray;
  static TreeMap<String, Integer> shipsMap = new TreeMap<String, Integer>();
  static TreeMap<String, Integer> directionsMap = new TreeMap<String, Integer>();
  TextView txtGameID;
  ImageView imgDefensiveGrid;
  ImageView imgAttackingGrid;
  RadioButton rdoAttack;
  RadioButton rdoDefend;
  Button btnAddShip, btnPlay;
  int addShipCount;

  public static GameCell[][] defendingGrid = new GameCell[11][11];
  public static GameCell[][] attackingGrid = new GameCell[11][11];

  @Override
  public void onCreate( Bundle savedInstanceState ) {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_board_setup );

    InitializeApp();

    shipsSpinner = findViewById( R.id.spinnerShips );
    directionsSpinner = findViewById( R.id.spinnerDirections );
    rowsSpinner = findViewById( R.id.spinnerRows );
    colsSpinner = findViewById( R.id.spinnerColumns );
    txtGameID = findViewById( R.id.txtGameId );
    imgDefensiveGrid = findViewById( R.id.imgDefensiveGrid );
    imgAttackingGrid = findViewById( R.id.imgAttackingGrid );
    rdoAttack = findViewById( R.id.rdoAttack );
    rdoDefend = findViewById( R.id.rdoDefend );

    btnAddShip = findViewById( R.id.btnAddShip );
    btnPlay = findViewById( R.id.btnPlay );

    btnAddShip.setVisibility( View.INVISIBLE );
    btnPlay.setVisibility( View.INVISIBLE );

    //imgDefensiveGrid.setVisibility( View.INVISIBLE );
    imgAttackingGrid.setVisibility( View.INVISIBLE );

    imgDefensiveGrid.setOnTouchListener( new View.OnTouchListener() {
      @Override
      public boolean onTouch( View v, MotionEvent event ) {
        if( event.getAction() == MotionEvent.ACTION_UP ) {
          Log.i( "SHIP", "onTouch: x( " + event.getX() + ") y(" + event.getY() + ")" );
          Log.i( "SHIP", "onTouch: cellx( " + Math.round(Math.floor(event.getX())) / BoardView.cellWidth + ")" +
              " celly(" + Math.round(Math.floor(event.getY())) / BoardView.cellWidth + ")" );
          Integer touchCol = (int)( Math.round( Math.floor( event.getX() / BoardView.cellWidth) ) );
          Integer touchRow = (int)( Math.round( Math.floor( event.getY() / BoardView.cellWidth) ) );
          if ( touchRow > 10 || touchCol > 10) {
          } else {
            colsSpinner.setSelection( touchCol - 1 );
            rowsSpinner.setSelection( touchRow - 1 );
          }
        }
        return true; //We have handled the event.
      }
    } );

    challengeComputer();
    GetAvailableShips();
    GetAvailableDirections();
    GetRows();
    GetCols();
  }

//  public void radioOnClick( View v ) {
//
//    if( rdoDefend.isChecked() ) {
//      imgDefensiveGrid.setVisibility( View.INVISIBLE );
//      imgAttackingGrid.setVisibility( View.VISIBLE );
//      imgAttackingGrid.invalidate();
//    } else {
//      imgDefensiveGrid.setVisibility( View.VISIBLE );
//      imgAttackingGrid.setVisibility( View.INVISIBLE );
//      imgDefensiveGrid.invalidate();
//    }
//  }

  private void InitializeApp() {
    // Initialize Game Grid
    for( int y = 0; y < 11; y++ ) {
      for( int x = 0; x < 11; x++ ) {
        defendingGrid[x][y] = new GameCell();
      }
    }

    for( int y = 0; y < 11; y++ ) {
      for( int x = 0; x < 11; x++ ) {
        attackingGrid[x][y] = new GameCell();
      }
    }
  }

  public void onClickAddShip( View v ) {

    final Object selectedShip = shipsSpinner.getSelectedItem();
    final Object selectedDirection = directionsSpinner.getSelectedItem();
    Object selectedDirectionKey = directionsMap.get( selectedDirection );
    final Object selectedRow = rowsSpinner.getSelectedItem();
    final Object selectedCol = colsSpinner.getSelectedItem();
    //Call the AddShip API
    //Build up URL with the 4 values


    // e.g. String url = BATTLE_SERVER_URL + "api/v1/game/" + gameId + "/add_ship/carrier/b/8/4.json";

    String url = BATTLE_SERVER_URL + "api/v1/game/" + gameId + "/add_ship/" +
        selectedShip + "/" + selectedRow + "/" + selectedCol + "/" + selectedDirectionKey + ".json";

    StringRequest stringRequest = new StringRequest( Request.Method.GET, url,
        //CallBacks
        new Response.Listener<String>() {
          @Override
          public void onResponse( String response ) {
            // Doo something with the returned data
            Log.d( "INTERNET", response );

            if( response.contains( "error" ) ) {
              //display error
              toastIt( "Illegal Ship Placement" );
            } else {
              int shipSize = shipsMap.get( selectedShip );

              TreeMap<String, Integer> rowsMap = new TreeMap<String, Integer>();

              rowsMap.put( "a", 1 );
              rowsMap.put( "b", 2 );
              rowsMap.put( "c", 3 );
              rowsMap.put( "d", 4 );
              rowsMap.put( "e", 5 );
              rowsMap.put( "f", 6 );
              rowsMap.put( "g", 7 );
              rowsMap.put( "h", 8 );
              rowsMap.put( "i", 9 );
              rowsMap.put( "j", 10 );

              int addRowSelected = rowsMap.get( selectedRow.toString() );
              int addColSelected = Integer.parseInt( selectedCol.toString() );

              //[col][row]

              switch( selectedDirection.toString() ) {
                case "north":
                  for( int i = addRowSelected; i > addRowSelected - shipSize; i-- ) {
                    defendingGrid[addColSelected][i].setHasShip( true );
                  }
                  break;
                case "south":
                  for( int i = addRowSelected; i < addRowSelected + shipSize; i++ ) {
                    defendingGrid[addColSelected][i].setHasShip( true );
                  }
                  break;
                case "east":
                  for( int i = addColSelected; i < addColSelected + shipSize; i++ ) {
                    defendingGrid[i][addRowSelected].setHasShip( true );
                  }
                  break;
                case "west":
                  for( int i = addColSelected; i > addColSelected - shipSize; i-- ) {
                    defendingGrid[i][addRowSelected].setHasShip( true );
                  }
                  break;
                default:
              }
              imgDefensiveGrid.invalidate();
              toastIt( (selectedShip.toString()).toUpperCase() + " Added!" );

              if (addShipCount < 5){
                shipsMap.remove( selectedShip );
                int size = shipsMap.keySet().size();
                shipsArray = new String[size];
                shipsArray = shipsMap.keySet().toArray( new String[]{} );
                shipSpinnerArrayAdapter = new ArrayAdapter<>( getApplicationContext(),
                    android.R.layout.simple_spinner_item, shipsArray );
                shipsSpinner.setAdapter( shipSpinnerArrayAdapter );
                addShipCount++;
              }

              if (addShipCount == 5){
                btnAddShip.setClickable( false );
              } else {
                btnAddShip.setClickable( true );
              }
            }

          }
        },
        new Response.ErrorListener() {
          @Override
          public void onErrorResponse( VolleyError error ) {
            //Do something with the error
            Log.d( "INTERNET", error.toString() );
            toastIt( "Internet Failure: " + error.toString() );
          }
        } ) {
      // Have to add this code to each of the JSON requests
      @Override
      public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        String credentials = username + ":" + password;
        Log.d( "AUTH", "Login Info: " + credentials );
        String auth = "Basic " + Base64.encodeToString( credentials.getBytes(), android.util.Base64.NO_WRAP );
        headers.put( "Authorization", auth );
        return headers;
      }
    };
    requestQueue.add( stringRequest );

  }

  public void playOnClick( View v ) {
    if(addShipCount == 5) {
      Intent intent = new Intent( getApplicationContext(), BoardPlay.class );
      startActivity( intent );
    } else {
      AlertDialog.Builder builder = new AlertDialog.Builder( this);
      builder
          .setMessage( "You need to add all ships to play a game." )
          .setCancelable( true )
          .setPositiveButton( "ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick( DialogInterface dialog, int which ) {

            }
          } )
          .create()
          .show();
    }
  }

  private void challengeComputer() {
    //Challenge the Computer to get the game ID

    String url = BATTLE_SERVER_URL + "api/v1/challenge_computer.json";

    JsonObjectRequest request = new JsonObjectRequest( url, null,
        //CallBacks
        new Response.Listener<JSONObject>() {
          @Override
          public void onResponse( JSONObject response ) {
            // Doo something with the returned data
            Log.d( "INTERNET", response.toString() );
            try {
              gameId = response.getInt( "game_id" );
              // Display the gameID on the view
              txtGameID.setText( "GameID: " + ( String.valueOf( gameId ) ) );
              btnPlay.setVisibility( View.VISIBLE );
              btnAddShip.setVisibility( View.VISIBLE );


            } catch( JSONException e ) {
              e.printStackTrace();
            }


          }
        },
        new Response.ErrorListener() {
          @Override
          public void onErrorResponse( VolleyError error ) {
            //Do something with the error
            Log.d( "INTERNET", error.toString() );
            toastIt( "Internet Failure: " + error.toString() );
          }
        } ) {
      // Have to add this code to each of the JSON requests
      @Override
      public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        String credentials = username + ":" + password;
        Log.d( "AUTH", "Login Info: " + credentials );
        String auth = "Basic " + Base64.encodeToString( credentials.getBytes(), android.util.Base64.NO_WRAP );
        headers.put( "Authorization", auth );
        return headers;
      }
    };
    // Fix timeout error for long running server process
    request.setRetryPolicy( new DefaultRetryPolicy(
        10000,
        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
    ) );

    requestQueue.add( request );
  }

  private void GetAvailableShips() {

    String url = BATTLE_SERVER_URL + "api/v1/available_ships.json";

    JsonObjectRequest request = new JsonObjectRequest( url, null,
        //CallBacks
        new Response.Listener<JSONObject>() {
          @Override
          public void onResponse( JSONObject response ) {
            // Doo something with the returned data
            Log.d( "INTERNET", response.toString() );

            Iterator iter = response.keys();
            while( iter.hasNext() ) {
              String key = (String) iter.next();
              try {
                Integer value = (Integer) response.get( key );
                shipsMap.put( key, value );
              } catch( JSONException e ) {
                e.printStackTrace();
              }
            }
            // shipsMap convert into an array that the spinner can use.
            int size = shipsMap.keySet().size(); //How many elements in the hash
            shipsArray = new String[size];
            shipsArray = shipsMap.keySet().toArray( new String[]{} );

            shipSpinnerArrayAdapter = new ArrayAdapter<String>( getApplicationContext(),
                android.R.layout.simple_spinner_item, shipsArray );
            shipsSpinner.setAdapter( shipSpinnerArrayAdapter );


          }
        },
        new Response.ErrorListener() {
          @Override
          public void onErrorResponse( VolleyError error ) {
            //Do something with the error
            Log.d( "INTERNET", error.toString() );
            toastIt( "Internet Failure: " + error.toString() );
          }
        } ) {
      // Have to add this code to each of the JSON requests
      @Override
      public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        String credentials = username + ":" + password;
        Log.d( "AUTH", "Login Info: " + credentials );
        String auth = "Basic " + Base64.encodeToString( credentials.getBytes(), android.util.Base64.NO_WRAP );
        headers.put( "Authorization", auth );
        return headers;
      }
    };
    requestQueue.add( request );
  }

  private void GetAvailableDirections() {

    String url = BATTLE_SERVER_URL + "api/v1/available_directions.json";

    JsonObjectRequest request = new JsonObjectRequest( url, null,
        //CallBacks
        new Response.Listener<JSONObject>() {
          @Override
          public void onResponse( JSONObject response ) {
            // Doo something with the returned data
            Log.d( "INTERNET", response.toString() );

            Iterator iter = response.keys();
            while( iter.hasNext() ) {
              String key = (String) iter.next();
              try {
                Integer value = (Integer) response.get( key );
                directionsMap.put( key, value );
              } catch( JSONException e ) {
                e.printStackTrace();
              }
            }
            // shipsMap convert into an array that the spinner can use.
            int size = directionsMap.keySet().size(); //How many elements in the hash
            directionsArray = new String[size];
            directionsArray = directionsMap.keySet().toArray( new String[]{} );

            directionsSpinnerArrayAdapter = new ArrayAdapter<String>( getApplicationContext(),
                android.R.layout.simple_spinner_item, directionsArray );
            directionsSpinner.setAdapter( directionsSpinnerArrayAdapter );


          }
        },
        new Response.ErrorListener() {
          @Override
          public void onErrorResponse( VolleyError error ) {
            //Do something with the error
            Log.d( "INTERNET", error.toString() );
            toastIt( "Internet Failure: " + error.toString() );
          }
        } ) {
      // Have to add this code to each of the JSON requests
      @Override
      public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        String credentials = username + ":" + password;
        Log.d( "AUTH", "Login Info: " + credentials );
        String auth = "Basic " + Base64.encodeToString( credentials.getBytes(), android.util.Base64.NO_WRAP );
        headers.put( "Authorization", auth );
        return headers;
      }
    };
    requestQueue.add( request );
  }

  private void GetRows() {
    List<String> rowsArray = new ArrayList<String>();
    rowsArray.add( "a" );
    rowsArray.add( "b" );
    rowsArray.add( "c" );
    rowsArray.add( "d" );
    rowsArray.add( "e" );
    rowsArray.add( "f" );
    rowsArray.add( "g" );
    rowsArray.add( "h" );
    rowsArray.add( "i" );
    rowsArray.add( "j" );

    rowsSpinnerArrayAdapter = new ArrayAdapter<String>( getApplicationContext(),
        android.R.layout.simple_spinner_item, rowsArray );
    rowsSpinner.setAdapter( rowsSpinnerArrayAdapter );
  }

  private void GetCols() {
    List<String> colsArray = new ArrayList<String>();
    colsArray.add( "1" );
    colsArray.add( "2" );
    colsArray.add( "3" );
    colsArray.add( "4" );
    colsArray.add( "5" );
    colsArray.add( "6" );
    colsArray.add( "7" );
    colsArray.add( "8" );
    colsArray.add( "9" );
    colsArray.add( "10" );

    colsSpinnerArrayAdapter = new ArrayAdapter<String>( getApplicationContext(),
        android.R.layout.simple_spinner_item, colsArray );
    colsSpinner.setAdapter( colsSpinnerArrayAdapter );
  }

}
