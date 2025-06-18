package com.example.donorblood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.ViewHolder> {

    private List<User> donorList;
    private Context context;

    public DonorAdapter(Context context, List<User> donorList) {
        this.context = context;
        this.donorList = donorList;
    }

    @Nullable
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_donor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = donorList.get(position);
        holder.textName.setText(user.getName());
        holder.textBloodType.setText("Blood Type: " + user.getBloodType());
        //holder.textLocation.setText("Location: " + user.location);

//        if (user.imageUri != null) {
//            Glide.with(context)
//                    .load(Uri.parse(user.imageUri))
//                    .circleCrop()
//                    .into(holder.imageProfile);
//        } else {
//            holder.imageProfile.setImageResource(R.drawable.profile);
//        }
    }

    @Override
    public int getItemCount() {
        return donorList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProfile;
        TextView textName, textBloodType, textLocation;

        public ViewHolder(View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.profileView);
            textName = itemView.findViewById(R.id.donorName);
            textBloodType = itemView.findViewById(R.id.donorBloodType);
            textLocation = itemView.findViewById(R.id.donorLocation);
        }
    }
}
