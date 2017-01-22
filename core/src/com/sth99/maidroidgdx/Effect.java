package com.sth99.maidroidgdx;

/**
 * Created by STH99 on 2016/10/15.
 * <p>
 * To display game effects which have nothing to related to
 * the bgm time, like 'perfect'　effect, use this class.
 */

public abstract class Effect {
    protected int dispFrames = 1;

    public Effect(int dispFrames) {
        super();
        this.dispFrames = dispFrames;
    }

    public final void setDispFrames(int dispFrames) {
        this.dispFrames = dispFrames;
    }

    public final int getDispFrames() {
        return dispFrames;
    }

    public void display() {
        dispFrames--;
    }

    public boolean isPlayOver() {
        return dispFrames <= 0;
    }
}
