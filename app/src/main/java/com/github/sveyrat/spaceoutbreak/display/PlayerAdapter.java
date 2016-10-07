package com.github.sveyrat.spaceoutbreak.display;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.sveyrat.spaceoutbreak.R;
import com.github.sveyrat.spaceoutbreak.domain.Player;
import com.github.sveyrat.spaceoutbreak.domain.constant.Role;

import java.util.List;

/**
 * Created by Rom on 02/10/2016.
 */

public class PlayerAdapter extends BaseAdapter {

    private final Context context;
    private final List<Player> players;

    public PlayerAdapter(Context context, List<Player> players) {
        super();
        this.context = context;
        this.players = players;
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
        }
        TextView playerName = (TextView) playerView.findViewById(R.id.name_player_tv);
        playerName.setText(player.getName());

        ImageView playerRolePicto = (ImageView) playerView.findViewById(R.id.player_role_picto);
        switch (player.getRole()) {
            case BASE_MUTANT:
                playerRolePicto.setImageResource(R.mipmap.base_mutant);
                break;
            case DOCTOR:
                playerRolePicto.setImageResource(R.mipmap.doctor);
                break;
            case GENETICIST:
                playerRolePicto.setImageResource(R.mipmap.geneticist);
                break;
            case PSYCHOLOGIST:
                playerRolePicto.setImageResource(R.mipmap.psychologist);
                break;
            case COMPUTER_SCIENTIST:
                playerRolePicto.setImageResource(R.mipmap.computer_scientist);
                break;
            case HACKER:
                playerRolePicto.setImageResource(R.mipmap.hacker);
                break;
            case SPY:
                playerRolePicto.setImageResource(R.mipmap.spy);
                break;
            case FANATIC:
                playerRolePicto.setImageResource(R.mipmap.fanatic);
                break;
        }

        if (player.isMutant()) {
            playerView.setBackground(ContextCompat.getDrawable(context, R.drawable.mutant_small_border));
        } else {
            playerView.setBackground(ContextCompat.getDrawable(context, R.drawable.small_border));
        }

        return playerView;
    }
}
