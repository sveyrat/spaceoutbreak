package com.github.sveyrat.spaceoutbreak.display.nightaction;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.sveyrat.spaceoutbreak.R;
import com.github.sveyrat.spaceoutbreak.domain.Player;

public abstract class StepManager {

    /**
     * @return the text to be displayed in the view header for this step
     */
    public abstract String headerText(Context context);

    /**
     * Handle a click on a player item in the night action view.
     * Toggles the image view state between selected and not selected
     *
     * @param context the view context
     * @param selectedImageView the selected indicator image view
     * @param player the player clicked
     */
    public abstract void select(Context context, final ImageView selectedImageView, final Player player);

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

    protected void showErrorToast(Context context, int stringResourceId) {
        Toast toast = Toast.makeText(context, context.getResources().getString(stringResourceId), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
