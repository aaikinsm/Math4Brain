package com.blackstar.math4brain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserListAdapter extends ArrayAdapter<String[]> {

	// declaring our ArrayList of items
	private List<String[]> objects;
	private int type;
	HashMap <String, Bitmap> imgMap = new HashMap<>();
    Handler mHandler;
	/* here we must override the constructor for ArrayAdapter
	* the only variable we care about now is ArrayList<Item> objects,
	* because it is the list of objects we want to display.
	*/
	public UserListAdapter(Context context, int textViewResourceId, List<String[]> objects, int type) {
		super(context, textViewResourceId, objects);
		this.objects = objects;
		this.type = type;
	}

	/*
	 * we are overriding the getView method here - this is what defines how each
	 * list item will look.
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent){

		// assign the view we are converting to a local variable
		View v = convertView;

		// first check to see if the view is null. if so, we have to inflate it.
		// to inflate it basically means to render, or show, the view.
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (type == 1) v = inflater.inflate(R.layout.users_row, null);
			else if (type == 2) v = inflater.inflate(R.layout.users_row2, null);
		}

		/*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 * 
		 * Therefore, i refers to the current Item object.
		 */
		final String[] i = objects.get(position);

		if (i != null) {

			// This is how you obtain a reference to the TextViews.
			// These TextViews are created in the XML files we defined.

			TextView rank = (TextView) v.findViewById(R.id.textViewRnk);
			TextView total = (TextView) v.findViewById(R.id.textViewTotal);
			TextView name = (TextView) v.findViewById(R.id.textViewNam);
			TextView level = (TextView) v.findViewById(R.id.textViewLvl);
			TextView avg = (TextView) v.findViewById(R.id.textViewAvg);
			final ImageView pic = (ImageView) v.findViewById(R.id.imageViewDp);
			mHandler = new Handler();



			// check to see if each individual textview is null.
			// if not, assign some text!
			if (rank != null){
				rank.setText("#"+i[4]);
			}
			if (name != null){
				if(i[0].equals("User:_no_name")) name.setText(R.string.default_name);
				else name.setText(i[0].replace("_"," "));
			}
			if (level != null){
				level.setText(i[1]);
			}
			if (avg != null){
				avg.setText(i[2]);
			}
			if (total != null){
				total.setText(i[3]);
			}

            final Runnable getPicThread = new Runnable(){
                @Override
                public void run() {
                    if (imgMap.containsKey(i[5])){
						pic.setImageBitmap(imgMap.get(i[5]));
					}

					else{
                        mHandler.postDelayed(this,50);
                    }
                }
            };

            if(type == 2 && !i[5].equals("none")) {
                new GetPic().execute(i[5]);
                getPicThread.run();
            }
			
		}

		// the view must be returned to our activity
		return v;

	}


    class GetPic extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {
            try {
				String url = params[0];
                URL imageURL = new URL(url);
				Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
				imgMap.put(url,bitmap);
            }catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
