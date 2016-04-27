package co.edu.udea.compumovil.lab4gr8.weather.l4g8_weather;

import co.edu.udea.compumovil.lab4gr8.weather.l4g8_weather.POJO.Model;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by SantiagoRomero
 */
public interface RestInterface {

    @GET("/weather")
    void getWheatherReport(@Query("q") String city, @Query("appid") String appId, Callback<Model>cb);

}
