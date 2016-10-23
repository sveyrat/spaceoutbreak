package com.github.sveyrat.spaceoutbreak;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.util.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NewGameInputPlayerActivity extends AppCompatActivity {

    ArrayAdapter<String> adapter;
    private ArrayList<String> players = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RepositoryManager.init(this);

        setContentView(R.layout.activity_new_game_input_player);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                players);
        ListView listView = (ListView) findViewById(R.id.new_game_input_player_player_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                final String itemName = ((TextView) v).getText().toString();
                AlertDialog.Builder adb = new AlertDialog.Builder(NewGameInputPlayerActivity.this);
                String message = String.format(getResources().getString(R.string.new_game_player_input_delete_message), itemName);
                adb.setMessage(message);
                adb.setNegativeButton(getResources().getString(R.string.new_game_player_input_no), null);
                adb.setPositiveButton(getResources().getString(R.string.new_game_player_input_yes), new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.remove(itemName);
                    }
                });
                adb.show();
            }
        });

        if (savedInstanceState != null) {
            players = savedInstanceState.getStringArrayList("players");
            adapter.addAll(players); // TODO : put outside of condition when default population is removed
        }else{
            adapter.addAll("Annie", "Braum", "Caitlyn", "Dr Mundo", "Ezreal", "Fiddlesticks", "Gragas", "Hecarim", "Illaoi", "Jarvan IV"); // TODO remove : populate list for testing purposes only
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        outState.putStringArrayList("players", players);
    }

    public void addPlayer(View view) {
        EditText playerNameEditText = (EditText) findViewById(R.id.new_game_input_player_player_name);
        String playerName = playerNameEditText.getText().toString();
        if (playerName.isEmpty()) {
            return;
        }
        if (StringUtil.containsIgnoreCase(players, playerName)) {
            Toast toast = Toast.makeText(NewGameInputPlayerActivity.this, getResources().getString(R.string.new_game_player_input_same_player_error), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }
        adapter.insert(playerName, 0);
        playerNameEditText.setText("");
    }

    public void validatePlayerList(View view) {
        if (players.size() < getResources().getInteger(R.integer.min_players)) {
            Toast toast = Toast.makeText(NewGameInputPlayerActivity.this, getResources().getString(R.string.new_game_player_input_not_enough_players_error), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }
        RepositoryManager.getInstance().initGameRepository().createGameWithPlayers(players);
        Intent newGameSettingsIntent = new Intent(this, NewGameSettingsActivity.class);
        startActivity(newGameSettingsIntent);
    }
}
