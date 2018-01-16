package com.example.android.badmintonscorecounter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * This app is created for Badminton sport. This sport consists of maximum 3 games(In badminton a set is called game). Each game is of 21 points each.
 * Team who scores 21 points first wins the game. If both teams scores equally at 20-20 then game goes to tiebreaker.
 * Team who takes 2 points lead wins the game. If no team is able to take 2-points lead till 29 points, then team
 * scoring 30th point wins the game.
 * Team who wins 2 sets wins the match. There will be maximum of 3 games.
 */
public class MainActivity extends AppCompatActivity {

    int teamAScore = 0;
    int teamBScore = 0;
    int[][] tally = new int[3][2];
    int currentGame = 0;
    int gameWonByA = 0;
    int gameWonByB = 0;
    int pointWonBy = 0;
    boolean serviceBroken = false;
    String player1, player2, player3, player4;
    String teamAServer="A0";
    String teamBServer="B0";

    Context context = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();

        player1 = intent.getStringExtra("player1");
        player2 = intent.getStringExtra("player2");
        player3 = intent.getStringExtra("player3");
        player4 = intent.getStringExtra("player4");

        teamAServer = player1;
        teamBServer = player3;

        TextView serveTextView = (TextView) findViewById(R.id.serve_text_view);
        serveTextView.setText(player1+ " serves from RIGHT");
        context = getApplicationContext();
    }

    /*Display score of team A*/
    private void displayTeamAScore() {
        TextView textView = (TextView) findViewById(R.id.teama_text_view);
        textView.setText(String.valueOf(teamAScore));
    }
    /*Display score of team B*/
    private void displayTeamBScore() {
        TextView textView = (TextView) findViewById(R.id.teamb_text_view);
        textView.setText(String.valueOf(teamBScore));
    }
    /*Add point to the team A's game*/
    public void addTeamAPoint(View view) {
        /*Following if is to disable adding games more than 3. If any of the player wins 2 games first, then 3rd game should not be played*/
        if (currentGame < 3 || (gameWonByA <2 && gameWonByB <2) ) {
            teamAScore = teamAScore+1;
            displayTeamAScore();

            if (pointWonBy == 1) {
                //service broke
                serviceBroken = true;
                if(teamAServer.equals(player1)){
                    teamAServer = player2;
                }
                else {
                    teamAServer = player1;
                }
            }
            pointWonBy = 0;
            updateServe(0);
            if((teamAScore == 21 && teamBScore<20) || (teamAScore>20 && teamAScore <= 29 && teamBScore < (teamAScore-1)) || (teamAScore == 30 && teamBScore<30)){
                /*Team A has won this game. Add this score to tally of current match. Then set scores for next game as (0,0)*/
                gameWonByA++;

                tally[currentGame][0] = teamAScore;
                tally[currentGame][1] = teamBScore;
                updateTally();
                currentGame++;
                teamAScore = 0;
                teamBScore = 0;

                /*small vibration to indicate current game has been completed */
                Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);

                /*Show dialog to the user stating which team has won current game/match*/
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                /*If TeamA wins 2 games, then show match completed message*/
                if(gameWonByA == 2) {
                    builder.setMessage("Team A Won the Match")
                            .setTitle("Match Completed");
                    /*Incrementing currentGame to 3 to avoid adding further games.*/
                    currentGame++;
                }
                else{
                    builder.setMessage("Team A Won the Game")
                            .setTitle("Game Completed");
                }
                /*Continue this match*/
                builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        displayTeamAScore();
                        displayTeamBScore();
                    }
                });
                /*Reset match from Game1*/
                builder.setNegativeButton("reset match", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetMatch();
                    }
                });
                AlertDialog dialog = builder.create();

                /*Disable clicking outside to prevent adding more points to the game*/
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);

                dialog.show();
            }


        }
        else if(gameWonByA == 2 || currentGame == 3){
            /*If any of the player wins 2 games, match is over so prevent user from adding more games*/
            Button teamAPointButton = (Button) findViewById(R.id.teamAButton);
            teamAPointButton.setClickable(false);

            Button teamBPointButton = (Button) findViewById(R.id.teamBButton);
            teamBPointButton.setClickable(false);
        }

    }

    /*Following method contains same logic as of addTeamPoint method but is for TeamB's score. */
    public void addTeamBPoint(View view) {
        if(currentGame <3) {
            teamBScore = teamBScore+1;
            displayTeamBScore();

            if (pointWonBy == 0) {
                //service broke
                serviceBroken = true;
                if(teamBServer.equals(player3)){
                    teamBServer = player4;
                }
                else {
                    teamBServer = player3;
                }
            }
            pointWonBy = 1;
            updateServe(1);
            if((teamBScore == 21 && teamAScore<20) || (teamBScore>20 && teamBScore <= 29 && teamAScore < (teamBScore-1)) || (teamBScore == 30 && teamAScore<30)){
                gameWonByB++;

                Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);

                tally[currentGame][0] = teamAScore;
                tally[currentGame][1] = teamBScore;
                updateTally();
                currentGame++;
                teamAScore = 0;
                teamBScore = 0;

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                if(gameWonByB ==2){
                    builder.setMessage("Team B Won the Match")
                            .setTitle("Match Completed");
                    currentGame++;
                }
                else {
                    builder.setMessage("Team B Won the Game")
                            .setTitle("Game Completed");
                }
                builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        displayTeamAScore();
                        displayTeamBScore();
                    }
                });
                builder.setNegativeButton("reset match", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetMatch();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        }
        else if (gameWonByB == 2 || currentGame == 3){
            Button teamAPointButton = (Button) findViewById(R.id.teamAButton);
            teamAPointButton.setClickable(false);

            Button teamBPointButton = (Button) findViewById(R.id.teamBButton);
            teamBPointButton.setClickable(false);
        }

    }
    public void resetScores(View view) {
        resetMatch();
    }

    /*Reset scores*/
    private void resetMatch() {
        teamAScore = 0;
        teamBScore = 0;
        gameWonByA = 0;
        gameWonByB = 0;
        currentGame = 0;
        for (int i=0;i<3;i++) {
            tally[i][0] = 0;
            tally[i][1] = 0;
        }
        updateTally();
        displayTeamAScore();
        displayTeamBScore();
        Button teamAPointButton = (Button) findViewById(R.id.teamAButton);
        teamAPointButton.setClickable(true);

        Button teamBPointButton = (Button) findViewById(R.id.teamBButton);
        teamBPointButton.setClickable(true);
        TextView serveTextView = (TextView) findViewById(R.id.serve_text_view);
        serveTextView.setText(player1+" serves from RIGHT");
    }

    /*Update match score tally. This table basically contains a column for no. of games won by each team & another 3 columns for game1 to game3
    * Textviews are named in the format of game[currentGame][teamAScore/teamBScore]. This helps to conveniently update correct row & column
    * */
    private void updateTally() {
        TextView gamesWonByA = (TextView) findViewById(R.id.games_won_by_A);
        gamesWonByA.setText(String.valueOf(gameWonByA));

        TextView gamesWonByB = (TextView) findViewById(R.id.games_won_by_B);
        gamesWonByB.setText(String.valueOf(gameWonByB));

        TextView game00TextView = (TextView) findViewById(R.id.game00);
        game00TextView.setText(String.valueOf(tally[0][0]));

        TextView game01TextView = (TextView) findViewById(R.id.game01);
        game01TextView.setText(String.valueOf(tally[0][1]));


        TextView game10TextView = (TextView) findViewById(R.id.game10);
        game10TextView.setText(String.valueOf(tally[1][0]));

        TextView game11TextView = (TextView) findViewById(R.id.game11);
        game11TextView.setText(String.valueOf(tally[1][1]));


        TextView game20TextView = (TextView) findViewById(R.id.game20);
        game20TextView.setText(String.valueOf(tally[2][0]));

        TextView game21TextView = (TextView) findViewById(R.id.game21);
        game21TextView.setText(String.valueOf(tally[2][1]));


    }

    private void updateServe(int team) {
        String serveStr;

        if(team == 0) {
            //teamA wins point
            if(teamAScore%2 == 0) {
                //team a player serves from right
                serveStr = teamAServer+" serves from RIGHT";
            }
            else {
                //team a player serves from left
                serveStr = teamAServer+ " serves from LEFT";
            }
        }
        else {
            //teamB wins point
            if(teamBScore%2 == 0) {
                //team b player serves from right
                serveStr = teamBServer+" serves from RIGHT";
            }
            else {
                //team b player serves from left
                serveStr = teamBServer+ " serves from LEFT";
            }
        }
        TextView serveTextView = (TextView) findViewById(R.id.serve_text_view);
        serveTextView.setText(serveStr);
    }

}
