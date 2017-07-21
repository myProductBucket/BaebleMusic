package com.baeble.www.baebleapp.model;

import com.google.gson.annotations.SerializedName;


public class ErrorMessage {
    @SerializedName("message")
    String message;

    public ErrorMessage(){}
    public ErrorMessage(String message){this.message = message;}
    public String getMessage(){return message;}
    public void setMessage(String message){this.message = message;}
}
