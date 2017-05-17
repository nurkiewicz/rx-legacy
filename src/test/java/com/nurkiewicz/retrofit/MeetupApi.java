package com.nurkiewicz.retrofit;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface MeetupApi {

	/**
	 * Oslo:   59.911491, 10.757933
	 * Warsaw: 52.237049, 21.017532
	 * Gdańsk: 54.372158, 18.638306
	 */
	@GET("/2/cities")
	Observable<Cities> listCities(
			@Query("lat") double lat,
			@Query("lon") double lon
	);
}
