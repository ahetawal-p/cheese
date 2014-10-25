package com.stealthecheese.adapter;

import java.util.ArrayList;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.stealthecheese.R;
import com.stealthecheese.activity.MainActivity;
import com.stealthecheese.activity.TheftActivity;
import com.stealthecheese.util.BitmapRetrieveTask;
import com.stealthecheese.util.CircleTransform;
import com.stealthecheese.viewmodel.PlayerViewModel;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendsListAdapter extends BaseAdapter   implements OnClickListener {
    
    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    PlayerViewModel tempValues=null;
    int i=0;
     
    /*************  CustomAdapter Constructor *****************/
    public FriendsListAdapter(Activity a, ArrayList d,Resources resLocal) {
         
           /********** Take passed values **********/
            activity = a;
            data=d;
            res = resLocal;
         
            /***********  Layout inflator to call external xml layout () ***********/
             inflater = ( LayoutInflater )activity.
                                         getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         
    }
 
    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {
         
        if(data.size()<=0)
            return 1;
        return data.size();
    }
 
    public Object getItem(int position) {
        return position;
    }
 
    public long getItemId(int position) {
        return position;
    }
     
    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{   
        public TextView counterTextView;
        public ImageView friendImageView;
 
    }
 
    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {
         
        View vi = convertView;
        ViewHolder holder;
         
        if(convertView==null){
             
            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.friend_row, null);
             
            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.counterTextView=(TextView)vi.findViewById(R.id.counterTextView);
            holder.friendImageView=(ImageView)vi.findViewById(R.id.friendImageView);
            holder.friendImageView.setOnClickListener(new OnImageClickListener(position));
            
           /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else 
            holder=(ViewHolder)vi.getTag();
         
        if(data.size()<=0)
        {
            Log.v("FriendsListAdapter", "No friend items");
             
        }
        else
        {
            /***** Get each Model object from Arraylist ********/
            tempValues=null;
            tempValues = ( PlayerViewModel ) data.get( position );
             
            /************  Set Model values in Holder elements ***********/

             holder.counterTextView.setText(Integer.toString(tempValues.getCheese()));
             
             //use Picasso to load image into ImageView
             String imageUrl = tempValues.getImageString();
             Transformation circleTransform = new CircleTransform();
             Picasso.with(activity).load(imageUrl).transform(circleTransform).into(holder.friendImageView);

             /******** Set Item Click Listner for LayoutInflater for each row *******/

             //vi.setOnClickListener(new OnItemClickListener( position ));
        }
        return vi;
    }
    
    public void disableFriendListItem(int position)
    {
    	
    }
    
    @Override
    public void onClick(View v) {
            Log.v("FriendsListAdapter", "=====Row button clicked=====");
    }
    
    /* Called when image is clicked in ListView */
    private class OnImageClickListener  implements OnClickListener{           
        private int mPosition;
         
        OnImageClickListener(int position){
             mPosition = position;
        }
         
        @Override
        public void onClick(View arg0) 
        {
          TheftActivity sct = (TheftActivity)activity;
          sct.onImageClicked(arg0, mPosition);
        }               
    }   
    
    
    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements OnClickListener{           
        private int mPosition;
         
//        OnItemClickListener(int position){
//             mPosition = position;
//        }
         
        @Override
        public void onClick(View arg0) {

   
          MainActivity sct = (MainActivity)activity;

         /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/

            //sct.onItemClick(mPosition);
        }               
    }   
}
