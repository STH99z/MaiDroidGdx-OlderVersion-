package com.sth99.maidroidgdx;

/**
 * Created by STH99 on 2016/10/10.
 */

public class SegmentPath implements Path {
    private PointF start;
    private PointF end;
    private float length;
    private float towards;

    public SegmentPath(PointF start, PointF end) {
        this.start = start;
        this.end = end;
        length = (float) start.getDistance(end);
        towards = (float) (Math.atan2(end.x - start.x, end.y - start.y) / Math.PI * 180) - 90f;
    }

    @Override
    public PointF getPos(float percentage) {
        return new PointF((end.x - start.x) * percentage + start.x,
                (end.y - start.y) * percentage + start.y);
    }

    @Override
    public float getTowards(float percentage) {
        return towards;
    }

    @Override
    public float getLength() {
        return length;
    }
}
