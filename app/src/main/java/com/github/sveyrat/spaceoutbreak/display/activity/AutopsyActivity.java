package com.github.sveyrat.spaceoutbreak.display.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.github.sveyrat.spaceoutbreak.R;
import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.dao.repository.GameInformationRepository;
import com.github.sveyrat.spaceoutbreak.dao.repository.NightActionRepository;
import com.github.sveyrat.spaceoutbreak.dao.repository.VoteRepository;
import com.github.sveyrat.spaceoutbreak.display.RoundPhaseToActivityManager;
import com.github.sveyrat.spaceoutbreak.display.adapter.AutopsyAdapter;
import com.github.sveyrat.spaceoutbreak.domain.Player;

import java.util.ArrayList;
import java.util.List;

import static com.github.sveyrat.spaceoutbreak.display.RoundPhaseToActivityManager.IS_NIGHT_AUTOPSY_INTENT_EXTRA_FIELD_NAME;

/**
 * Created by Rom on 02/08/2017.
 */

public class AutopsyActivity extends AppCompatActivity {

    private List<Player> killedPlayers;
    private ListView listView;
    private AutopsyAdapter adapter;
    private boolean isNight;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RepositoryManager.init(this);

        setContentView(R.layout.activity_autopsy);

        isNight = getIntent().getBooleanExtra(IS_NIGHT_AUTOPSY_INTENT_EXTRA_FIELD_NAME, false);

        if (isNight) {
            NightActionRepository nightActionRepository = RepositoryManager.getInstance().nightActionRepository();
            killedPlayers = nightActionRepository.killedDuringNightPhase();
        } else {
            VoteRepository voteRepository = RepositoryManager.getInstance().voteRepository();
            killedPlayers = new ArrayList<>();
            killedPlayers.add(voteRepository.killedThisRound());
        }
        adapter = new AutopsyAdapter(this, killedPlayers);
        listView = (ListView) findViewById(R.id.autopsy_list);
        listView.setAdapter(adapter);

    }

    public void confirm(View view) {
        GameInformationRepository gameInformationRepository = RepositoryManager.getInstance().gameInformationRepository();
        if (isNight) {
            gameInformationRepository.markNightAutopsyAsDone();
        } else {
            gameInformationRepository.markDayAutopsyAsDone();
        }
        Intent nextActivityIntent = RoundPhaseToActivityManager.nextRoundPhaseIntent(AutopsyActivity.this);
        startActivity(nextActivityIntent);

    }

    @Override
    public void onBackPressed() {
    }
}