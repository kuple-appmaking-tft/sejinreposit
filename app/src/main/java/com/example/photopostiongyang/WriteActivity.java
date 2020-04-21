package com.example.photopostiongyang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class WriteActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private EditText mWriteTitle;
    private EditText mWriteContentsText;
    private EditText mWriteNameText;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        findViewById(R.id.write_upload_botton).setOnClickListener(this);



        mWriteTitle=findViewById(R.id.write_title_text);
        mWriteContentsText=findViewById(R.id.write_contents_text);
        //닉네임 아이디 둘중하나 뽑아오기
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
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
                break;

        }
    }
}
