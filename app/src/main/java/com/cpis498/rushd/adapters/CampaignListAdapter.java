package com.cpis498.rushd.adapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.cpis498.rushd.CampaignPilgrimsActivity;
import com.cpis498.rushd.PilgrimInfoActivity;
import com.cpis498.rushd.R;
import com.cpis498.rushd.models.Pilgrim;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CampaignListAdapter extends RecyclerView.Adapter<CampaignItem> {
    Context context;
    List<Pilgrim> pilgrimList;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    public CampaignListAdapter(Context context, List<Pilgrim> pilgrimList) {
        this.context = context;
        this.pilgrimList = pilgrimList;
        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(context);
    }

    @NonNull
    @Override
    public CampaignItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context)
                .inflate(R.layout.list_card_campaign_item,parent,false);


        return new CampaignItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CampaignItem holder, int position) {
        final int itemPosition=position;
        Pilgrim pilgrim=this.pilgrimList.get(position);
        holder.text_name.setText(pilgrim.getName());
        holder.text_nid.setText(pilgrim.getNid());
        String phase=pilgrim.getCurrent_phase().isEmpty()?"لم يبدأ بعد" :pilgrim.getCurrent_phase();
        holder.text_phase.setText(phase);

        holder.button_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", pilgrim.getPhone(), null));
                context.startActivity(intent);
            }
        });

        holder.button_locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, PilgrimInfoActivity.class);
                intent.putExtra("pilgrim_id",pilgrim.getUid());
                context.startActivity(intent);
            }
        });



        holder.button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              AlertDialog.Builder dialog=new AlertDialog.Builder(context);
              dialog.setMessage("متأكد من حذف العنصر؟");
              dialog.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                      progressDialog.setCancelable(false);
                      progressDialog.setMessage("الرجاء الانتظار قليلاً");
                      firestore.collection("user_info")
                              .document(pilgrim.getDoc_id())
                              .update("campaign","","current_phase","")
                      .addOnSuccessListener(new OnSuccessListener<Void>() {
                          @Override
                          public void onSuccess(Void unused) {
                              progressDialog.dismiss();

                              ((CampaignPilgrimsActivity) context).loadCampaignInfo();
                          }
                      }).addOnFailureListener(new OnFailureListener() {
                          @Override
                          public void onFailure(@NonNull Exception e) {
                              Toast.makeText(context,"حدث خطأ" ,Toast.LENGTH_LONG).show();
                              progressDialog.dismiss();
                          }
                      });
                  }
              });
                dialog.setNegativeButton("لا", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                    }
                });
                dialog.show();
            }
        });






    }

    @Override
    public int getItemCount() {
        return pilgrimList.size();
    }
}

class  CampaignItem extends  RecyclerView.ViewHolder{

    TextView text_name,text_phase,text_nid;
    TextView button_call,button_locate,button_delete;

    public CampaignItem(@NonNull View itemView) {
        super(itemView);
        text_name=itemView.findViewById(R.id.text_name);
        text_phase=itemView.findViewById(R.id.text_phase);
        text_nid=itemView.findViewById(R.id.text_nid);
        button_call=itemView.findViewById(R.id.button_call);
        button_locate=itemView.findViewById(R.id.button_locate);
        button_delete=itemView.findViewById(R.id.button_delete);
    }
}
