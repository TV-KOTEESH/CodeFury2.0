package com.example.codex;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemListAdapter extends BaseAdapter {

    Activity activity;
    ArrayList<ItemInfo> customListDataModelArrayList;
    LayoutInflater layoutInflater;


    public ItemListAdapter(Activity activity, ArrayList<ItemInfo> customListDataModelArray){
        this.activity=activity;
        this.customListDataModelArrayList = customListDataModelArray;
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return customListDataModelArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return customListDataModelArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private static class ViewHolder{
        ImageView pic;
        TextView name, desc, price, contact, orgName;
    }
    ViewHolder viewHolder = null;


    // this method  is called each time for arraylist data size.
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View vi=view;
        final int pos = position;
        if(vi == null){
            // create  viewholder object for list_rowcell View.
            viewHolder = new ViewHolder();
            // inflate list_rowcell for each row
            vi = layoutInflater.inflate(R.layout.view_item,null);
            viewHolder.pic = (ImageView) vi.findViewById(R.id.pic);
            viewHolder.name = (TextView) vi.findViewById(R.id.item_name);
            viewHolder.desc = (TextView) vi.findViewById(R.id.item_desc);
            viewHolder.price = vi.findViewById(R.id.price_view);
            viewHolder.contact = vi.findViewById(R.id.contact);

            /*We can use setTag() and getTag() to set and get custom objects as per our requirement.
            The setTag() method takes an argument of type Object, and getTag() returns an Object.*/
            vi.setTag(viewHolder);
        }else {

            /* We recycle a View that already exists */
            viewHolder= (ViewHolder) vi.getTag();
        }

        // viewHolder.pic.setImageResource(customListDataModelArrayList.get(pos).getImage_id());
        viewHolder.name.setText(customListDataModelArrayList.get(pos).getItemName());
        viewHolder.desc.setText(customListDataModelArrayList.get(pos).getItemDesc());
        viewHolder.price.setText("Rs " + customListDataModelArrayList.get(pos).getmItemPrice());
        viewHolder.contact.setText("Contact: " + customListDataModelArrayList.get(pos).getmContact());
        return vi;
    }
}