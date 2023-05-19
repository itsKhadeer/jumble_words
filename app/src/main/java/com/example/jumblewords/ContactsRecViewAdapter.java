package com.example.jumblewords;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ContactsRecViewAdapter extends RecyclerView.Adapter<ContactsRecViewAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<Contact> contacts = new ArrayList<>();
    private final selectListener listener;
    Animation buttonCLick;

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    public ContactsRecViewAdapter(Context context, selectListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.txtName.setText(contacts.get(position).getName());
        holder.parent.setOnClickListener(v -> {
            buttonCLick = AnimationUtils.loadAnimation(context, R.anim.heart_lost_animation);
            holder.parent.setEnabled(false);
            if(!MainActivity.speedModeOn) {
                if (MainActivity2.count / 2 < MainActivity2.currAnsweringWord.length()) {
                    holder.parent.setBackground(ContextCompat.getDrawable(context, R.drawable.backgroundbutton2));
                    listener.onItemClicked(contacts.get(holder.getAdapterPosition()));
                    holder.txtName.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.txtName.startAnimation(buttonCLick);

                }
            } else {
                if (MainActivity3.count / 2 < MainActivity3.currAnsweringWord.length()) {
                    holder.parent.setBackground(ContextCompat.getDrawable(context, R.drawable.backgroundbutton2));
                    listener.onItemClicked(contacts.get(holder.getAdapterPosition()));
                    holder.txtName.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.txtName.startAnimation(buttonCLick);

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final RelativeLayout parent;
        private final TextView txtName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            parent = itemView.findViewById(R.id.parent);

        }
    }
}