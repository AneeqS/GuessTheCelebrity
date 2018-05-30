package com.aneeqshah.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebUrls = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    int chosenCeleb = 0;
    ImageView imageView;
    int locationCorrectAns = 0;
    String[] ans = new String[4];
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public void celebChosen(View view){

        if(view.getTag().toString().equals(Integer.toString(locationCorrectAns))){

            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(getApplicationContext(), "Wrong!, It was " + celebNames.get(chosenCeleb), Toast.LENGTH_SHORT).show();
        }
        newQuestion();
    }



    public class downloadImageTask extends AsyncTask<String, Void, Bitmap>{


        @Override
        protected Bitmap doInBackground(String... urls) {
            try {

                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();
                InputStream inputStream = connection.getInputStream();

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                return bitmap;

            } catch (Exception e) {

                e.printStackTrace();
            }
            return null;
        }
    }

    public class downloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url = null;
            HttpURLConnection connection = null;

            try{

                url = new URL(urls[0]);

                connection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();

                while(data != -1){
                    char curr = (char) data;
                    result += curr;
                    data = reader.read();
                }

                return result;


            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView2);
        button0 = findViewById(R.id.button);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        downloadTask task = new downloadTask();
        String result = null;

        try {

            result = task.execute("http://www.posh24.se/kandisar").get();

            String[] splitResult = result.split("<div class=\"sidebarContainer\">");

            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while(m.find()){

                celebUrls.add(m.group(1));
            }


            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);

            while(m.find()){

                celebNames.add(m.group(1));
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
        newQuestion();
    }

    public void newQuestion() {
        Random random = new Random();
        chosenCeleb = random.nextInt(celebUrls.size());

        downloadImageTask imageTask = new downloadImageTask();
        Bitmap image;

        try {

            image = imageTask.execute(celebUrls.get(chosenCeleb)).get();
            imageView.setImageBitmap(image);

            locationCorrectAns = random.nextInt(4);
            int inc;

            for(int i = 0; i < 4; i++){

                if(i == locationCorrectAns){
                    ans[i] = celebNames.get(chosenCeleb);

                }else{

                    inc = random.nextInt(celebUrls.size());

                    while(inc == chosenCeleb){
                        inc = random.nextInt(celebUrls.size());
                    }
                    ans[i] = celebNames.get(inc);
                }
            }

            button0.setText(ans[0]);
            button1.setText(ans[1]);
            button2.setText(ans[2]);
            button3.setText(ans[3]);

        } catch (Exception e) {

            e.printStackTrace();
        }



    }

}
