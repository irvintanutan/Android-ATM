package com.novigosolutions.certiscisco.objects;

/**
 * Created by dhanrajk on 23-06-17.
 */

public class ManualEntryResult {
    private String data;
    private boolean isserial;
    private int pos;

    public ManualEntryResult(String data, boolean isserial,int pos) {
        this.data = data;
        this.isserial = isserial;
        this.pos=pos;

    }

    public String getData() {
        return data;
    }

    public boolean isserial() {
        return isserial;
    }
    public int getPos() {
        return pos;
    }
}
