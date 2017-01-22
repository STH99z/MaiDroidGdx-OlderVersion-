package com.sth99.maidroidgdx;

/**
 * Created by STH99 on 2016/10/10.
 */

public interface Path {
    PointF getPos(float percentage);

    float getTowards(float percentage);

    float getLength();
}
