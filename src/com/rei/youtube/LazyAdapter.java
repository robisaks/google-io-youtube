package com.rei.youtube;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<Video> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    
    public LazyAdapter(Activity a, ArrayList<Video> vids) {
        activity = a;
        data=vids;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public static class ViewHolder{
        public TextView text;
        public TextView auth;
        public ImageView image;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        ViewHolder holder;
        if(convertView==null){
            vi = inflater.inflate(R.layout.item, null);
            holder=new ViewHolder();
            holder.text=(TextView)vi.findViewById(R.id.text);
            holder.auth=(TextView)vi.findViewById(R.id.auth);
            holder.image=(ImageView)vi.findViewById(R.id.image);
            vi.setTag(holder);
        }
        else
            holder=(ViewHolder)vi.getTag();
        
        holder.text.setText(data.get(position).title);
        holder.auth.setText(data.get(position).author);
        holder.image.setTag(data.get(position).img);
        Log.d("YOUTUBE", "thumb: " + data.get(position).img);
        imageLoader.DisplayImage(data.get(position).img, activity, holder.image);
        return vi;
    }
}