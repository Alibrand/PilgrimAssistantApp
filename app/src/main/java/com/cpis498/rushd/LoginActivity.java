package com.cpis498.rushd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cpis498.rushd.services.FirebaseHelper;
import com.cpis498.rushd.services.StartLocationTracking;


public class LoginActivity extends AppCompatActivity {

    TextView txtCreateAccount;
    EditText text_user_nid,text_password;
    AppCompatButton button_signin;
    FirebaseHelper helper;
    ImageView show_hide_pass;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //initialize variables
        txtCreateAccount=findViewById(R.id.text_create_account);
        text_user_nid =findViewById(R.id.etxt_userid);
        text_password=findViewById(R.id.etxt_password);
        button_signin=findViewById(R.id.button_signin);
        show_hide_pass=findViewById(R.id.btn_show_hide_password);
        helper=new FirebaseHelper(this);

        StartLocationTracking tracking=new StartLocationTracking(this);
        tracking.requestPermissions();

        //check if there is a logged user
        if(helper.isLogged()==true)
            helper.reloginUser();




        show_hide_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(text_password.getTransformationMethod()== PasswordTransformationMethod.getInstance())
                {
                    text_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    show_hide_pass.setImageResource(R.drawable.ic_eye_slash_hide_pass);
                }else
                {
                    text_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    show_hide_pass.setImageResource(R.drawable.ic_eye_visible_pass);
                }

            }
        });

        txtCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start signup activity
                Intent intent=new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);
            }
        });


        button_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save user input
                String userid= text_user_nid.getText().toString();
                String pass=text_password.getText().toString();

                //check data validity
                if(userid.isEmpty()||pass.isEmpty())
                {
                    Toast.makeText(LoginActivity.this,"توجد حقول فارغة" , Toast.LENGTH_LONG).show();
                    return;
                }
                String email=userid+"@rushd.com";
                helper.login(email,pass);


            }
        });
    }


}