package com.example.culs.helpers;

public interface PostType extends Comparable<PostType>{

    int TYPE_POST = 101;
    int TYPE_EVENT = 102;
    int TYPE_SPONSOR = 103;

    int getType();
    long getTimediff();

}


