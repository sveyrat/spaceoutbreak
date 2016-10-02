package com.github.sveyrat.spaceoutbreak;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.display.PlayerAdapter;
import com.github.sveyrat.spaceoutbreak.domain.Player;

import java.util.List;

/**
 * Created by Rom on 22/09/2016.
 */
public class NightBasisActivity extends AppCompatActivity {
    private PlayerAdapter adapter;
    private List<Player> players;
    private TextView mutantCounter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RepositoryManager.init(this);
        players = RepositoryManager.getInstance().gameInformationRepository().loadAlivePlayers();
        setContentView(R.layout.activity_night_basis);
        mutantCounter = (TextView) findViewById(R.id.mutant_counter);

        adapter = new PlayerAdapter(this,players);
        GridView gridView = (GridView) findViewById(R.id.night_basis_list_players);
        gridView.setAdapter(adapter);
        updateView();
    }






    void updateView(){
        players = RepositoryManager.getInstance().gameInformationRepository().loadAlivePlayers();
        adapter.notifyDataSetChanged();
        int nbMutants= RepositoryManager.getInstance().nightActionRepository().countMutantsForComputerScientist();
        mutantCounter.setText(""+nbMutants);

    }
}
