package com.cpis498.rushd.services;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cpis498.rushd.ContactMyCampaignActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendSOS {

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAA61MMZDE:APA91bEEm3AU4GbuX5vK86v3XlgtiEgIZ-Jkk3xFEByE4Sn7SpEMnQ1rVenbO_A9ZXLLHI-y5cpbdie0Mt29WInO6G0mRET_Ta9doxbV0xP5QUiJoz2D6KK922eGK5qfq__sC-AupdBh";
    final private String contentType = "application/json";
    final String TAG = "fcm";
    Context context;
    MySharedPreferences preferences;
    ProgressDialog progressDialog;
     FirebaseHelper helper;

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC="/topics/all";

    public SendSOS(Context context)
    {
        helper=new FirebaseHelper();
        this.context=context;
        preferences=new MySharedPreferences(this.context);
        progressDialog=new ProgressDialog(context);
    }


    public void send(String organizer_id){
        progressDialog.setCancelable(false);
        progressDialog.setMessage("جاري إرسال النداء الى المنظم");
        progressDialog.show();

        String name=preferences.getString("name");
        NOTIFICATION_TITLE="إنذار أنا تائه";
        NOTIFICATION_MESSAGE="نداء طلب مساعدة من " +name;

        String uid=helper.getCurrentUser().getUid();
        Log.i(TAG, helper.mAuth.getCurrentUser().getEmail());


        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        JSONObject data = new JSONObject();

        Log.i(TAG, "sending " );
        try {
            data.put("organizer_id",organizer_id);
            data.put("pilgrim_id",uid);
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("body", NOTIFICATION_MESSAGE);
            notification.put("data", data);
            notification.put("to", TOPIC);
            notification.put("notification", notifcationBody);
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage() );
            Toast.makeText(context,"حدث خطأ.. يرجى المحاولة ثانية" ,Toast.LENGTH_LONG).show();
progressDialog.dismiss();
        }
        sendNotification(notification);

    }

    private void sendNotification(JSONObject notification) {
        Log.i(TAG, "onResponse: " + notification.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                        Toast.makeText(context,"تم إرسال النداء بنجاح...سيتواصل معك المنظم حالاً" ,Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();

                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.i(TAG, "onErrorResponse: "+error.getMessage());
                        Toast.makeText(context,"حدث خطأ.. يرجى المحاولة ثانية" ,Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }){
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(context.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}
