package com.blackstar.math4brain;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by Albert on 2017-09-21.
 */

public class ProfilePictureActivity extends Activity{

    String IPADRS2 = "amensah.com/kokotoa", FILEDP = "displayPicture.png", FILENAME = "m4bfile1";
    String[] gFile;
    String filePath;
    int PICK_IMAGE_REQUEST = 1, FILESIZE = 25;
    ImageView displayPic;
    Button changeImgBtn, removeBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_pic);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("data")){
                gFile = extras.getStringArray("data");
            }
        }

        displayPic = (ImageView) findViewById(R.id.imageViewDP);
        changeImgBtn = (Button) findViewById(R.id.buttonChangeImg);
        removeBtn = (Button) findViewById(R.id.buttonRemove);

        Bitmap bitmap;
        filePath = getFilesDir().getPath() + "/"+FILEDP;
        File f = new File(filePath);
        if(f.exists()) {
            bitmap = BitmapFactory.decodeFile(filePath);
            displayPic.setImageBitmap(bitmap);
        }else{
            removeBtn.setVisibility(View.GONE);
        }


        changeImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "en_Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File f = new File(filePath);
                if(f.exists()) {
                    if (f.delete()) {
                        gFile[23] = "NoImageURL";
                        saveData();
                    }
                }
                finish();
            }
        });
    }

    public void saveData(){
        try {
            String savData = "";
            for (int i = 0; i < FILESIZE; i++) savData += gFile[i] + " ";
            OutputStreamWriter out2 = new OutputStreamWriter(openFileOutput(FILENAME, 0));
            out2.write(savData);
            out2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                displayPic.setImageBitmap(bitmap);

                try {
                    String path = getFilesDir().getPath() + "/"+FILEDP;
                    File f = new File(path);
                    FileOutputStream out = new FileOutputStream(f);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.close();
                    new FileUploader(path,gFile[12]+".png").execute();

                    //write
                    gFile[23] = "http://"+IPADRS2+"/uploads/images/small/"+gFile[12]+".png";
                    saveData();


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("progress","error occurred");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
