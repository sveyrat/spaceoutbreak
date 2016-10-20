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

public abstract class StepManager {

    private int headerTextStringResourceId;

    protected List<Player> selectedPlayers = new ArrayList<>();

    public StepManager(int headerTextStringResourceId) {
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

    /**
     * @return the step manager for the step following this one
     */
    public abstract StepManager nextStep();

    protected final void showErrorToast(Context context, int stringResourceId) {
        Toast toast = Toast.makeText(context, context.getResources().getString(stringResourceId), Toast.LENGTH_SHORT);
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
     * Whether the step requires to select players from the grid. This is usefull for the computer scientist step for instance, where no player is inspected.
     * The activity then switches directly to the text view.
     *
     * @return true if the step does not require a player selection, false otherwise
     */
    public boolean autoValidate() {
        return false;
    }

    public boolean useRoleSelection() {
        return false;
    }

}
