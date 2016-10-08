package com.github.sveyrat.spaceoutbreak;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.display.PlayerNightAdapter;
import com.github.sveyrat.spaceoutbreak.domain.Player;

import java.util.List;

/**
 * Created by Rom on 22/09/2016.
 */
public class NightBasisActivity extends AppCompatActivity {
    private PlayerNightAdapter adapter;
    private List<Player> players;
    private TextView mutantCounter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RepositoryManager.init(this);
        players = RepositoryManager.getInstance().gameInformationRepository().loadAlivePlayers();
        setContentView(R.layout.activity_night_basis);
        mutantCounter = (TextView) findViewById(R.id.mutant_counter);

        adapter = new PlayerNightAdapter(this,players);
        GridView gridView = (GridView) findViewById(R.id.night_basis_list_players);
        gridView.setAdapter(adapter);
        updateView();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                //final String itemName = ((TextView) v).getText().toString();
                final Player player = adapter.getItem(position);
                AlertDialog.Builder adb = new AlertDialog.Builder(NightBasisActivity.this);
                String message = "Joueur "+ player.getName();
                adb.setMessage(message);
                adb.setNegativeButton("Muter ?",new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        RepositoryManager.getInstance().nightActionRepository().mutate(player);
                        updateView();
                    }
                });
                adb.setPositiveButton("Soigner ?", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        RepositoryManager.getInstance().nightActionRepository().heal(player);
                        updateView();
                    }
                });
                adb.show();
            }
        });
    }

    void updateView(){
        players = RepositoryManager.getInstance().gameInformationRepository().loadAlivePlayers();
        adapter.notifyDataSetChanged();
        int nbMutants= RepositoryManager.getInstance().nightActionRepository().countMutantsForComputerScientist();
        mutantCounter.setText(""+nbMutants);
    }
}
