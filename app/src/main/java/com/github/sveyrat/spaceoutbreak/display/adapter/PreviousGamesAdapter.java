package com.github.sveyrat.spaceoutbreak.display.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.sveyrat.spaceoutbreak.R;
import com.github.sveyrat.spaceoutbreak.domain.Game;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Rom on 01/08/2017.
 */

public class PreviousGamesAdapter extends BaseAdapter {

    private final Context context;
    private List<Game> games;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM - HH:mm");

    public PreviousGamesAdapter(Context context, List<Game> games) {
        super();
        this.context = context;
        this.games = games;
    }

    @Override
    public int getCount() {
        return games.size();
    }

    @Override
    public Game getItem(int position) {
        return games.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setPlayers(List<Game> games) {
        this.games = games;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Game game = games.get(position);
        View gameView = convertView;
        if (gameView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gameView = inflater.inflate(R.layout.previous_game_item, parent, false);
        }
        TextView gameDate = (TextView) gameView.findViewById(R.id.previous_game_date);
        gameDate.setText(sdf.format(game.getCreationDate()));

        TextView nrPlayers = (TextView) gameView.findViewById(R.id.previous_game_nr_of_players);
        nrPlayers.setText("Alive = "+game.getPlayers().size());

        return gameView;
    }

}
