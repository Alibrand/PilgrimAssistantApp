package com.cpis498.rushd.services;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.cpis498.rushd.LoginActivity;
import com.cpis498.rushd.MainActivity;
import com.cpis498.rushd.OrganizerActivity;
import com.cpis498.rushd.models.Pilgrim;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

public class FirebaseHelper {
    Context mContext;
     FirebaseAuth mAuth;
    FirebaseFirestore mFireStore;
    ProgressDialog progressDialog;
    public static Pilgrim pilgrim;
    MySharedPreferences preferences;
     StartLocationTracking locationTracking;

    public FirebaseHelper(Context mContext) {
        this.mContext = mContext;
        mAuth = FirebaseAuth.getInstance();
        mFireStore = FirebaseFirestore.getInstance();
        preferences=new MySharedPreferences(mContext);

         locationTracking =new StartLocationTracking(mContext);
    }

    public FirebaseHelper() {

        mAuth = FirebaseAuth.getInstance();
        mFireStore = FirebaseFirestore.getInstance();
         }

    public Pilgrim getCurrentUser(){
        return this.pilgrim;
    }

    public void registerNewUser(Pilgrim user) {
        showDialog("الرجاء الانتظار قليلاً");
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, "تم التسجيل بنجاح", Toast.LENGTH_SHORT).show();
                           FirebaseUser newUser=mAuth.getCurrentUser();
                            user.setUid(newUser.getUid());
                            //save user info to firebase store
                            mFireStore.collection("user_info")
                                    .add(user.toMap())
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    //after a successful signup
                                    pilgrim=user;
                                    //set logged as true
                                    setLogged(true);
                                    //save account type
                                    preferences.setAccountType(pilgrim.getAccount_type());
                                    //check user account type
                                    checkUserType();


                                    Toast.makeText(mContext, "أهلا وسهلا بك" , Toast.LENGTH_LONG).show();
                                    dismissDialog();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    checkException(task.getException());
                                }
                            });



                        } else {
                            checkException(task.getException());
                             }

                    }
                });
    }

    public void   login(String email, String password) {
        showDialog("الرجاء الانتظار قليلاً");
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            //after a successful signin

                            mFireStore.collection("user_info")
                                    .whereEqualTo("uid",user.getUid())
                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    //get the first result
                                    DocumentSnapshot doc=queryDocumentSnapshots.getDocuments().get(0);
                                     pilgrim =new Pilgrim(doc.getData());
                                    //set logged as true
                                    setLogged(true);
                                    //save account type
                                    preferences.setAccountType(pilgrim.getAccount_type());
                                     //check user account type
                                    checkUserType();


                                    Toast.makeText(mContext, "أهلا وسهلا بك" , Toast.LENGTH_LONG).show();
                                    dismissDialog();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    checkException(e);
                                }
                            });


                        } else {
                            checkException(task.getException());

                        }

                    }
                });
    }


    private void setLogged(boolean value){
        preferences.setIsLogged(value);

    }

    public boolean isLogged(){
        return preferences.getIsLogged();

    }


    public  void updatePassword(String oldPass,String newPass)
    {
        showDialog("الرجاء الانتظار قليلاً");
         FirebaseUser currentUser=mAuth.getCurrentUser();
        final String email = currentUser.getEmail();

        AuthCredential credential = EmailAuthProvider.getCredential(email,oldPass);

        currentUser.reauthenticate(credential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        currentUser.updatePassword(newPass)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(mContext, "تم تعديل كلمة المرور بنجاح" , Toast.LENGTH_LONG).show();
                                        dismissDialog();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                checkException(e);
                                dismissDialog();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                checkException(e);
                dismissDialog();
            }
        });

    }







    public void reloginUser() {
        showDialog("الرجاء الانتظار قليلاً");
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String uid= currentUser.getUid();
            mFireStore.collection("user_info")
                    .whereEqualTo("uid",uid)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    //get the first result
                    DocumentSnapshot doc=queryDocumentSnapshots.getDocuments().get(0);
                    pilgrim =new Pilgrim(doc.getData());

                    checkUserType();
                    dismissDialog();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    checkException(e);
                }
            });
            //subscribing in a topic
            FirebaseMessaging.getInstance().subscribeToTopic("all");


        }

    }


    public   String getLoggedUserId(){
        return mAuth.getCurrentUser().getUid();
    }


    private void checkUserType(){
        preferences.setString("name",pilgrim.getName());
        //check user account type
        if(pilgrim.getAccount_type().equals("حاج")) {
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("campaign_id",pilgrim.getCampaign());
            mContext.startActivity(intent);
        }
        else{
            Intent intent = new Intent(mContext, OrganizerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mContext.startActivity(intent);

        }

        locationTracking.startUpdates();
    }

    public void logout() {
        //set logged as false
        setLogged(false);
        mAuth.signOut();

        if(locationTracking!=null)
        locationTracking.stopUpdates();
        locationTracking=null;
        Intent intent=new Intent(mContext, LoginActivity.class);
        mContext.startActivity(intent);
        ((Activity) mContext).finish();
    }




    //dialog functions

    private void showDialog(String message) {
        if(progressDialog==null)
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void dismissDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void checkException(Exception taskException){
        Log.d("error",taskException.getMessage());
        try {
            throw taskException;
        } catch(FirebaseAuthWeakPasswordException e) {
            Toast.makeText(mContext,"كلمة المرور ضعيفة يرجى اختيار كلمة اقوى", Toast.LENGTH_LONG).show();

        } catch(FirebaseAuthInvalidCredentialsException e) {
            Toast.makeText(mContext,"كلمة المرور خطأ", Toast.LENGTH_LONG).show();

        }
        catch( FirebaseAuthInvalidUserException e) {
            Toast.makeText(mContext,"رقم البطاقة غير موجود في النظام", Toast.LENGTH_LONG).show();
        }
        catch( FirebaseAuthUserCollisionException e) {
            Toast.makeText(mContext,"يوجد حساب آخر يستخدم رقم البطاقة ذاته", Toast.LENGTH_LONG).show();
        }
      catch(Exception e) {
            Toast.makeText(mContext,"حدث خطأ", Toast.LENGTH_LONG).show();

        }
        dismissDialog();
    }
}
