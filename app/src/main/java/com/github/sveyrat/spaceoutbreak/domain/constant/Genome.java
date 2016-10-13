package com.github.sveyrat.spaceoutbreak.domain.constant;

import com.github.sveyrat.spaceoutbreak.R;

public enum Genome {
    NORMAL(R.string.common_genome_normal),
    RESISTANT(R.string.common_genome_resistant),
    HOST(R.string.common_genome_host);

    private int labelResourcesId;

    private Genome(int labelResourcesId){
        this.labelResourcesId = labelResourcesId;
    }
    public int getLabelResourcesId(){
        return labelResourcesId;
    }
}
