package com.github.sveyrat.spaceoutbreak;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class NewGameInputPlayerActivity extends AppCompatActivity {

    private List<String> players = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game_input_player);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                players);
        ListView listView = (ListView) findViewById(R.id.new_game_input_player_player_list);
        listView.setAdapter(adapter);
    }

    public void addPlayer(View view) {
        // TODO here : handle already existing player name and empty input cases
        EditText playerNameEditText = (EditText) findViewById(R.id.new_game_input_player_player_name);
        String playerName = playerNameEditText.getText().toString();
        adapter.add(playerName);
        playerNameEditText.setText("");
    }

    public void validatePlayerList(View view) {
        // TODO
    }
}
