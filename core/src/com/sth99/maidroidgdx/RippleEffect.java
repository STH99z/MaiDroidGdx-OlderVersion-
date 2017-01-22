package com.sth99.maidroidgdx;

/**
 * Created by STH99 on 2016/10/30.
 */

public class RippleEffect extends Effect {
    private static final float EFFECT_SCALE = 1.4f;
    private PointF pos;

    @Override
    public boolean isPlayOver() {
        return super.isPlayOver();
    }

    @Override
    public void display() {
        super.display();
        double scale = (15 - dispFrames) / 15f * Math.PI / 2;
        scale = Math.sin(scale);
        Engine.batch.setColor(1f, 1f, 1f, 1f - (float) scale);
        Engine.drawImageCC(Engine.imgRipple, pos, (float) scale * EFFECT_SCALE, 0f);
        Engine.batch.setColor(1f, 1f, 1f, 1f);
    }

    public RippleEffect(PointF pos) {
        super(15);
        this.pos = pos;
    }
}
