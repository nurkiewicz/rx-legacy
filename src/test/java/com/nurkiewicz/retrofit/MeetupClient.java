package com.nurkiewicz.retrofit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class MeetupClient {

	private final Retrofit retrofit;

	public MeetupClient() {
		retrofit = new Retrofit.Builder()
				.baseUrl("https://api.meetup.com/")
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.addConverterFactory(JacksonConverterFactory.create(jsonMapper()))
				.build();
	}

	public MeetupApi build() {
		return retrofit.create(MeetupApi.class);
	}

	private ObjectMapper jsonMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setPropertyNamingStrategy(
				PropertyNamingStrategy.SNAKE_CASE);
		objectMapper.configure(
				DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objectMapper;
	}

}
