package com.nurkiewicz.retrofit;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MeetupApi {

	/**
	 * Oslo:   59.911491, 10.757933
	 * Warsaw: 52.237049, 21.017532
	 * Sopot:  54.441581, 18.560096
	 */
	@GET("/2/cities")
	Flowable<Cities> listCities(
			@Query("lat") double lat,
			@Query("lon") double lon
	);
}
