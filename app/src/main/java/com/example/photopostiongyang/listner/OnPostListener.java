package com.example.photopostiongyang.listner;

import com.example.photopostiongyang.PostInfo;

public interface OnPostListener {
    void onDelete(PostInfo postInfo);
    void onModify();
}
