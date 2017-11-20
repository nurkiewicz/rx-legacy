package com.nurkiewicz.retrofit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class MeetupClient {

	private final Retrofit retrofit;

	public MeetupClient() {
		retrofit = new Retrofit.Builder()
				.baseUrl("https://api.meetup.com/")
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
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

	public static void main(String[] args) {
		MeetupApi client = new MeetupClient().build();
		client
				.listCities(52.237049, 21.017532)
				.flatMapIterable(Cities::getResults)
				.toList()
				.blockingGet()
				.forEach(System.out::println);
	}

}
