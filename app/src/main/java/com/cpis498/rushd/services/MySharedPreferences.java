package com.cpis498.rushd.services;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

class MySharedPreferences {
    SharedPreferences sharedPref;
    Context mContext;
    private static final String PREFERNCE_NAME="myPref";

    public MySharedPreferences(Context mContext) {
        this.mContext = mContext;
        sharedPref = ((Activity) mContext).getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE)  ;

    }

    public void setIsLogged(boolean value){
        //set is logged to true
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("isLogged", value);
        editor.apply();
    }

    public boolean getIsLogged(){
        return sharedPref.getBoolean("isLogged",false);
    }

    public void setAccountType(String type){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("account_type", type);
        editor.apply();
    }

    public  void setString(String name,String value){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(name, value);
        editor.apply();
    }

    public String getString(String name){
        return sharedPref.getString(name,"");
    }

    public String getAccountType(){
        return sharedPref.getString("account_type","");
    }



}
