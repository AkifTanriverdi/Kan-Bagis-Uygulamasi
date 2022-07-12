package com.akif.blooddonationapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akif.blooddonationapp.R;
import com.akif.blooddonationapp.model.user;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class  userAdapter extends RecyclerView.Adapter<userAdapter.ViewHolder>{
    private Context context;
    private List<user> userList;
    public userAdapter(Context context, List<user> userList) {
        this.context = context;
        this.userList = userList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(
                R.layout.user_displayed_layout,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final user user=userList.get(position);
        holder.type.setText(user.getType());
        holder.userEmail.setText(user.getEmail());
        holder.userName.setText(user.getName());
        holder.bloodgroup.setText(user.getBloodgroup());
    }
    @Override
    public int getItemCount() {
        return userList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView userProfileimage;
        public TextView type,userName,userEmail,bloodgroup,usercity;
      //  public Button emailNow;
        public ViewHolder(@NonNull  View itemView) {
            super(itemView);
            userProfileimage=itemView.findViewById(R.id.userProfileimage);
            type=itemView.findViewById(R.id.type);
            userName=itemView.findViewById(R.id.userName);
            bloodgroup=itemView.findViewById(R.id.bloodgroup);
         //   emailNow=itemView.findViewById(R.id.emailNow);
            usercity=itemView.findViewById(R.id.usercity);
            userEmail=itemView.findViewById(R.id.userEmail);






        }
    }
}
