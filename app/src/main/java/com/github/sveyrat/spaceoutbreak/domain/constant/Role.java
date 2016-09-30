package com.github.sveyrat.spaceoutbreak.domain.constant;

import com.github.sveyrat.spaceoutbreak.R;

public enum Role {
    ASTRONAUT(R.string.role_astronaut_name),
    BASE_MUTANT(R.string.role_base_mutant_name),
    DOCTOR(R.string.role_doctor_name),
    PSYCHOLOGIST(R.string.role_psychologist_name),
    GENETICIST(R.string.role_geneticist_name),
    COMPUTER_SCIENTIST(R.string.role_computer_scientist_name),
    SPY(R.string.role_spy_name),
    HACKER(R.string.role_hacker_name),
    FANATIC(R.string.role_fanatic_name);

    private int labelResourcesId;

    private Role(int labelResourcesId){
        this.labelResourcesId = labelResourcesId;
    }
    public int getLabelResourcesId(){
        return labelResourcesId;
    }
}
