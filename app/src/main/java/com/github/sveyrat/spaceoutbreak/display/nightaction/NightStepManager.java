package com.github.sveyrat.spaceoutbreak.display.nightaction;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.sveyrat.spaceoutbreak.domain.Player;
import com.github.sveyrat.spaceoutbreak.domain.constant.Role;

import java.util.ArrayList;
import java.util.List;

public abstract class NightStepManager {

    private int headerTextStringResourceId;

    protected List<Player> selectedPlayers = new ArrayList<>();

    /**
     * A step is fake if no one will wake up to do the actions,
     * but the GM still needs to act as if the role was still being played.
     *
     * This is the case when the player(s) that should play has been paralyzed,
     * or if all the doctors have been mutated in the case of the doctors step.
     */
    protected boolean fakeStep;

    public NightStepManager(boolean fakeStep, int headerTextStringResourceId) {
        this.fakeStep = fakeStep;
        this.headerTextStringResourceId = headerTextStringResourceId;
    }

    /**
     * @return the text to be displayed in the view header for this step
     */
    public final String headerText(Context context) {
        return context.getResources().getString(headerTextStringResourceId);
    }

    /**
     * Handle a click on a player item in the night action view.
     * Toggles the image view state between selected and not selected
     *
     * @param context           the view context
     * @param selectedImageView the selected indicator image view
     * @param player            the player clicked
     */
    public void select(Context context, final ImageView selectedImageView, final Player player) {
        boolean playerSelected = (View.VISIBLE == selectedImageView.getVisibility());
        if (playerSelected) {
            selectedPlayers.remove(player);
            selectedImageView.setVisibility(View.GONE);
            return;
        }
        selectedPlayers.add(player);
        selectedImageView.setVisibility(View.VISIBLE);
    }

    /**
     * Handles the click on a role item in the night action view.
     * Toggles the image view state between selected and not selected.
     * This should be used only by the hacker step manager, the only step where the selected item is a role instead of a player.
     *
     * @param context           the view context
     * @param selectedImageView the selected indicator image view
     * @param role              the role clicked
     */
    public void select(Context context, final ImageView selectedImageView, final Role role) {
        // do nothing : only the hacker step manager has a use for this
    }

    /**
     * Executes the actions required by the step, only if the view state is valid.
     *
     * @param context the view context
     * @return whether the step has been validated or not
     */
    public abstract boolean validateStep(Context context);

    protected final void showErrorToast(Context context, int stringResourceId, String... args) {
        String message = null;
        if (args != null && args.length > 0) {
            message = String.format(context.getResources().getString(stringResourceId), args);
        } else {
            message = context.getResources().getString(stringResourceId);
        }
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * Indications for the user on what to do next, like notify players that are concerned with the step actions.
     *
     * @return a text that is to be displayed on the view after the step is complete.
     */
    public String afterStepText(Context context) {
        return null;
    }

    /**
     * Whether the step requires to select players from the grid.
     * This is usefull for the computer scientist step for instance, where no player is inspected,
     * or for the fake steps.
     * The activity then switches directly to the text view.
     *
     * @return true if the step does not require a player selection, false otherwise
     */
    public boolean autoValidate() {
        return fakeStep;
    }

    public boolean useRoleSelection() {
        return false;
    }
}
