package com.github.sveyrat.spaceoutbreak;

import android.content.DialogInterface;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.display.PlayerAdapter;
import com.github.sveyrat.spaceoutbreak.display.nightaction.MutantsMutateOrKillStepManager;
import com.github.sveyrat.spaceoutbreak.domain.Player;

import java.util.List;

/**
 * Created by Rom on 22/09/2016.
 */
public class NightBasisActivity extends AppCompatActivity {
    private PlayerAdapter adapter;
    private List<Player> players;
    private TextView mutantCounter;

    private MutantsMutateOrKillStepManager stepManager = new MutantsMutateOrKillStepManager();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RepositoryManager.init(this);
        players = RepositoryManager.getInstance().gameInformationRepository().loadAlivePlayers();
        setContentView(R.layout.activity_night_basis);
        mutantCounter = (TextView) findViewById(R.id.mutant_counter);

        adapter = new PlayerAdapter(this, players);
        GridView gridView = (GridView) findViewById(R.id.night_basis_list_players);
        gridView.setAdapter(adapter);
        updateView();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Player player = adapter.getItem(position);
                ImageView selectedImageView = (ImageView) v.findViewById(R.id.selected_image);
                stepManager.select(NightBasisActivity.this, selectedImageView, player);
            }
        });
    }

    public void confirm(View view) {
        // TODO call step manager
        // TODO go to the next step
    }

    private void updateView() {
        players = RepositoryManager.getInstance().gameInformationRepository().loadAlivePlayers();
        adapter.notifyDataSetChanged();
        Integer nbMutants = RepositoryManager.getInstance().nightActionRepository().countMutantsForComputerScientist();
        mutantCounter.setText(nbMutants.toString());
    }
}
