package com.example.photopostiongyang.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.photopostiongyang.Adapter.SliderAdapterExample;
import com.example.photopostiongyang.Model.Board;
import com.example.photopostiongyang.Model.SliderItem;
import com.example.photopostiongyang.PostInfo;
import com.example.photopostiongyang.R;
import com.example.photopostiongyang.view.ContentsItemView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;
import static com.example.photopostiongyang.Util.GALLERY_IMAGE;
import static com.example.photopostiongyang.Util.GALLERY_VIDEO;
import static com.example.photopostiongyang.Util.INTENT_MEDIA;
import static com.example.photopostiongyang.Util.INTENT_PATH;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.LongStream;


public class WriteActivity extends AppCompatActivity implements View.OnClickListener {


    private  static  int PICK_IMAGE_REQUEST=1;

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private StorageReference mStorageRef;

    private EditText mWriteTitle;
    private EditText mWriteContentsText;
    private EditText mWriteNameText;
    private String id;

    private RelativeLayout buttonsBackgroundLayout;
    private ArrayList<String> pathList = new ArrayList<>();
   // private ImageView mImageView;
    private static final int GALLERY_IMAGE = 0;
    private EditText selectedEditText;
    //private LinearLayout parent;
    private ImageView selectedImageVIew;
    private EditText contentsEditText;
    private EditText titleEditText;

    private SliderAdapterExample sliderAdapterExample;
    private ArrayList<Uri> mImageURIlist;

    //private Uri mImageUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        findViewById(R.id.write_upload_botton).setOnClickListener(this);



        //이미지 슬라이더
        SliderView sliderView=findViewById(R.id.imageSlider);
        sliderAdapterExample = new SliderAdapterExample(this);
        ///sliderAdapterExample.addItem(<SliderItem>);데이터 넣는과정//////////////

        sliderView.setSliderAdapter(sliderAdapterExample);


        sliderView.setIndicatorAnimation(IndicatorAnimations.THIN_WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(false);
        //////

        findViewById(R.id.write_imagechoose_imageButton).setOnClickListener(this);

        mWriteTitle = findViewById(R.id.write_title_text);
        mWriteContentsText = findViewById(R.id.write_contents_text);



    }




    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.write_upload_botton:
                //this.id=mStore.collection("Board").document().getId();
                Map<String, Object> post = new HashMap<>();
                // post.put("id", this.id);
                post.put("Title", mWriteTitle.getText().toString());
                //post.put("Name", mWriteNameText.getText().toString());
                post.put("contents", mWriteContentsText.getText().toString());

                mStore.collection("Board").
                        document().
                        set(post).
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(WriteActivity.this, "업로드성공", Toast.LENGTH_SHORT).show();
                                finish();//저장완료되면 꺼지는것
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(WriteActivity.this, "업로드실패", Toast.LENGTH_SHORT).show();
                            }
                        });
                ////여기서부턴 이미지 업로드


                break;

            case R.id.write_imagechoose_imageButton://겔러리랑연동
                myStartActivity(GalleryActivity.class,GALLERY_IMAGE,0);
                break;


        }
    }
            //업로드파일


    public void onActivityResult(int requestCode, int resultCode, Intent data) { //이미지 클릭후 상황
        super.onActivityResult(requestCode, resultCode, data);


                if (resultCode == Activity.RESULT_OK) {
                    String path = data.getStringExtra(INTENT_PATH);
                    //pathList.add(path);
                    sliderAdapterExample.addItem(new SliderItem(path));
                    ContentsItemView contentsItemView = new ContentsItemView(this);
                    //contentsItemView.setImage(path);
                    contentsItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                            selectedImageVIew = (ImageView) v;
                        }
                    });

                    //  contentsItemView.setOnFocusChangeListener(onFocusChangeListener);
                }
        }


        private String getFileExtension (Uri uri){
            ContentResolver cR = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(cR.getType(uri));
        }
        private void myStartActivity (Class c,int media, int requestCode){
            Intent intent = new Intent(this, c);
            intent.putExtra(INTENT_MEDIA, media);
            startActivityForResult(intent, requestCode);
        }

    ///////////////////////////////업로드 과정

    /////////////업로드




}

