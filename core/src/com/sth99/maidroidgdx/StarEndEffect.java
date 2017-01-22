package com.sth99.maidroidgdx;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by STH99 on 2016/10/18.
 */

public class StarEndEffect extends Effect {
    private static int STAR_END_EFFECT_DISPTIME = 28;
    //    private PointF posStart;
    private PointF posEnd;
    private float angle;
    private StarEndEffectType type;
    private int starEffIndex = 0;

    public void setStarEffIndex(int index) {
        this.starEffIndex = index;
    }

    public StarEndEffect(int outSide1, int outSide2) {
        super(STAR_END_EFFECT_DISPTIME);
        type = StarEndEffectType.segment;
//        posStart = Engine.BOARD_POSITION[outSide1];
        posEnd = Engine.BOARD_POSITION[outSide2];
        angle = new SegmentPath(Engine.BOARD_POSITION[outSide1], Engine.BOARD_POSITION[outSide2]).getTowards(0f);
    }

    public StarEndEffect(PointF posEnd, float angle) {
        super(STAR_END_EFFECT_DISPTIME);
        type = StarEndEffectType.segment;
//        this.posStart = posStart;
        this.posEnd = posEnd;
        this.angle = angle;
    }

    public StarEndEffect(PointF posStart, PointF posEnd) {
        super(STAR_END_EFFECT_DISPTIME);
        type = StarEndEffectType.segment;
        this.posEnd = posEnd;
        this.angle = new SegmentPath(posStart, posEnd).getTowards(0f);
    }

    @Override
    public void display() {
        super.display();
        float opacity = 1f;
        if (dispFrames >= STAR_END_EFFECT_DISPTIME - 4)
            opacity = (STAR_END_EFFECT_DISPTIME - dispFrames) / 4f;
        if (dispFrames < 4)
            opacity = dispFrames / 4f;
        Texture img = Engine.imgStarEff[starEffIndex];
        Engine.batch.setColor(1f, 1f, 1f, opacity);
        Engine.batch.draw(img,
                posEnd.x - img.getWidth(),
                posEnd.y - img.getHeight() / 2f,
                img.getWidth(), img.getHeight() / 2f,
                img.getWidth(), img.getHeight(),
                1f, 1f, -angle,
                0, 0, img.getWidth(), img.getHeight(),
                false, false);
        Engine.batch.setColor(1f, 1f, 1f, 1f);
    }
}

enum StarEndEffectType {
    segment, arc
}
