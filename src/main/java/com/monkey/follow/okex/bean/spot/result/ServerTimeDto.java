package com.monkey.follow.okex.bean.spot.result;

public class ServerTimeDto {
    private Long epoch;
    private String iso;

    public Long getEpoch() {
        return epoch;
    }

    public void setEpoch(Long epoch) {
        this.epoch = epoch;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }
}
