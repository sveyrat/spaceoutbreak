package com.github.sveyrat.spaceoutbreak.display;

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

/**
 * Created by Rom on 15/01/2017.
 */

public class CaptainVoteAdapter extends BaseAdapter {

    private final Context context;
    private List<Player> players;
    private Player voted;

    public CaptainVoteAdapter(Context context, List<Player> players, Player voted) {
        super();
        this.context = context;
        this.players = players;
        this.voted = voted;
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
            if(voted != null) {
                if (voted.equals(player)) {
                    ImageView selectedImageView = (ImageView) playerView.findViewById(R.id.selected_image);
                    selectedImageView.setVisibility(View.VISIBLE);
                }
            }
        }
        TextView playerName = (TextView) playerView.findViewById(R.id.name_player_tv);
        playerName.setText(player.getName());
        playerView.setBackground(ContextCompat.getDrawable(context, R.drawable.small_border));

        return playerView;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setVoted(Player voted) {
        this.voted = voted;
    }
}
