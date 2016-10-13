package com.github.sveyrat.spaceoutbreak.dao.dto;

import com.github.sveyrat.spaceoutbreak.domain.NightAction;
import com.github.sveyrat.spaceoutbreak.domain.constant.Role;

import java.util.List;

public class SpyInspectionResult {

    private boolean mutated = false;
    private boolean paralyzed = false;
    private boolean healed = false;
    private boolean inspectedByPsychologist = false;
    private boolean inspectedByGeneticist = false;

    public SpyInspectionResult() {
    }

    public SpyInspectionResult(List<NightAction> actions) {
        for (NightAction action : actions) {
            switch (action.getType()) {
                case MUTATE:
                    mutated = true;
                    break;
                case PARALYSE:
                    paralyzed = true;
                    break;
                case HEAL:
                    healed = true;
                    break;
                case INSPECT:
                    if (Role.PSYCHOLOGIST == action.getActingPlayerRole()) {
                        inspectedByPsychologist = true;
                    }
                    if (Role.GENETICIST == action.getActingPlayerRole()) {
                        inspectedByGeneticist = true;
                    }
                    break;
            }
        }
    }

    public boolean isMutated() {
        return mutated;
    }

    public boolean isParalyzed() {
        return paralyzed;
    }

    public boolean isHealed() {
        return healed;
    }

    public boolean isInspectedByPsychologist() {
        return inspectedByPsychologist;
    }

    public boolean isInspectedByGeneticist() {
        return inspectedByGeneticist;
    }
}
