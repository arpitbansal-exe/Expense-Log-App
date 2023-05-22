package com.example.expenselog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<Exp_Model> list;

    public MyAdapter(Context context, ArrayList<Exp_Model> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.item_view,parent,false);
        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Exp_Model e = list.get(position);
        holder.Dateview.setText(e.getDate());
        holder.Desview.setText(e.getDes());
        holder.Amtview.setText(String.valueOf(e.getValue()));

    }

    @Override
    public int getItemCount() {
        if(list.isEmpty()){
            return 0;
        }
        else {
        return list.size() ;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView Dateview;
        TextView Desview;
        TextView Amtview;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Dateview=itemView.findViewById(R.id.dateview);
            Desview=itemView.findViewById(R.id.desview);
            Amtview=itemView.findViewById(R.id.amtview);

        }
    }
}