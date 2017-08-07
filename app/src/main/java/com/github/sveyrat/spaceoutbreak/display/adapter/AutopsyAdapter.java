package com.github.sveyrat.spaceoutbreak.display.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.sveyrat.spaceoutbreak.R;
import com.github.sveyrat.spaceoutbreak.domain.Player;

import java.util.List;

/**
 * Created by Rom on 07/08/2017.
 */

public class AutopsyAdapter extends BaseAdapter {

    private final Context context;
    private List<Player> players;

    public AutopsyAdapter(Context context, List<Player> players) {
        super();
        this.context = context;
        this.players = players;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Player player = players.get(position);
        View playerView = convertView;
        if (playerView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            playerView = inflater.inflate(R.layout.autopsy_item, parent, false);
        }

        TextView killedName = (TextView) playerView.findViewById(R.id.name_killed_tv);
        killedName.setText(String.format(this.context.getResources().getString(R.string.autopsy_activity_killed_name), player.getName()));

        TextView killedRole = (TextView) playerView.findViewById(R.id.role_killed_tv);
        killedRole.setText(String.format(this.context.getResources().getString(R.string.autopsy_activity_killed_role), player.getRole().getLabelResourceId()));

        TextView killedStatus = (TextView) playerView.findViewById(R.id.status_killed_tv);
        if (player.isMutant()) {
            killedStatus.setText(String.format(this.context.getResources().getString(R.string.autopsy_activity_killed_status), this.context.getResources().getString(R.string.common_yes)));
        } else {
            killedStatus.setText(String.format(this.context.getResources().getString(R.string.autopsy_activity_killed_status), this.context.getResources().getString(R.string.common_no)));
        }

        TextView killedGenotype = (TextView) playerView.findViewById(R.id.genotype_killed_tv);
        killedGenotype.setText(String.format(this.context.getResources().getString(R.string.autopsy_activity_killed_genotype), player.getGenome().getLabelResourcesId()));

        return playerView;
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

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}
