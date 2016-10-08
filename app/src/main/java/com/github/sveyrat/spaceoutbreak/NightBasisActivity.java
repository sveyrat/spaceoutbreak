package com.github.sveyrat.spaceoutbreak;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.display.PlayerNightAdapter;

import android.widget.ImageView;

import com.github.sveyrat.spaceoutbreak.display.nightaction.MutantsMutateOrKillStepManager;
import com.github.sveyrat.spaceoutbreak.display.nightaction.StepManager;
import com.github.sveyrat.spaceoutbreak.domain.Player;

import java.util.List;

/**
 * Created by Rom on 22/09/2016.
 */
public class NightBasisActivity extends AppCompatActivity {

    private PlayerNightAdapter adapter;
    private TextView headerTextView;
    private TextView mutantCounter;
    private TextView afterStepTextView;
    private GridView playerGrid;

    private StepManager stepManager = new MutantsMutateOrKillStepManager();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RepositoryManager.init(this);
        setContentView(R.layout.activity_night_basis);

        headerTextView = (TextView) findViewById(R.id.night_basis_step_tv);
        mutantCounter = (TextView) findViewById(R.id.mutant_counter);
        afterStepTextView = (TextView) findViewById(R.id.after_step_text);

        List<Player> alivePlayers = RepositoryManager.getInstance().gameInformationRepository().loadAlivePlayers();
        adapter = new PlayerNightAdapter(this, alivePlayers);
        playerGrid = (GridView) findViewById(R.id.night_basis_list_players);
        playerGrid.setAdapter(adapter);
        updateView();

        playerGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Player player = adapter.getItem(position);
                ImageView selectedImageView = (ImageView) v.findViewById(R.id.selected_image);
                stepManager.select(NightBasisActivity.this, selectedImageView, player);
            }
        });
    }


    public void confirm(View view) {
        String afterStepText = stepManager.afterStepText(this);
        if (View.GONE == afterStepTextView.getVisibility() && afterStepText != null) {
            headerTextView.setText(getResources().getString(R.string.night_basis_gameMasterAction_headerText));
            afterStepTextView.setText(afterStepText);
            afterStepTextView.setVisibility(View.VISIBLE);
            playerGrid.setVisibility(View.GONE);
            return;
        }
        boolean validationResult = stepManager.validateStep(this);
        if (!validationResult) {
            return;
        }
        stepManager = stepManager.nextStep();
        updateView();
    }

    private void updateView() {
        afterStepTextView.setText("");
        afterStepTextView.setVisibility(View.GONE);
        playerGrid.setVisibility(View.VISIBLE);

        List<Player> alivePlayers = RepositoryManager.getInstance().gameInformationRepository().loadAlivePlayers();
        adapter.setPlayers(alivePlayers);
        adapter.notifyDataSetChanged();

        Integer nbMutants = RepositoryManager.getInstance().gameInformationRepository().countMutantsInCurrentGame();
        mutantCounter.setText(nbMutants.toString());

        headerTextView.setText(stepManager.headerText(this));
    }
}
