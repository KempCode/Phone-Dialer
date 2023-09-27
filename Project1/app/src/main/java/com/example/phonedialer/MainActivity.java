/*
Michael Kemp 22010820

* */
package com.example.phonedialer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.method.DialerKeyListener;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;
import androidx.print.PrintHelper;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private String telephoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Permission will be asked when the dial button is pressed.
        //this.checkCallerPermissions();

        //Declaring TextView after onCreate.
        TextView phoneNumberText = findViewById(R.id.textView);


        //Button 0 when pressed append "0" to EditText and same for the other number buttons.
        this.appendNumberToEditText(findViewById(R.id.but0),"0", phoneNumberText);
        this.appendNumberToEditText(findViewById(R.id.but1),"1", phoneNumberText);
        this.appendNumberToEditText(findViewById(R.id.but2),"2", phoneNumberText);
        this.appendNumberToEditText(findViewById(R.id.but3),"3", phoneNumberText);
        this.appendNumberToEditText(findViewById(R.id.but4),"4", phoneNumberText);
        this.appendNumberToEditText(findViewById(R.id.but5),"5", phoneNumberText);
        this.appendNumberToEditText(findViewById(R.id.but6),"6", phoneNumberText);
        this.appendNumberToEditText(findViewById(R.id.but7),"7", phoneNumberText);
        this.appendNumberToEditText(findViewById(R.id.but8),"8", phoneNumberText);
        this.appendNumberToEditText(findViewById(R.id.but9),"9", phoneNumberText);
        this.appendNumberToEditText(findViewById(R.id.butHash),"#", phoneNumberText);
        this.appendNumberToEditText(findViewById(R.id.butStar),"*", phoneNumberText);

        //Call function
        this.callFunction(phoneNumberText);

        //Delete function
        this.deleteFunction(phoneNumberText);

        //Handle the ACTION_DIAL intent coming from the ADB shell.
        this.handleDIALIntent(phoneNumberText);
    }



    private void deleteFunction(TextView phoneNumberText){
        // Function backspaces the last character in the TextView as long as its not an
        // empty number
        Button deleteButton = findViewById(R.id.butBack);
        deleteButton.setOnClickListener((View v) ->{
            String currentPhoneNumber = phoneNumberText.getText().toString();
            if(currentPhoneNumber.length() > 0){
                //Remove last character, has to be a substring without last character in Java
                // as Strings are immutable!
                String numberWithRemoval = currentPhoneNumber.substring(0,
                        currentPhoneNumber.length() -1);
                phoneNumberText.setText(numberWithRemoval);
            }
            else{
                //if number is empty, empty string
                phoneNumberText.setText("");
            }
        });
    }

    private void callFunction(TextView phoneNumberText){
        Button callButton = findViewById(R.id.butCall);
        //Lambda function to get current text in the TextView and append a number / * / # char
        callButton.setOnClickListener((View v) ->{
            String phoneNumberToDial = phoneNumberText.getText().toString();
            this.telephoneNumber = "tel:" + phoneNumberToDial;
            //Permission will be asked when the dial button is pressed.
            this.checkCallerPermissions();
        });
    }

    private void appendNumberToEditText(Button buttonName, String inputNumber, TextView phoneNumberText){
        // When particular button is pressed append the number to the EditText....
        buttonName.setOnClickListener((View v) ->{
            String currentPhoneNumber = phoneNumberText.getText().toString();
            String newPhoneNumber = currentPhoneNumber + inputNumber;
            phoneNumberText.setText(newPhoneNumber);
        });
    }

    private void checkCallerPermissions(){
        //Check permissions for ACTION_CALL intent
        // Runtime permission as it is dangerous.
        //checkSelfPermission is checking if the permission has already been granted.
        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED){
                // request for permissions directly
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 0);
        }
        else{
            //if user has already selected permissions for this app make the call.
            this.callNumber(this.telephoneNumber);
        }
    }
    private void callNumber(String telephoneNumber){
        // Caller method.
        Intent CallerIntent = new Intent(Intent.ACTION_CALL);
        CallerIntent.setData(Uri.parse(telephoneNumber));
        //Catch errors if necessary
        // Make sure theres a view to handle the intent.
        if(CallerIntent.resolveActivity(getPackageManager()) != null){
            startActivity(CallerIntent);
        }
    }


    //This method overriding code is from class week 3.
    // overriding the Callback when permission has been granted.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //If user grants permission for this app to make a call then do it.
            this.callNumber(this.telephoneNumber);
        }
    }

    private void handleDIALIntent(TextView phoneNumberText){
        //Handle the ACTION_DIAL intent coming from the ADB shell.
        // This method .getData() is deprecated but I tried new methods but to no avail..
        Uri dialerData = getIntent().getData();
        if(dialerData != null && "tel".equals(dialerData.getScheme())){
            String PhoneNumber = dialerData.getSchemeSpecificPart();
            this.telephoneNumber = PhoneNumber;
            phoneNumberText.setText(this.telephoneNumber);
        }
    }

}