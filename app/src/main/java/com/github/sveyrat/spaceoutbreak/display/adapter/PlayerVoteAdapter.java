package com.github.sveyrat.spaceoutbreak.display.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.sveyrat.spaceoutbreak.R;
import com.github.sveyrat.spaceoutbreak.domain.Player;

import java.util.List;
import java.util.Map;

/**
 * Created by Rom on 05/10/2016.
 */

public class PlayerVoteAdapter extends BaseAdapter {

    private final Context context;
    private List<Player> players;
    private Map<Player, Player> votes;

    public PlayerVoteAdapter(Context context, List<Player> players, Map<Player, Player> votes) {
        super();
        this.context = context;
        this.players = players;
        this.votes = votes;
    }

    @Override
    public int getCount() {
        return players.size();
    }

    @Override
    public Player getItem(int position) {
        return players.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Player player = players.get(position);
        View playerView = convertView;
        if (playerView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            playerView = inflater.inflate(R.layout.player_item, parent, false);
            if (votes.containsKey(player)) {
                ImageView selectedImageView = (ImageView) playerView.findViewById(R.id.selected_image);
                selectedImageView.setVisibility(View.VISIBLE);
            }
        }
        TextView playerNameText = (TextView) playerView.findViewById(R.id.name_player_tv);
        String playerName;
        if (player == null) {
            playerName = context.getResources().getString(R.string.vote_activity_blank_vote);
        } else {
            playerName = player.getName();
        }
        playerNameText.setText(playerName);
        playerView.setBackground(ContextCompat.getDrawable(context, R.drawable.small_border));

        return playerView;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setVotes(Map<Player, Player> votes) {
        this.votes = votes;
    }

}
