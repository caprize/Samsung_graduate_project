package com.example.samsung_project.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.samsung_project.Network.RecipeGet;
import com.example.samsung_project.R;

import java.io.IOException;
import java.util.List;

import static java.lang.Thread.sleep;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.DishViewHolder> {
    List<String> dishesList;
    LayoutInflater inflater;
    Bitmap bm;

    public DishAdapter(List<String> dishes,Context context){
        this.dishesList = dishes;
        this.inflater = LayoutInflater.from(context);
    }

    public List<String> getDishesList() {return dishesList;}

    public void setDishesList(List<String> dishesList){this.dishesList = dishesList; }

    public Bitmap getImage(int position){
        String req = dishesList.get(position);
        byte[][] response = {new byte[0]};
        String method = "post_image";
        RecipeGet imageGet = new RecipeGet();
        String url = "10.0.2.2";
        Runnable task = () -> {
            try {
                System.out.println("here");
                response[0] = imageGet.post_GetImage(url,req);
                System.out.println(response[0]);
                sleep(1000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(task);
        thread.start();
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (response[0]!=null) {
            imageGet.setToBitmap(response[0]);
            bm = imageGet.prep_image();
            return bm;
        }
        else return null;
    }

    @NonNull
    @Override
    public DishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DishViewHolder(inflater.inflate(R.layout.dishes_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull DishViewHolder holder, int position) {
        bm = getImage(position);
        holder.imageView.setImageBitmap(bm);
        holder.name.setText(dishesList.get(position));
    }


    @Override
    public int getItemCount() {
        return 5;
    }
    class DishViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView name;

        public DishViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
        }
    }
}
