package co.edu.udea.compumovil.lab4gr8.weather.l4g8_weather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import co.edu.udea.compumovil.lab4gr8.weather.l4g8_weather.POJO.Model;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity {

    TextView city, status, humidity, pressure, temperature;
    ImageView icon;
    String url = "http://api.openweathermap.org/data/2.5";
    String[] cities;
    AutoCompleteTextView cityField;

    AsyncTask<String, Void, Bitmap> asyncTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityField = (AutoCompleteTextView) findViewById(R.id.cityField);
        cities = getResources().getStringArray(R.array.capitales);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cities);
        cityField.setAdapter(adapter);
    }

    public void findCityInfo(View view){

        String citySearch = String.valueOf(cityField.getText());
        city = (TextView) findViewById(R.id.txt_city);
        status = (TextView) findViewById(R.id.txt_status);
        humidity = (TextView) findViewById(R.id.txt_humidity);
        pressure = (TextView) findViewById(R.id.txt_press);
        temperature = (TextView) findViewById(R.id.txt_temp);
        icon = (ImageView) findViewById(R.id.image);
        //making object of RestAdapter
        RestAdapter adapter = new RestAdapter.Builder().setEndpoint(url).build();

        //Creating Rest Services
        RestInterface restInterface = adapter.create(RestInterface.class);

        //Calling method to get whether report
        restInterface.getWheatherReport(citySearch, "0d1d8586ed0681a308d8f3fa6b9abf9b", new Callback<Model>() {

            @Override
            public void success(Model model, Response response) {
                Double temper = model.getMain().getTemp() - (273.15);
                temper = this.round(temper, 2);
                city.setText(model.getName());
                status.setText(model.getWeather().get(0).getDescription());
                humidity.setText(model.getMain().getHumidity().toString() + "%");
                pressure.setText(model.getMain().getPressure().toString() + " hPa");
                temperature.setText(temper.toString() + " °C");

                Uri uri = Uri.parse("http://openweathermap.org/img/w/"
                        + model.getWeather().get(0).getIcon() + ".png");

                asyncTask = new AsyncTask<String, Void, Bitmap>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected Bitmap doInBackground(String... params) {
                        URL imageUrl;
                        Bitmap imagen = null;
                        try {
                            imageUrl = new URL(params[0]);
                            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                            conn.connect();
                            imagen = BitmapFactory.decodeStream(conn.getInputStream());
                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(), "Error cargando la imagen: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        return imagen;
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute(bitmap);
                        icon.setImageBitmap(bitmap);
                    }
                };

                asyncTask.execute(uri.toString());
            }

            @Override
            public void failure(RetrofitError error) {

                String merror = error.getMessage();
            }

            public double round(double value, int places) {
                if (places < 0) throw new IllegalArgumentException();

                long factor = (long) Math.pow(10, places);
                value = value * factor;
                long tmp = Math.round(value);
                return (double) tmp / factor;
            }
        });
    }
}
