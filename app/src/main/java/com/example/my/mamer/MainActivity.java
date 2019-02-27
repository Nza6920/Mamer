package com.example.my.mamer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         String a="成功";
        boolean m=isPhoneNumberValid("446681800");
        if(m){
            Log.e("Tag",a);
        }

    }
    public boolean isPhoneNumberValid(String phoneNum) {

        PhoneNumberUtil phoneNumUtil = PhoneNumberUtil.getInstance();
        String countryCode = Locale.getDefault().getCountry();

        try {
            Phonenumber.PhoneNumber numberProto = phoneNumUtil.parse(phoneNum, countryCode);
            return phoneNumUtil.isValidNumber(numberProto);
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }
        return false;
    }
}
