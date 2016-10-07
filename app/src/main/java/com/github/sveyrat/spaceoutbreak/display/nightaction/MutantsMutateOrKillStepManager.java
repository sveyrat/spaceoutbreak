package com.github.sveyrat.spaceoutbreak.display.nightaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageView;

import com.github.sveyrat.spaceoutbreak.domain.Player;

import java.util.ArrayList;
import java.util.List;

public class MutantsMutateOrKillStepManager {

    private List<Player> mutedPlayers = new ArrayList<>();
    private List<Player> killedPlayers = new ArrayList<>();

    public void select(Context context, final ImageView selectedImageView, final Player player) {
        final boolean playerSelected = (View.VISIBLE == selectedImageView.getVisibility());
        if (playerSelected) {
            mutedPlayers.remove(player);
            killedPlayers.remove(player);
            selectedImageView.setVisibility(View.GONE);
            return;
        }

        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        String message = "Joueur " + player.getName();
        adb.setMessage(message);
        adb.setNegativeButton("Muter", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mutedPlayers.add(player);
                selectedImageView.setVisibility(View.VISIBLE);
            }
        });
        adb.setPositiveButton("Tuer", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                killedPlayers.add(player);
                selectedImageView.setVisibility(View.VISIBLE);
            }
        });
        adb.show();
    }
}
