package com.sth99.maidroidgdx;

/**
 * Created by STH99 on 2016/10/10.
 */

public class ArcPath implements Path {
    private PointF center;
    private float radius;
    private float start;//angle 360
    private float end;//angle 360
    private float angleDelta;
    private float length;

    public ArcPath(PointF center, float radius, float arcStart, float arcEnd) {
        this.center = center;
        this.radius = radius;
        this.start = arcStart;
        this.end = arcEnd;
        angleDelta = end - start;
        length = (float) (Math.abs(angleDelta) / 180 * Math.PI * radius);
    }

    @Override
    public PointF getPos(float percentage) {
        float to = angleDelta * percentage + start;
        to = to / 180f * (float) Math.PI;
        return new PointF((float) (Math.sin(to) * radius), (float) (Math.cos(to) * radius));
    }

    @Override
    public float getTowards(float percentage) {
        float to = angleDelta * percentage + start;
        return to + (angleDelta < 0f ? 180f : 0f);
    }

    @Override
    public float getLength() {
        return length;
    }
}
