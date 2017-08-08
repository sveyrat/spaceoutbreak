package com.github.sveyrat.spaceoutbreak.display;

import android.content.Context;
import android.content.Intent;

import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.dao.dto.RoundPhase;
import com.github.sveyrat.spaceoutbreak.display.activity.AutopsyActivity;
import com.github.sveyrat.spaceoutbreak.display.activity.CaptainElectionActivity;
import com.github.sveyrat.spaceoutbreak.display.activity.GameEndActivity;
import com.github.sveyrat.spaceoutbreak.display.activity.NightBasisActivity;
import com.github.sveyrat.spaceoutbreak.display.activity.VoteActivity;
import com.github.sveyrat.spaceoutbreak.log.Logger;

public class RoundPhaseToActivityManager {

    public static final String IS_NIGHT_AUTOPSY_INTENT_EXTRA_FIELD_NAME = "IS_NIGHT_AUTOPSY";

    public static Intent nextRoundPhaseIntent(Context context) {
        RoundPhase roundPhase = RepositoryManager.getInstance().gameInformationRepository().nextRoundPhase();
        switch (roundPhase) {
            case CAPTAIN_ELECTION:
                return new Intent(context, CaptainElectionActivity.class);
            case NIGHT:
                return new Intent(context, NightBasisActivity.class);
            case NIGHT_AUTOPSY:
                Intent nightAutopsyIntent = new Intent(context, AutopsyActivity.class);
                nightAutopsyIntent.putExtra(IS_NIGHT_AUTOPSY_INTENT_EXTRA_FIELD_NAME, true);
                return nightAutopsyIntent;
            case VOTE:
                return new Intent(context, VoteActivity.class);
            case DAY_AUTOPSY:
                Intent dayAutopsyIntent = new Intent(context, AutopsyActivity.class);
                dayAutopsyIntent.putExtra(IS_NIGHT_AUTOPSY_INTENT_EXTRA_FIELD_NAME, false);
                return dayAutopsyIntent;
            case NEW_ROUND:
                RepositoryManager.getInstance().nightActionRepository().newRound();
                return new Intent(context, NightBasisActivity.class);
            case END:
                return new Intent(context, GameEndActivity.class);
        }
        String message = "RoundPhase " + roundPhase + " is not handled.";
        Logger.getInstance().error(RoundPhaseToActivityManager.class, message);
        throw new RuntimeException(message);
    }
}
