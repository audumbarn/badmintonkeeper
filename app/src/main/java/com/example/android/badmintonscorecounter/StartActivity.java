package com.example.android.badmintonscorecounter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void startGame(View view) {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.match_type);

        EditText player1EditText = (EditText) findViewById(R.id.editText);
        EditText player3EditText = (EditText) findViewById(R.id.editText3);

        EditText player2EditText = (EditText) findViewById(R.id.editText2);
        EditText player4EditText = (EditText) findViewById(R.id.editText4);

        String player2;
        String player4;
        String player1 = String.valueOf(player1EditText.getText());

        String player3 = String.valueOf(player3EditText.getText());

        if(radioGroup.getCheckedRadioButtonId() == R.id.doubles) {
            player2 = String.valueOf(player2EditText.getText());
            player4 = String.valueOf(player4EditText.getText());
        }
        else {
            player2 = player1;
            player4 = player3;
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("player1",player1);
        intent.putExtra("player2",player2);
        intent.putExtra("player3",player3);
        intent.putExtra("player4",player4);
        startActivity(intent);
    }
    public void onRadioButtonClicked(View view) {
        EditText playerA2 = (EditText) findViewById(R.id.editText2);
        EditText playerB2 = (EditText) findViewById(R.id.editText4);
        boolean checked = ((RadioButton)view).isChecked();
        switch (view.getId()) {
            case R.id.singles:
                if (checked){
                    playerA2.setVisibility(View.GONE);
                    playerB2.setVisibility(View.GONE);
                }
                break;
            case R.id.doubles:
                if (checked){
                    playerA2.setVisibility(View.VISIBLE);
                    playerB2.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}
