package com.stealthecheese.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.stealthecheese.R;
import com.stealthecheese.util.CircleTransform;
import com.stealthecheese.viewmodel.RankingViewModel;

public class RankingsListAdapter extends BaseAdapter   implements OnClickListener {
    
    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList<RankingViewModel> data;
    private static LayoutInflater inflater=null;
    public Resources res;
    RankingViewModel tempValues=null;
    int i=0;
    
    
    /*************  CustomAdapter Constructor *****************/
    public RankingsListAdapter(Activity a, ArrayList<RankingViewModel> d,Resources resLocal) {
         
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
    	public TextView cheeseCountTextView;
        public TextView playerNameTextview;
        public ImageView playerImageView;
 
    }
        
    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {
         
        View vi = convertView;
        ViewHolder holder;
        if(convertView==null){
             
            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.ranking_row, null);
             
            /****** View Holder Object to contain tabitem.xml file elements ******/
            
            holder = new ViewHolder();
            holder.playerNameTextview=(TextView)vi.findViewById(R.id.playerNameTextview);
            holder.cheeseCountTextView=(TextView)vi.findViewById(R.id.cheeseCountTextView);
            holder.playerImageView=(ImageView)vi.findViewById(R.id.playerImageView);
            
            
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
            tempValues = ( RankingViewModel ) data.get( position );
             
            /************  Set Model values in Holder elements ***********/

             String cheeseCountText = "x "+ Integer.toString(tempValues.getCheese());
             holder.cheeseCountTextView.setText(cheeseCountText);
             holder.playerNameTextview.setText(tempValues.getFirstName());
             
             //use Picasso to load image into ImageView
             String imageUrl = tempValues.getImageString();
             Transformation circleTransform = new CircleTransform();
             Picasso.with(activity).load(imageUrl)
             //.fit()
             //.centerCrop()
             .transform(circleTransform)
             .into(holder.playerImageView);
             
             /******** Set Item Click Listner for LayoutInflater for each row *******/
             vi.setClickable(false);
             holder.playerImageView.setClickable(false);
             //vi.setOnClickListener(new OnItemClickListener( position ));
             
             /* check if ranked player is user. if so, then make background blue */
             /*
             if (tempValues.getIsUser())
             {
            	 holder.playerNameTextview.setTextColor(Color.WHITE);
            	 vi.setBackgroundColor(Color.parseColor("#00C7D8"));
             }*/
             
        }
        return vi;
    }
    
    @Override
    public boolean isEnabled(int position) {
        return false;
    }
    
    @Override
    public void onClick(View v) {
            Log.v("FriendsListAdapter", "=====Row button clicked=====");
    }
    
    /* Called when image is clicked in ListView */
    /*
    private class OnImageClickListener  implements OnClickListener{           
        private int mPosition;
        private ImageView movedImage;
        private TextView cheeseCounter;
        
        OnImageClickListener(int position, ImageView movedCheeseImg, TextView counter){
             mPosition = position;
             movedImage = movedCheeseImg;
             cheeseCounter = counter;
        }
         
        @Override
        public void onClick(View arg0) {
          TheftActivity sct = (TheftActivity)activity;
          sct.onCheeseTheft(arg0, mPosition, movedImage, cheeseCounter);
        }               
    }   
    */
    
    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements OnClickListener{           
        private int mPosition;
         
//        OnItemClickListener(int position){
//             mPosition = position;
//        }
         
        @Override
        public void onClick(View arg0) {

   
        //  MainActivity sct = (MainActivity)activity;

         /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/

            //sct.onItemClick(mPosition);
        }               
    }   
}
