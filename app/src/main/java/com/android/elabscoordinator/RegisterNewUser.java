package com.android.elabscoordinator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.elabscoordinator.Model.Student;
import com.android.elabscoordinator.Retrofit.AllStudent;
import com.android.elabscoordinator.Retrofit.Api;
import com.android.elabscoordinator.Retrofit.RegisterNewStudent;
import com.android.elabscoordinator.Retrofit.Students;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterNewUser extends AppCompatActivity   {
    TextInputEditText name;
    TextInputEditText contact_no;
    TextInputEditText roll;
    String mName,mContact,mRoll;
    ProgressDialog singnInprogressDialog;


    Spinner gender;
    Spinner branch;
    Spinner course;
    Spinner year;
    String email;
    String mGender,mBrach,mCourse,mYear;
    Button submit;
    TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_user);
        error = findViewById(R.id.errorText);
        //for gender
        gender = findViewById(R.id.gender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter);
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mGender = adapterView.getItemAtPosition(i).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //for branch

        branch = findViewById(R.id.branch);
        ArrayAdapter<CharSequence> branchAdapter = ArrayAdapter.createFromResource(this,
                R.array.branch, android.R.layout.simple_spinner_item);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branch.setAdapter(branchAdapter);
        branch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mBrach = adapterView.getItemAtPosition(i).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //  for course
        course = findViewById(R.id.course);
        ArrayAdapter<CharSequence> courseAdapter = ArrayAdapter.createFromResource(this,
                R.array.courses, android.R.layout.simple_spinner_item);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        course.setAdapter(courseAdapter);
        course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mCourse = adapterView.getItemAtPosition(i).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //for year
        year = findViewById(R.id.year);
        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this,
                R.array.year, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year.setAdapter(yearAdapter);
        year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mYear = adapterView.getItemAtPosition(i).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
       name = findViewById(R.id.name);
       roll =findViewById(R.id.roll);
       contact_no = findViewById(R.id.contact);
       submit = findViewById(R.id.submit);
       submit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               mRoll = roll.getText().toString();
               mName = name.getText().toString();
               mName = mName.charAt(0)+mName.substring(1);
               mContact = contact_no.getText().toString();
               email = mRoll+"@kiit.ac.in";
               if (isValidResponce())
               {
                   singnInprogressDialog = new ProgressDialog(RegisterNewUser.this);
                   singnInprogressDialog.setMessage("connecting to server  please wait......");
                   singnInprogressDialog.show();
                   retrofit();

               }

           }
       });
    }
    public Boolean isValidResponce()
    {
        if(mBrach.equals("select branch") || mCourse.equals("select subject") || mGender.equals("select gender") || mYear.equals("select year"))
        {

            error.setText("please enter all the field");
            error.setVisibility(View.VISIBLE);
            return false;
        }

        else if (mContact.length() != 10 || mRoll.length() != 7)
        {
            Toast.makeText(this, ""+mCourse.length()+"  "+mRoll.length(), Toast.LENGTH_SHORT).show();

            error.setText("please enter appropiate roll no or contact no");
            error.setVisibility(View.VISIBLE);
            return false;
        }
        else
            return true;

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
        RegisterNewStudent student = new RegisterNewStudent(mRoll,mName,mGender,mBrach,email,mYear,mCourse,mContact);


    Call<RegisterNewStudent> call = api.register(student);
    call.enqueue(new Callback<RegisterNewStudent>() {
        @Override
        public void onResponse(Call<RegisterNewStudent> call, Response<RegisterNewStudent> response) {
            Toast.makeText(RegisterNewUser.this, "success", Toast.LENGTH_SHORT).show();
            singnInprogressDialog.dismiss();
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(RegisterNewUser.this);
            builder.setMessage( mName+"has been register").setTitle("success");
            builder.create();
            builder.show();
            startActivity(new Intent(RegisterNewUser.this,HomeActivity.class));
            finish();
        }

        @Override
        public void onFailure(Call<RegisterNewStudent> call, Throwable t) {
            singnInprogressDialog.dismiss();
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(RegisterNewUser.this);
            builder.setMessage("server error").setTitle("Failure");
            builder.create();
            builder.show();
            startActivity(new Intent(RegisterNewUser.this,HomeActivity.class));
            finish();

        }
    });

    }


}
