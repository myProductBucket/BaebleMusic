package com.baeble.www.baebleapp.listener;

/**
 * Created by ZaneH_000 on 10/14/2015.
 */
public interface SvcModelListener<DataModel> {
    public void success(Boolean isSuccess, DataModel data);
}