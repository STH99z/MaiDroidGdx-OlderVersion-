package com.sth99.maidroidgdx;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by STH99 on 2016/10/16.
 */

public class NoteHitEffect extends Effect {
    private static final float EFFECT_SCALE = 1.2f;

    enum Type {
        perfect, great, good, miss
    }

    private static final int DISPFRAME = 24;

    private Type type = Type.miss;
    private Texture imgEff = null;
    private Texture imgEffText = null;
    private PointF pos = Engine.BOARD_CENTER;
    private float angle = Engine.BOARD_ANGLE[0];

    public NoteHitEffect(Type type, PointF pos, float angle) {
        super(1);
        this.pos = pos;
        this.angle = angle;
        this.type = type;
        if (type != Type.miss)
            imgEff = Engine.imgEff[type.ordinal()];
        imgEffText = Engine.imgEffText[type.ordinal()];
        setDispFrames(DISPFRAME);

    }

    public NoteHitEffect(Type type, int side) {
        this(type, Engine.BOARD_POSITION[side], Engine.BOARD_ANGLE[side]);
    }

    private static float lim(float val, float min, float max) {
        if (val < min)
            return min;
        if (val > max)
            return max;
        return val;
    }

    @Override
    public void display() {
        super.display();
        dispText();
        if (type == Type.perfect) {
            dispPerfect();
            return;
        }
        if (type == Type.great) {
            dispGreat();
            return;
        }
        if (type == Type.good) {
            dispGood();
            return;
        }
        if (type == Type.miss) {
            dispMiss();
            return;
        }
    }

    private void dispPerfect() {
        dispFrameEff();
        float rot = -90 + dispFrames * 7.5f;
        float rotd = rot / 180f * (float) Math.PI;
        float ang2 = (float) ((angle + rot) / 180f * Math.PI);
        float ang3 = (float) ((angle - rot) / 180f * Math.PI);
        PointF pos2 = new PointF((float) Math.sin(ang2),
                (float) Math.cos(ang2));
        PointF pos3 = new PointF((float) Math.sin(ang3),
                (float) Math.cos(ang3));
        if (dispFrames > 19) {
            Engine.batch.setColor(1f, 1f, 1f, (25 - dispFrames) * .2f);
        } else if (dispFrames <= 5) {
            Engine.batch.setColor(1f, 1f, 1f, 1f - (5 - dispFrames) * .2f);
        }
        Engine.drawImageCC(imgEff, pos, (.9f + (25 - dispFrames) * 0.0125f) * EFFECT_SCALE, angle);//center
        //4rot img
        Engine.drawImageCC(imgEff,
                pos.add(pos2.mul((float) Math.cos(rotd) * 64)),
                .8f * EFFECT_SCALE, angle);
        Engine.drawImageCC(imgEff,
                pos.add(pos2.mul(-(float) Math.cos(rotd) * 64)),
                .8f * EFFECT_SCALE, angle);
        Engine.drawImageCC(imgEff,
                pos.add(pos3.mul((float) Math.cos(rotd) * 64)),
                .6f * EFFECT_SCALE, angle);
        Engine.drawImageCC(imgEff,
                pos.add(pos3.mul(-(float) Math.cos(rotd) * 64)),
                .6f * EFFECT_SCALE, angle);
        Engine.batch.setColor(1f, 1f, 1f, 1f);
    }

    private void dispGreat() {
        final int IMG_COUNT = 3;
        final int TRANSPARENT_FRAMES = 4;
        final int DISP_FRAMES = 16;
        int frame0 = 24 - dispFrames;
        float[] rot = new float[]{0f, 30f, 0f};
        int[] startDrawFrame = new int[]{0, 4, 8};
        int[] endDrawFrame = new int[]{DISP_FRAMES, 4 + DISP_FRAMES, 8 + DISP_FRAMES};
        for (int i = 0; i < IMG_COUNT; i++) {
            if (frame0 <= startDrawFrame[i] || frame0 >= endDrawFrame[i])
                continue;
            float opacity = 1f;
            if (frame0 - startDrawFrame[i] < TRANSPARENT_FRAMES)
                opacity = lim((frame0 - startDrawFrame[i]) / (float) TRANSPARENT_FRAMES, 0f, 1f);
            else if (endDrawFrame[i] - frame0 < TRANSPARENT_FRAMES)
                opacity = lim((endDrawFrame[i] - frame0) / (float) TRANSPARENT_FRAMES, 0f, 1f);
            float scale = ((float) frame0 - startDrawFrame[i]) / DISP_FRAMES * 0.6f + 0.6f;
            scale *= EFFECT_SCALE;
            Engine.batch.setColor(1f, 1f, 1f, opacity);
            Engine.drawImageC(imgEff, pos.x, pos.y, scale, scale, rot[i]);
        }
        Engine.batch.setColor(1f, 1f, 1f, 1f);
    }

    private void dispGood() {
        float SCALE_SHRINK = 0.7f;
        float DIST_MAX = 58f;
        float[] rot = new float[]{0f, 120f, 240f};
        float dist = DIST_MAX;
        float scale = 0f;
        int frame0 = 24 - dispFrames;
        float f;
        for (int i = 0; i < 3; i++) {
            PointF p;
            if (frame0 < 8) {
                f = frame0;
                scale = f / 8 * EFFECT_SCALE * SCALE_SHRINK;
                dist = DIST_MAX * scale;
                p = pos.translate(rot[i] + angle, dist);
            } else if (frame0 < 16) {
                f = frame0 - 12;
                scale = EFFECT_SCALE * SCALE_SHRINK;
                dist = f / 4 * DIST_MAX * EFFECT_SCALE * SCALE_SHRINK;
                p = pos.translate(-rot[i] + angle, dist);
            } else {
                f = frame0 - 16;
                scale = (8 - f) / 8 * EFFECT_SCALE * SCALE_SHRINK;
                dist = DIST_MAX * scale;
                p = pos.translate(rot[i] + angle, dist);
            }
            Engine.drawImageC(imgEff, p.x, p.y, scale, scale, angle);
        }
    }

    private void dispMiss() {
    }

    private void dispText() {
        float x = DISPFRAME - dispFrames;
        if (dispFrames > DISPFRAME / 2) {
            x = x / (DISPFRAME / 2) * 0.5f + 0.5f;
        } else {
            x = x / DISPFRAME * (float) Math.PI;
            x = (float) Math.sin(x);
        }
        Engine.setOpacity(x);
        PointF p = pos.mul(-Engine.effTextOffset);
//        Engine.drawImageCC(imgEffText, pos.mul(0.9f), 1f, -angle);
        Engine.batch.draw(imgEffText,
                -imgEffText.getWidth() / 2 + p.x,
                -Engine.imgFrame[0].getHeight() / 2 + p.y,
                imgEffText.getWidth() / 2, Engine.imgFrame[0].getHeight() / 2,
                imgEffText.getWidth(), imgEffText.getHeight(),
                1f, 1f,
                -angle + 180f,
                0, 0,
                imgEffText.getWidth(), imgEffText.getHeight(),
                true, true);
        Engine.setOpacity(1f);
    }

    private void dispFrameEff() {
        if (!Engine.dispFrameEff)
            return;
        float x = DISPFRAME - dispFrames;
        x = x / DISPFRAME + 1f;
        float y = 1f / x;
//        y = (y - 0.5f) * 2f;
        y -= 0.5f;
        if (y > EffectManager.frameEffOpacity)
            EffectManager.frameEffOpacity = y;
    }

    @Override
    public String toString() {
        return "Eff:" + type.name() + " " + dispFrames;
    }
}
