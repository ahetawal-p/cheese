package com.stealthecheese.util;

import java.lang.ref.WeakReference;
import java.net.URL;

import com.stealthecheese.application.StealTheCheeseApplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class BitmapRetrieveTask extends AsyncTask<String, Void, Bitmap> {
	private final WeakReference<ImageView> imageViewReference;
    private Exception exception;

    public BitmapRetrieveTask(ImageView imageView) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
    }
    
    protected Bitmap doInBackground(String... facebookId) {
    		String urlString = String.format(StealTheCheeseApplication.FRIEND_CHEESE_COUNT_PIC_URL, facebookId);
    		URL img_url;
    		Bitmap bm = null;

    		try 
    		{
    			img_url = new URL(urlString);
    			bm = BitmapFactory.decodeStream(img_url.openConnection().getInputStream());
    		} 
    		catch (Exception e) 
    		{
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
    		return bm;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

}