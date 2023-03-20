package com.cpis498.rushd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cpis498.rushd.models.Pilgrim;
import com.cpis498.rushd.services.FirebaseHelper;

public class SignupActivity extends AppCompatActivity {

    String[] accountsTypes={"حاج","منظم حملات"};
    Spinner spinner_types;
    TextView text_signin;
    EditText text_user_nid, text_username, text_password, text_confirm_password, text_phone;
    AppCompatButton button_signup;
    FirebaseHelper helper;
    ImageView show_hide_pass,show_hide_passconf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //initialize variables
        spinner_types=findViewById(R.id.spinner_account_type);
        text_signin=findViewById(R.id.text_signin);
        text_user_nid =findViewById(R.id.etxt_userid);
        text_username =findViewById(R.id.etxt_username);
        text_phone =findViewById(R.id.etxt_phone);
        text_password =findViewById(R.id.etxt_password);
        text_confirm_password =findViewById(R.id.etxt_confirm_password);
        button_signup=findViewById(R.id.button_signup);
        show_hide_pass=findViewById(R.id.btn_show_hide_password);
        show_hide_passconf=findViewById(R.id.btn_show_hide_confirm_password);
        helper=new FirebaseHelper(this);


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

        show_hide_passconf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(text_confirm_password.getTransformationMethod()== PasswordTransformationMethod.getInstance())
                {
                    text_confirm_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    show_hide_passconf.setImageResource(R.drawable.ic_eye_slash_hide_pass);
                }else
                {
                    text_confirm_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    show_hide_passconf.setImageResource(R.drawable.ic_eye_visible_pass);
                }

            }
        });

        //set on click
        text_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });

        button_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get user data from text fields
                String nid= text_user_nid.getText().toString();
                String email=nid+"@rushd.com";
                String name= text_username.getText().toString();
                String pass= text_password.getText().toString();
                String passconfirm= text_confirm_password.getText().toString();
                String phone=text_phone.getText().toString();
                String accountType=spinner_types.getSelectedItem().toString();

                //check if any is empty
                if(nid.isEmpty()||name.isEmpty()||pass.isEmpty()||passconfirm.isEmpty()||phone.isEmpty())
                {
                    Toast.makeText(SignupActivity.this,"توجد حقول فارغة",Toast.LENGTH_LONG).show();
                    return;
                }

                //check password validity
                if(!pass.equals(passconfirm))
                {
                    Toast.makeText(SignupActivity.this,"كلمات المرور غير متطابقة",Toast.LENGTH_LONG).show();
                    return;
                }

                //if all is ok so signup
                Pilgrim newuser=new Pilgrim();
                newuser.setName(name);
                newuser.setEmail(email);
                newuser.setNid(nid);
                newuser.setAccount_type(accountType);
                newuser.setPassword(pass);
                newuser.setPhone(phone);

                helper.registerNewUser(newuser);


            }
        });

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(SignupActivity.this,
                android.R.layout.simple_spinner_item,accountsTypes);
        spinner_types.setAdapter(adapter);
    }
}