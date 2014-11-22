package com.stealthecheese.adapter;

import java.util.ArrayList;

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

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.picasso.Picasso;
import com.stealthecheese.R;
import com.stealthecheese.activity.TheftActivity;
import com.stealthecheese.viewmodel.PlayerViewModel;

public class FriendsListAdapter extends BaseAdapter   implements OnClickListener {
    
    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList<PlayerViewModel> data;
    private static LayoutInflater inflater=null;
    public Resources res;
    int i=0;
    
    
    /*************  CustomAdapter Constructor *****************/
    public FriendsListAdapter(Activity a, ArrayList<PlayerViewModel> d,Resources resLocal) {
         
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
        private TextView counterTextView;
        private ImageView friendImageView;
        private ImageView animImage;
 
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
            holder.animImage = (ImageView)vi.findViewById(R.id.cheeseAnimationImageView);
            holder.counterTextView=(TextView)vi.findViewById(R.id.counterTextView);
            holder.friendImageView=(ImageView)vi.findViewById(R.id.friendImageView);
            
            
           /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        } else { 
        	holder=(ViewHolder)vi.getTag();
        	holder.friendImageView=(ImageView)vi.findViewById(R.id.friendImageView);
        }
        
        holder.friendImageView.setOnClickListener(new OnImageClickListener(position, holder.animImage, holder.counterTextView));
        
        if(data.size()<=0) {
            Log.v("FriendsListAdapter", "No friend items");
             
        } else {
            /***** Get each Model object from Arraylist ********/
            PlayerViewModel tempValues = (PlayerViewModel)data.get(position);
             
            /************  Set Model values in Holder elements ***********/

             holder.counterTextView.setText(Integer.toString(tempValues.getCheese()));
             
             //use Picasso to load image into ImageView
             String imageUrl = tempValues.getImageString();
             Picasso.with(activity).load(imageUrl)
             .fit()
             .centerCrop()
             .into(holder.friendImageView);
             
             /* if player has 0 cheese, gray out image and disable click for both ImageView and ListItem*/
             Boolean showMe = (Boolean) data.get(position).getShowMe();
             
             if (!showMe) {
            	 lockImageClick(holder.friendImageView, holder.counterTextView);
             } else {
            	 unlockImageClick(holder.friendImageView, holder.counterTextView);
             }
             
             if(tempValues.getAnimateMe()){
            	 animatePushUpdates(holder);
            	 tempValues.setAnimateMe(false);
             }
                          
        }
        return vi;
    }
    
    
	private void animatePushUpdates(ViewHolder holder) {
		YoYo.with(Techniques.ZoomIn).duration(700).playOn(holder.friendImageView);
		YoYo.with(Techniques.Bounce).duration(500).playOn(holder.counterTextView);
	}
    
    public void lockImageClick(ImageView imageView, TextView textView)
    {
    	imageView.setAlpha(0.2f);
    	textView.setAlpha(0.2f);
    	imageView.setClickable(false);
    }
    
    public void unlockImageClick(ImageView imageView, TextView textView)
    {
    	imageView.setAlpha(1f);
    	textView.setAlpha(1f);
    	imageView.setClickable(true);
    }
    
    @Override
    public void onClick(View v) {
            Log.v("FriendsListAdapter", "=====Row button clicked=====");
    }
    
    /* Called when image is clicked in ListView */
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
    
}
