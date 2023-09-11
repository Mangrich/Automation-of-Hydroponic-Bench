package com.example.fertileasy;


import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

//Classe que define itens personalizados e define operações de remoção de itens da lista e torna possível
//itens serem pressionados.
public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> {
    private ArrayList<com.example.fertileasy.ExampleItem> mExampleList;
    private OnItemClickListener mListener;
    public interface OnItemClickListener{

        void onItemClick(int position,  View v);
        void onDeleteClick(int position, View v);
    }
     void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
     static class ExampleViewHolder extends RecyclerView.ViewHolder {
         ImageView mImageView;
         TextView mTextView1;
         TextView mTextView2;
         ImageView mDeleteImage;

         ExampleViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.textView);
            mTextView2 = itemView.findViewById(R.id.textView2);
            mDeleteImage = itemView.findViewById(R.id.image_delete);
             AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            itemView.setOnClickListener((View v) ->{
                    if (listener != null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onItemClick(position, v);

                        }
                    }


            });

            mDeleteImage.setOnClickListener((View v) ->  {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position, v);

                        }
                    }


            });


        }
    }


     ExampleAdapter(ArrayList<com.example.fertileasy.ExampleItem> exampleList) {
        mExampleList = exampleList;
    }


    @Override
    @NonNull
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_item, parent, false);
        return new ExampleViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        com.example.fertileasy.ExampleItem currentItem = mExampleList.get(position);
        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mTextView1.setText(currentItem.getmText1());
        holder.mTextView2.setText(currentItem.getmText2());

    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

}
