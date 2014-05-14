package com.sckftr.android.securephoto.db;

public abstract class BaseModel implements DbModel {

    private String originalContentId;

    public String getOriginalContentId() {
        return originalContentId;
    }

    public void setOriginalContentId(String originalContentId) {
        this.originalContentId = originalContentId;
    }


}
