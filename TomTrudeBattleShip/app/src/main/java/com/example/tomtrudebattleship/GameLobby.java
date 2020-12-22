package com.example.tomtrudebattleship;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class GameLobby extends BaseActivity {

  Button btnChallengeComp;
  TextView txtAvatarName, txtFirstName, txtLastName, txtEmail, txtBattlesWon, txtBattlesLost, txtBattlesTied, txtLevel, txtCoins, txtXP;
  ImageView imgAvatar;


  @SuppressLint( "SetTextI18n" )
  @Override
  public void onCreate( Bundle savedInstanceState ) {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_game_lobby );

    btnChallengeComp = findViewById( R.id.btnChallengeComp );

    txtAvatarName = findViewById( R.id.txtAvatarName );
    txtFirstName = findViewById( R.id.txtFirstName );
    txtLastName = findViewById( R.id.txtLastName );
    txtEmail = findViewById( R.id.txtEmail );
    txtBattlesWon = findViewById( R.id.txtBattlesWon );
    txtBattlesLost = findViewById( R.id.txtBattlesLost );
    txtBattlesTied = findViewById( R.id.txtBattlesTied );
    txtLevel = findViewById( R.id.txtLevel );
    txtCoins = findViewById( R.id.txtCoins );
    txtXP = findViewById( R.id.txtXP );
    imgAvatar = findViewById( R.id.imgAvatar );


    txtAvatarName.setText( userPrefs.getAvatarName() );
    txtFirstName.setText( userPrefs.getFirstName() );
    txtLastName.setText( userPrefs.getLastName() );
    txtEmail.setText( userPrefs.getEmail() );
    txtBattlesWon.setText( Integer.toString(userPrefs.getBattlesWon()) );
    txtBattlesLost.setText( Integer.toString(userPrefs.getBattlesLost()) );
    txtBattlesTied.setText( Integer.toString(userPrefs.getBattlesTied()) );
    txtLevel.setText( Integer.toString(userPrefs.getLevel()) );
    txtCoins.setText( Integer.toString(userPrefs.getCoins()) );
    txtXP.setText( Integer.toString(userPrefs.getExperiencePoints()) );
    Picasso.with(this).load( "http://battlegameserver.com/" + userPrefs.getAvatarImage() ).into( imgAvatar );

  }

  public void challengeComputerOnClick( View v ){

    Intent intent = new Intent( getApplicationContext(), BoardSetup.class );
    startActivity( intent );

  }
}
