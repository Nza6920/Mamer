package com.example.my.mamer;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //        所需要申请的权限
        String[] perms={Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        checkPermission(perms);
    }

    private void checkPermission(String[] perms){
        //        检查自己是否得到该权限

        if (EasyPermissions.hasPermissions(this,perms)){
            Log.e("Tag","已获得权限");

        } else {
            EasyPermissions.requestPermissions(this,"必要权限",0,perms);

        }
    }
//    请求权限回调
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
        Toast.makeText(this,"该功能需要授权方能使用",Toast.LENGTH_SHORT).show();
        Log.i("TAG","获取失败的权限");
    }
}

