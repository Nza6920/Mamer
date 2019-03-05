package com.example.my.mamer;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        所需要申请的权限
        String[] perms={Manifest.permission.ACCESS_WIFI_STATE};
//        检查自己是否得到该权限
        if (EasyPermissions.hasPermissions(this,perms)){
            Log.e("Tag","已获得权限");
        }else {
            EasyPermissions.requestPermissions(this,"必要权限",0,perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//将结果转发到easyPermission
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }


    @Override
    public void onPermissionsGranted(int requestCode,List<String> perms) {
        Log.i("TAG","获取成功的权限:");
    }

    @Override
    public void onPermissionsDenied(int requestCode,List<String> perms) {
        Log.i("TAG","获取失败的权限");
    }
}

