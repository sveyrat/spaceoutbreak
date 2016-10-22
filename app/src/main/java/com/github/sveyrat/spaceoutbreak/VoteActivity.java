package com.github.sveyrat.spaceoutbreak;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.display.PlayerVoteAdapter;
import com.github.sveyrat.spaceoutbreak.domain.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Rom on 05/10/2016.
 */

public class VoteActivity extends AppCompatActivity {
    private List<Player> players;
    private CharSequence[] playersToVote;
    private PlayerVoteAdapter adapter;
    private Map<Player, Player> voteResults;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RepositoryManager.init(this);
        players = RepositoryManager.getInstance().gameInformationRepository().loadAlivePlayers();
        playersToVote = putPlayerNamesInCharSequence(players);
        voteResults = new HashMap<Player, Player>();
        setContentView(R.layout.activity_vote);

        adapter = new PlayerVoteAdapter(this, players);
        GridView gridView = (GridView) findViewById(R.id.vote_list_players);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, final View selectedView, int position, long id) {
                final Player player = adapter.getItem(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(VoteActivity.this);
                String message = String.format(getResources().getString(R.string.vote_activity_dialog_title), player.getName());

                int tempVote = 0;
                if (voteResults.containsKey(player) && voteResults.get(player) != null) {
                    tempVote = getPlayerPositionInCharSequence(playersToVote, voteResults.get(player));
                }
                builder.setTitle(message)
                        .setSingleChoiceItems(playersToVote, tempVote, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {

                                // The 'which' argument contains the index position
                                // of the selected item
                                int finalChoicePosition = which - 1;
                                if (voteResults.containsKey(player)) {
                                    voteResults.remove(player);
                                }

                                if (which != 0) {
                                    voteResults.put(player, players.get(finalChoicePosition));
                                } else {
                                    voteResults.put(player, null);
                                }
                                updateView();
                                ImageView selectedImageView = (ImageView) selectedView.findViewById(R.id.selected_image);
                                selectedImageView.setVisibility(View.VISIBLE);

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                    }
                                }, 500);
                            }

                        });
                /*// Set the action buttons
                        .setPositiveButton(R.string.common_validate, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        if(voteResults.containsKey(player)){
                            if(finalPosition!=0) {
                                voteResults.put(player, players.get(finalPosition));
                            }
                            else{
                                voteResults.put(player, null);
                            }

                        }
                            dialog.dismiss();


                    }
                })
                        .setNegativeButton(R.string.common_return, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                               dialog.dismiss();
                            }
                        });*/

                builder.show();
            }
        });

    }

    public void confirm(View view) {
        List<Player> results = new ArrayList<>();
        results.addAll(voteResults.values());
        int nullVoteNumber = players.size() - results.size();
        int blankVoteNumber = 0;
        Map<String, Integer> finalResults = new HashMap<String, Integer>();

        while (results.remove(null)) {
            blankVoteNumber++;
        }

        finalResults.put(getResources().getString(R.string.vote_activity_null_vote), nullVoteNumber);
        finalResults.put(getResources().getString(R.string.vote_activity_blank_vote), blankVoteNumber);

        if (nullVoteNumber + blankVoteNumber < players.size() + 1) {
            for (Player e : results) {
                String name = e.getName();
                if (finalResults.containsKey(name)) {
                    Integer lastVal = finalResults.get(name);
                    finalResults.remove(name);
                    finalResults.put(name, lastVal + 1);
                } else {
                    finalResults.put(name, 1);
                }
            }
        }

        AlertDialog.Builder adb = new AlertDialog.Builder(VoteActivity.this);
        String message = finalResults.toString();
        message = message.replace(",", "\n");
        message = message.replace("{", "");
        message = message.replace("}", "");
        adb.setTitle(getResources().getString(R.string.vote_activity_dialog_result_title));
        adb.setMessage(message);
        adb.setNegativeButton(getResources().getString(R.string.common_return), null);
        adb.setPositiveButton(getResources().getString(R.string.common_validate), null);
        adb.show();


    }

    void updateView() {
        adapter.setPlayers(players);
        adapter.notifyDataSetChanged();

    }

    CharSequence[] putPlayerNamesInCharSequence(List<Player> players) {
        List<String> playerNames = new ArrayList<String>();
        playerNames.add(getResources().getString(R.string.vote_activity_blank_vote));
        for (Player player : players) {
            playerNames.add(player.getName());
        }
        CharSequence[] playersNamesInCharSequence = playerNames.toArray(new CharSequence[players.size()]);
        return playersNamesInCharSequence;
    }

    int getPlayerPositionInCharSequence(CharSequence[] names, Player player) {
        int position = 0;
        for (CharSequence s : names) {
            if (s.equals(player.getName())) {
                return position;
            }
            position++;
        }
        return -1;
    }
}

