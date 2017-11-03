package io.github.alexlondon07.demodevices;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexlondon07 on 10/28/17.
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private Context context;
    private List<String> arrayFiles;
    private int withLayout, heightLayout;

    public PhotoAdapter(Context context) {
        this.context = context;
    }

    public void setFiles(ArrayList<String> arrayFiles){
        this.arrayFiles = arrayFiles;
    }

    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return  new ViewHolder(view);
    }

    public void setSize(int with, int height){
        this.withLayout = with;
        this.heightLayout = height;

    }

    @Override
    public void onBindViewHolder(PhotoAdapter.ViewHolder holder, int position) {
        String fileName = arrayFiles.get(position);
        Glide.with(context).load(fileName).override(withLayout, heightLayout).into(holder.item_photo);
    }

    @Override
    public int getItemCount() {
        return arrayFiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final ImageView item_photo;
        final ImageView item_delete;

        public ViewHolder(View itemView) {
            super(itemView);
            item_photo =  itemView.findViewById(R.id.item_photo);
            item_delete =  itemView.findViewById(R.id.item_photo_delete);
            item_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    arrayFiles.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    notifyItemRangeRemoved(getAdapterPosition(),getItemCount());
                }
            });


        }
    }
}
