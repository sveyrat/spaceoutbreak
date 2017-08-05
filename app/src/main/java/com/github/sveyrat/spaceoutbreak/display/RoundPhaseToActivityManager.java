package com.github.sveyrat.spaceoutbreak.display;

import android.content.Context;
import android.content.Intent;

import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.dao.dto.RoundPhase;
import com.github.sveyrat.spaceoutbreak.display.activity.CaptainElectionActivity;
import com.github.sveyrat.spaceoutbreak.display.activity.GameEndActivity;
import com.github.sveyrat.spaceoutbreak.display.activity.NightBasisActivity;
import com.github.sveyrat.spaceoutbreak.display.activity.VoteActivity;
import com.github.sveyrat.spaceoutbreak.log.Logger;

public class RoundPhaseToActivityManager {

    public static Intent goToActivityIntent(Context context, RoundPhase roundPhase) {
        Logger.getInstance().info(RoundPhaseToActivityManager.class.getName(), "Selecting activity for RoundPhase " + roundPhase);
        switch (roundPhase) {
            case NIGHT:
                return new Intent(context, NightBasisActivity.class);
            case DAY:
                return new Intent(context, VoteActivity.class);
            case CAPTAIN_ELECTION:
                return new Intent(context, CaptainElectionActivity.class);
            case NEW:
                RepositoryManager.getInstance().nightActionRepository().newRound();
                return new Intent(context, NightBasisActivity.class);
            case END:
                return new Intent(context, GameEndActivity.class);
        }
        String message = "RoundPhase " + roundPhase + " is not handled.";
        Logger.getInstance().error(RoundPhaseToActivityManager.class.getName(), message);
        throw new RuntimeException(message);
    }
}
