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
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BoardPlay extends BaseActivity {

  Spinner playRowsSpinner, playColsSpinner;
  ArrayAdapter rowsSpinnerArrayAdapter, colsSpinnerArrayAdapter;
  //String[] shipsArray, directionsArray;
  //static TreeMap<String, Integer> shipsMap = new TreeMap<String, Integer>();
  //static TreeMap<String, Integer> directionsMap = new TreeMap<String, Integer>();
  TextView txtGameID;
  ImageView imgDefensiveGrid;
  ImageView imgAttackingGrid;
  RadioButton rdoAttack;
  RadioButton rdoDefend;
  Button btnAttack;

  public static GameCell[][] defendingGrid = new GameCell[11][11];
  public static GameCell[][] attackingGrid = new GameCell[11][11];

  @Override
  public void onCreate( Bundle savedInstanceState ) {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_board_play );

    InitializeApp();

    //shipsSpinner = findViewById( R.id.spinnerShips );
    //directionsSpinner = findViewById( R.id.spinnerDirections );
    playRowsSpinner = findViewById( R.id.playSpinnerRows );
    playColsSpinner = findViewById( R.id.playSpinnerCols );

    txtGameID = findViewById( R.id.txtGameId );
    txtGameID.setText( "GameID: " + ( String.valueOf( BoardSetup.gameId ) ) );

    imgDefensiveGrid = findViewById( R.id.imgDefensiveGrid );
    imgAttackingGrid = findViewById( R.id.imgAttackingGrid );
    rdoAttack = findViewById( R.id.rdoAttack );
    rdoDefend = findViewById( R.id.rdoDefend );

    imgDefensiveGrid.setVisibility( View.VISIBLE );
    imgAttackingGrid.setVisibility( View.INVISIBLE );

    btnAttack = findViewById( R.id.btnAttack );
    btnAttack.setVisibility( View.INVISIBLE );

    imgAttackingGrid.setOnTouchListener( new View.OnTouchListener() {
      @Override
      public boolean onTouch( View v, MotionEvent event ) {
        if( event.getAction() == MotionEvent.ACTION_UP ) {
          Log.i( "SHIP", "onTouch: x( " + event.getX() + ") y(" + event.getY() + ")" );
          Log.i( "SHIP", "onTouch: cellx( " + event.getX() / BoardView.cellWidth + ")" +
              " celly(" + event.getY() / BoardView.cellWidth + ")" );
          Integer touchCol = (int)( Math.round( Math.floor( event.getX() / BoardView.cellWidth) ) );
          Integer touchRow = (int)( Math.round( Math.floor( event.getY() / BoardView.cellWidth) ) );
          if ( touchRow > 10 || touchCol > 10) {
          } else {
            playColsSpinner.setSelection( touchCol - 1 );
            playRowsSpinner.setSelection( touchRow - 1 );
          }
        }
        return true; //We have handled the event.
      }
    } );

    GetRows();
    GetCols();
  }

  public void radioOnClick( View v ) {

    if( rdoDefend.isChecked() ) {
      imgDefensiveGrid.setVisibility( View.VISIBLE );
      imgAttackingGrid.setVisibility( View.INVISIBLE );
      btnAttack.setVisibility( View.INVISIBLE );

      imgAttackingGrid.invalidate();
    } else {
      imgDefensiveGrid.setVisibility( View.INVISIBLE );
      imgAttackingGrid.setVisibility( View.VISIBLE );
      btnAttack.setVisibility( View.VISIBLE );

      imgDefensiveGrid.invalidate();
    }
  }

  public void onClickAttack( View v ) {

    final TreeMap<String, Integer> rowsMap = new TreeMap<String, Integer>();

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


    final Object selectedRow = playRowsSpinner.getSelectedItem();
    final Object selectedCol = playColsSpinner.getSelectedItem();
    //Call the AddShip API
    //Build up URL with the 4 values
    //set waiting
    int attackRowSelected = rowsMap.get( selectedRow.toString() );
    int attackColSelected = Integer.parseInt( selectedCol.toString() );
    
    if( attackingGrid[attackColSelected][attackRowSelected].getHit().equals( true ) ||
        attackingGrid[attackColSelected][attackRowSelected].getMiss().equals( true ) ) {
      toastIt( "You have already attacked here." );
    } else {
      attackingGrid[attackColSelected][attackRowSelected].setWaiting( true );
      imgAttackingGrid.invalidate();

      // e.g. String url = BATTLE_SERVER_URL + "api/v1/game/" + gameId + "/add_ship/carrier/b/8/4.json";

      String url = BATTLE_SERVER_URL + "api/v1/game/" + gameId + "/attack/" + selectedRow + "/" + selectedCol + ".json";

      StringRequest stringRequest = new StringRequest( Request.Method.GET, url,
          //CallBacks
          new Response.Listener<String>() {
            @Override
            public void onResponse( String response ) {
              // Doo something with the returned data
              Log.d( "INTERNET", response );
              attack = gson.fromJson( response, Attack.class );
              Log.d( "HIT", "hit was " + attack.getHit() );
              Log.d( "HIT", "Comp hit was " + attack.getCompHit() );

              //check user hit
              if( attack.getHit() ) {
                Log.d( "HIT", "you Hit!" );

                int attackRowSelected = rowsMap.get( selectedRow.toString() );
                int attackColSelected = Integer.parseInt( selectedCol.toString() );

                attackingGrid[attackColSelected][attackRowSelected].setHit( true );
              } else {

                int attackRowSelected = rowsMap.get( selectedRow.toString() );
                int attackColSelected = Integer.parseInt( selectedCol.toString() );

                attackingGrid[attackColSelected][attackRowSelected].setMiss( true );
              }

              //check comp hit
              if( attack.getCompHit() ) {
                Log.d( "HIT", "Comp Hit!" );

                int attackRowSelected = rowsMap.get( attack.getCompRow().toString() );
                int attackColSelected = Integer.parseInt( attack.getCompCol().toString() );

                defendingGrid[attackColSelected + 1][attackRowSelected].setHasShip( false );
                defendingGrid[attackColSelected + 1][attackRowSelected].setHit( true );
              } else {

                int attackRowSelected = rowsMap.get( attack.getCompRow().toString() );
                int attackColSelected = Integer.parseInt( attack.getCompCol().toString() );

                defendingGrid[attackColSelected + 1][attackRowSelected].setMiss( true );
              }

              //check ship sunk
              if( !attack.getUserShipSunk().equals( "no" ) ) {
                AlertDialog.Builder builder = new AlertDialog.Builder( BoardPlay.this );
                builder
                    .setMessage( "User " + attack.getUserShipSunk().toString().toUpperCase() + " Sunk! " +
                        "Computer has sunk " + attack.getNumComputerShipsSunk() + " of your ships!" )
                    .setCancelable( true )
                    .setPositiveButton( "Continue", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick( DialogInterface dialog, int which ) {
                      }
                    } )
                    .create()
                    .show();

              }

              if( !attack.getCompShipSunk().equals( "no" ) ) {
                AlertDialog.Builder builder = new AlertDialog.Builder( BoardPlay.this );
                builder
                    .setMessage( "Computer " + attack.getCompShipSunk().toString().toUpperCase() + " Sunk! " +
                        "You have sunk " + attack.getNumYourShipsSunk() + " of the computer's ships! Keep it up!" )
                    .setCancelable( true )
                    .setPositiveButton( "Continue", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick( DialogInterface dialog, int which ) {
                      }
                    } )
                    .create()
                    .show();
              }

              imgDefensiveGrid.invalidate();
              imgAttackingGrid.invalidate();

              //check winner
              if( attack.getWinner().equals( "computer" ) ) {
                AlertDialog.Builder builder = new AlertDialog.Builder( BoardPlay.this );
                builder.setTitle( "You Won!!!!!" )
                    .setMessage( "You Sunk All the Computer Ships!" )
                    .setCancelable( false )
                    .setPositiveButton( "Return to Game Lobby", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick( DialogInterface dialog, int which ) {
                        Intent intent = new Intent( getApplicationContext(), GameLobby.class );
                        startActivity( intent );
                      }
                    } )
                    .create()
                    .show();
              }

              if( attack.getWinner().equals( "you" ) ) {
                AlertDialog.Builder builder = new AlertDialog.Builder( BoardPlay.this );
                builder.setTitle( "You Lost!!!!! :(" )
                    .setMessage( "All Your Ships Were Sunk!" )
                    .setCancelable( false )
                    .setPositiveButton( "Return to Game Lobby", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick( DialogInterface dialog, int which ) {
                        Intent intent = new Intent( getApplicationContext(), GameLobby.class );
                        startActivity( intent );
                      }
                    } )
                    .create()
                    .show();
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
          String auth = "Basic " + Base64.encodeToString( credentials.getBytes(), Base64.NO_WRAP );
          headers.put( "Authorization", auth );
          return headers;
        }
      };
      requestQueue.add( stringRequest );
    }
  }

  private void InitializeApp() {
    // Initialize Game Grids
    defendingGrid = BoardSetup.defendingGrid;
    attackingGrid = BoardSetup.attackingGrid;
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
    playRowsSpinner.setAdapter( rowsSpinnerArrayAdapter );
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
    playColsSpinner.setAdapter( colsSpinnerArrayAdapter );
  }

}
