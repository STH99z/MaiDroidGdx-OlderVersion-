package com.sth99.maidroidgdx;

/**
 * Created by STH99 on 2016/10/27.
 */

enum JudgeTiming {
    perfect(0.06f),
    great(0.15f),
    good(0.3f);

    JudgeTiming(float equalValue) {
        eqV = equalValue;
    }

    final float v() {
        return eqV;
    }

    private float eqV;
}

enum InputStatus {
    untouch,
    justTouched,
    justRelease,
    hold
}