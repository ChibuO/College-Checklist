package com.example.collegechecklist;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> items;
    private ArrayList<Boolean> checked;
    private Context mContext;
    private SaveInstructions listSaver = new SaveInstructions();

    public RecyclerViewAdapter(ArrayList<String> rItems, ArrayList<Boolean> rChecked, Context rContext) {
        items = rItems;
        checked = rChecked;
        mContext = rContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.newBox.setText(items.get(position));
        holder.newBox.setChecked(checked.get(position));
        holder.newBox.isLongClickable();
        holder.newBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean ischecked = ((CheckBox) view).isChecked();

                if(ischecked) {
                    checked.set(holder.getAdapterPosition(), true);
                    notifyDataSetChanged();
                    listSaver.save(items, checked, mContext);
                } else {
                    //another way of getting color
                    checked.set(holder.getAdapterPosition(), false);
                    notifyDataSetChanged();
                    listSaver.save(items, checked, mContext);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        RelativeLayout parentlayout;
        CheckBox newBox;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            newBox = itemView.findViewById(R.id.checkBox);
            parentlayout =  itemView.findViewById(R.id.parentlayout);
            parentlayout.setOnCreateContextMenuListener(this);
        }

        //for the long click context menu
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            menu.add(this.getAdapterPosition(), 121, 0, "Rename");
            menu.add(this.getAdapterPosition(), 122, 1, "Delete");
        }
    }

    //to delete an item
    public void deleteItem(int position) {
        items.remove(position);
        checked.remove(position);
        notifyDataSetChanged();
    }

    //to rename an item
    public void renameItem(int position, String newName) {
        items.set(position, newName);
        notifyDataSetChanged();
    }

}
