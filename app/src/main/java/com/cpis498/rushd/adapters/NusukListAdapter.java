package com.cpis498.rushd.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.cpis498.rushd.HajjDoneActivity;
import com.cpis498.rushd.HajjSecondDayActivity;
import com.cpis498.rushd.R;
import com.cpis498.rushd.UmrahNusukActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NusukListAdapter extends RecyclerView.Adapter<NusukItem> {

    Context context;
    List<String> nusukList;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    String currentNusuk;
    String docid;
    List<Boolean> dayComplete;
    int day = 0;
    boolean allDone=false;
    String[] daysLabels=new String[]{"الأول","الثاني","الثالث" ,"الرابع" ,"الخامس" , "السادس"};

    public NusukListAdapter(Context context, List<String> nusukList, String current, String docid, List<Boolean> check, int day) {
        this.context = context;
        this.nusukList = nusukList;
        this.firestore = FirebaseFirestore.getInstance();
        this.progressDialog = new ProgressDialog(context);
        this.currentNusuk = current;
        this.docid = docid;
        this.dayComplete = check;
        this.day = day;
    }

    @NonNull
    @Override
    public NusukItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_card_nusuk, parent, false);


        return new NusukItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NusukItem holder, int position) {
        int currentIndex = holder.getAdapterPosition();
        String nusk = nusukList.get(currentIndex);



        int index = nusukList.indexOf(currentNusuk);

        holder.text_nusk.setText(nusk);

        if (index > -1)
            if (currentIndex == index) {
                holder.image_check.setVisibility(View.GONE);
                holder.button_complete.setVisibility(View.VISIBLE);
            } else if (currentIndex > index) {
                holder.image_check.setVisibility(View.GONE);
                holder.button_complete.setVisibility(View.GONE);
            } else {
                holder.text_nusk.setTextColor(context.getResources().getColor(R.color.complete_green));
                holder.image_check.setVisibility(View.VISIBLE);
                holder.button_complete.setVisibility(View.GONE);
            }
        else {
            if (dayComplete.get(day) == true) {
                holder.text_nusk.setTextColor(context.getResources().getColor(R.color.complete_green));
                holder.image_check.setVisibility(View.VISIBLE);
                holder.button_complete.setVisibility(View.GONE);
            } else {
                holder.image_check.setVisibility(View.GONE);
                if (currentIndex == 0)
                    holder.button_complete.setVisibility(View.VISIBLE);
                else
                    holder.button_complete.setVisibility(View.GONE);
            }
        }

        holder.button_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setCancelable(false);
                progressDialog.setMessage("الرجاء الانتظار قليلاً");
                progressDialog.show();
                String nextNusuk="",current_day="";


                if (currentIndex < nusukList.size() - 1)
                {
                    nextNusuk = nusukList.get(currentIndex + 1);
                current_day=String.valueOf(day);
                }

                else {
                    dayComplete.set(day, true);
                    current_day=String.valueOf(day+1);
                    //check if ll days are done
                    //check if the journy is umrah
                    boolean isUmrah=context  instanceof UmrahNusukActivity;
                    allDone=true;
                    if(!isUmrah)
                    for(boolean day : dayComplete)
                        allDone=allDone&day;
                    else
                        allDone=dayComplete.get(0) && dayComplete.get(1);
                    if(allDone)
                        nextNusuk = "انتهى من تأدية كل المناسك" ;
                    else
                    nextNusuk = "نهاية اليوم  " + daysLabels[day];






                }

                Map<String, Object> data = new HashMap<>();
                data.put("current_phase", nextNusuk);
                data.put("days_check", dayComplete);
                data.put("current_day", current_day);

                firestore.collection("user_info")
                        .document(docid)
                        .update(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                ((Activity) context).finish();


                                if(allDone)
                                {
                                    Intent intent=new Intent(context, HajjDoneActivity.class);
                                    context.startActivity(intent);
                                }
                                else
                                context.startActivity(((Activity) context).getIntent());
                                Log.d("rushInfo", "updated="+data.get("current_phase"));
                                progressDialog.dismiss();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "حدث خطأ", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return nusukList.size();
    }
}

class NusukItem extends RecyclerView.ViewHolder {

    TextView text_nusk;
    AppCompatButton button_complete;
    ImageView image_check;

    public NusukItem(@NonNull View itemView) {
        super(itemView);
        text_nusk = itemView.findViewById(R.id.text_nusuk);
        button_complete = itemView.findViewById(R.id.button_complete);
        image_check = itemView.findViewById(R.id.image_check);
    }
}
