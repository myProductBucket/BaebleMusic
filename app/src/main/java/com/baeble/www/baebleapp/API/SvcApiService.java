package com.baeble.www.baebleapp.API;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by Zane.H on 10/14/2015.
 */
public class SvcApiService
{
	private static final String BASE_URL = "http://xxx.com/xxx.svc/json";

	private static String username = "android";
	private static String password = " ";
	private static SvcApiEndPoint svcUserIdService;

	static
	{
		setupSvcClient();
	}

	public static SvcApiEndPoint getUserIdEndPoint()
	{
		return svcUserIdService;
	}

	public static void setupSvcClient()
	{
		Gson gson = new GsonBuilder().setDateFormat("yyyy'-'MM'-'dd").create();
		RestAdapter restAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL).setEndpoint(BASE_URL).setConverter(new GsonConverter(gson)).setRequestInterceptor(new RequestInterceptor()
		{
			@Override
			public void intercept(RequestFacade request)
			{
				request.addHeader("Accept", "application/json");
			}
		}).build();
		svcUserIdService = restAdapter.create(SvcApiEndPoint.class);
	}
}
