package com.android.elabscoordinator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
import com.android.elabscoordinator.Adapter.StudentAdapter;
import com.android.elabscoordinator.Model.Student;
import com.android.elabscoordinator.Retrofit.AllStudent;
import com.android.elabscoordinator.Retrofit.Api;
import com.android.elabscoordinator.Retrofit.Students;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements BeaconConsumer {
    private BeaconManager beaconManager;
    RecyclerView recyclerView;
    String subjectCode = "1";
    ArrayList<Student> studentList;
    StudentAdapter adapter;
    FloatingActionButton fab;
    ProgressBar progressBar;
    View progresslayout;
    TextView progressText;
    LottieAnimationView progressAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         SharedPreferences pref =getSharedPreferences("subject_code", Context.MODE_PRIVATE);
         subjectCode = pref.getString("code","null");
        Toast.makeText(this, subjectCode, Toast.LENGTH_SHORT).show();

        progresslayout = findViewById(R.id.progress);
        fab = findViewById(R.id.floatingActionButton);
        progressBar = findViewById(R.id.progress_circular);
        progressText = progresslayout.findViewById(R.id.errorText);
        progressAnimation = progresslayout.findViewById(R.id.lottieAnimationView);
        progressAnimation.setAnimation(R.raw.progress);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beaconManager.unbind(MainActivity.this);
                beaconManager.disableForegroundServiceScanning();

               // progressBar.setIndeterminate(true);
                //progressBar.setVisibility(View.VISIBLE);
                progresslayout.setVisibility(View.VISIBLE);
                progressAnimation.playAnimation();

                retrofit();

            }
        });
        studentList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewStudent);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
         adapter = new StudentAdapter(studentList,this);
        recyclerView.setAdapter(adapter);
        beaconInit();

    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.removeAllRangeNotifiers();
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    for (Beacon beacon : beacons)
                    {  if (beacon.getId2().toString().equals(subjectCode))
                        {
                            Toast.makeText(MainActivity.this, ""+beacon.getId1(), Toast.LENGTH_SHORT).show();
                            studentList.add(new Student(getAuthKey(beacon.getId1()),decode(beacon.getId1())));
                            HashSet<Student> hs = new HashSet<>();
                            hs.addAll(studentList);
                            studentList.clear();
                            studentList.addAll(hs);
                          //  Toast.makeText(MainActivity.this, ""+decode(beacon.getId1()), Toast.LENGTH_SHORT).show();

                            adapter.notifyDataSetChanged();
                        }


                    }

                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {    }


    }
    public String decode(Identifier beacon)
    {
        String inputString = beacon.toString();
        String s = inputString.substring(29, 36);
        return s ;


    }
    public String getAuthKey(Identifier beacon)
    {
        String inputString = beacon.toString();
        final String authKey = inputString.substring(0, 8)
                +inputString.substring(9, 13)
                + inputString.substring(14, 18)
                + inputString.substring(19, 23)
                + inputString.substring(24, 28);
        return authKey;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    public void beaconInit()
    {
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);
    }
    public void retrofit()
    {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request orignal = chain.request();
                Request request = orignal.newBuilder()
                        .header("Content-Type", "application/json")
                        .method(orignal.method(), orignal.body())
                        .build();

                return chain.proceed(request);
            }
        });
        OkHttpClient client = httpClient.build();
        final String API_ROOT_URL = "https://elabsattendanceapp.herokuapp.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .client(okHttpClient)
                .build();
        Api api = retrofit.create(Api.class);
        ArrayList<Students> list = new ArrayList<>();
        for (Student s :studentList)
        {
           list.add(new Students(s.getAuthKey(),s.getRoll()));

        }
        Call<AllStudent> call = api.request(new AllStudent(list));
        call.enqueue(new Callback<AllStudent>() {
            @Override
            public void onResponse(Call<AllStudent> call, Response<AllStudent> response) {
                progressAnimation.setAnimation(R.raw.done);
                progressAnimation.playAnimation();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        startActivity(new Intent(MainActivity.this,HomeActivity.class));
                    }
                }, 3000);
                progressText.setText("success");
                Toast.makeText(MainActivity.this, ""+response.body().toString(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<AllStudent> call, Throwable t) {
                progressAnimation.setAnimation(R.raw.error);
                progressAnimation.playAnimation();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        startActivity(new Intent(MainActivity.this,HomeActivity.class));
                    }
                }, 3000);
                progressText.setText("error please try after some time");
                Toast.makeText(MainActivity.this, ""+t.toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }


}
