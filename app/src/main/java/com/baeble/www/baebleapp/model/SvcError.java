package com.baeble.www.baebleapp.model;

import com.google.gson.annotations.SerializedName;

public class SvcError {
    @SerializedName("error")
    ErrorMessage errors;
    public SvcError(ErrorMessage errors){this.errors = errors;}
    public SvcError() {}
    public ErrorMessage getErrors(){return errors;}
}

