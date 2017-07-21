package com.baeble.www.baebleapp.API;

import android.util.Log;

import com.baeble.www.baebleapp.model.ErrorMessage;
import com.baeble.www.baebleapp.model.SvcError;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by ZaneH_000 on 10/14/2015.
 */
public abstract class SvcApiRestCallback<T> implements Callback<T> {
    public abstract void failure(SvcError svcError);

    @Override
    public void failure(RetrofitError error) {
        try
        {
            Log.d("SvcApiRestCallback", "restError :" + error.toString());
            SvcError restError = (SvcError)error.getBodyAs(SvcError.class);
            if(restError != null) {
                failure(restError);
            } else
            {
                failure(new SvcError(new ErrorMessage(error.getMessage())));
            }
        }
        catch (Exception ex){
            failure(new SvcError(new ErrorMessage(ex.getMessage())));
        }
    }

}