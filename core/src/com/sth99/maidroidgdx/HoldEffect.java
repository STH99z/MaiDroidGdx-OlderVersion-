package com.sth99.maidroidgdx;

/**
 * Created by STH99 on 2016/10/18.
 */

public class HoldEffect extends Effect {
    PointF pos;
    float angle;

    @Override
    public void display() {
        //TODO draw purple ripples
        Engine.drawImageCC(
                Engine.imgEff[0],
                pos,1f,angle);
        super.display();
    }

    public HoldEffect(int dispFrames, PointF pos, float angle) {
        super(dispFrames);
        this.pos = pos;
        this.angle = angle;
    }
}
