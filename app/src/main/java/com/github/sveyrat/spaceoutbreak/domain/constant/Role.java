package com.github.sveyrat.spaceoutbreak.domain.constant;

import com.github.sveyrat.spaceoutbreak.R;

public enum Role {
    ASTRONAUT(R.string.role_astronaut_name, R.mipmap.astronaut),
    BASE_MUTANT(R.string.role_base_mutant_name, R.mipmap.base_mutant),
    DOCTOR(R.string.role_doctor_name, R.mipmap.doctor),
    PSYCHOLOGIST(R.string.role_psychologist_name, R.mipmap.psychologist),
    GENETICIST(R.string.role_geneticist_name, R.mipmap.geneticist),
    COMPUTER_SCIENTIST(R.string.role_computer_scientist_name, R.mipmap.computer_scientist),
    SPY(R.string.role_spy_name, R.mipmap.spy),
    HACKER(R.string.role_hacker_name, R.mipmap.hacker),
    FANATIC(R.string.role_fanatic_name, R.mipmap.fanatic);

    private int labelResourceId;
    private int imageResourceId;

    private Role(int labelResourceId, int imageResourceId) {
        this.labelResourceId = labelResourceId;
        this.imageResourceId = imageResourceId;
    }

    public int getLabelResourceId() {
        return labelResourceId;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }


}
