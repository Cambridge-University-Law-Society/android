package com.example.culs.helpers;

import java.util.Comparator;

public interface PostType extends Comparable<PostType>{

    int TYPE_POST = 101;
    int TYPE_EVENT = 102;

    int getType();
    long getTimediff();

}


