package com.sth99.maidroidgdx;

/**
 * Created by STH99 on 2016/10/10.
 */

public class MultiPath implements Path {
    private Path[] paths;
    private float[] lengthPartSum;
    private float length = 0f;

    public MultiPath(Path[] paths) {
        this.paths = paths;
        lengthPartSum = new float[paths.length];
        for (int i = 0; i < paths.length; i++) {
            lengthPartSum[i] = length;
            length += paths[i].getLength();
        }
    }

    private int getPart(float percentage) {
        int i;
        float pl = percentage * length;
        for (i = lengthPartSum.length - 1; i > 0; i--) {
            if (lengthPartSum[i] < pl)
                break;
        }
        return i;
    }

    @Override
    public PointF getPos(float percentage) {
        if (percentage > 1f)
            percentage = 1f;
        int i = getPart(percentage);
        return paths[i].getPos((percentage * length - lengthPartSum[i]) / paths[i].getLength());
    }

    @Override
    public float getTowards(float percentage) {
        if (percentage > 1f)
            percentage = 1f;
        int i = getPart(percentage);
        return paths[i].getTowards((percentage * length - lengthPartSum[i]) / paths[i].getLength());
    }

    @Override
    public float getLength() {
        return length;
    }

    public static MultiPath CreatePath(byte side1, byte side2, int method) {
        Path[] p;
        float ang1;
        float ang2;
        int side1i, side2i;
        switch (method) {
            case BoardManager.chDash:
                p = new Path[1];
                p[0] = new SegmentPath(Engine.BOARD_POSITION[side1], Engine.BOARD_POSITION[side2]);
                break;
            case BoardManager.chV:
                p = new Path[2];
                p[0] = new SegmentPath(Engine.BOARD_POSITION[side1], Engine.BOARD_CENTER);
                p[1] = new SegmentPath(Engine.BOARD_CENTER, Engine.BOARD_POSITION[side2]);
                break;
            case BoardManager.chGreater:
                ang1 = Engine.BOARD_ANGLE[side1];
                ang2 = Engine.BOARD_ANGLE[side2];
                if (side1 < 2 || side1 > 5) {
                    while (ang2 < ang1) {
                        ang2 += 360f;
                    }
                } else {
                    while (ang2 > ang1) {
                        ang2 -= 360f;
                    }
                }
                p = new Path[1];
                p[0] = new ArcPath(Engine.BOARD_CENTER, Engine.BOARD_RADIUS, ang1, ang2);
                break;
            case BoardManager.chLess:
                ang1 = Engine.BOARD_ANGLE[side1];
                ang2 = Engine.BOARD_ANGLE[side2];
                if (side1 < 2 || side1 > 5) {
                    while (ang2 > ang1) {
                        ang2 -= 360f;
                    }
                } else {
                    while (ang2 < ang1) {
                        ang2 += 360f;
                    }
                }
                p = new Path[1];
                p[0] = new ArcPath(Engine.BOARD_CENTER, Engine.BOARD_RADIUS, ang1, ang2);
                break;
            case BoardManager.chP:
                side1i = (byte) ((side1 + 7) % 8);
                side2i = (byte) ((side2 + 2) % 8);
                ang1 = Engine.BOARD_ANGLE[side1i] - 22.5f;
                ang2 = Engine.BOARD_ANGLE[side2i] - 22.5f;
                while (ang2 >= ang1) {
                    ang2 -= 360f;
                }
                p = new Path[3];
                p[0] = new SegmentPath(Engine.BOARD_POSITION[side1], Engine.BOARD_POSITION2_MID[side1i]);
                p[1] = new ArcPath(Engine.BOARD_CENTER, Engine.BOARD_RADIUS2, ang1, ang2);
                p[2] = new SegmentPath(Engine.BOARD_POSITION2_MID[side2i], Engine.BOARD_POSITION[side2]);
                break;
            case BoardManager.chQ:
                side1i = (byte) ((side1 + 2) % 8);
                side2i = (byte) ((side2 + 7) % 8);
                ang1 = Engine.BOARD_ANGLE[side1i] - 22.5f;
                ang2 = Engine.BOARD_ANGLE[side2i] - 22.5f;
                while (ang2 <= ang1) {
                    ang2 += 360f;
                }
                p = new Path[3];
                p[0] = new SegmentPath(Engine.BOARD_POSITION[side1], Engine.BOARD_POSITION2_MID[side1i]);
                p[1] = new ArcPath(Engine.BOARD_CENTER, Engine.BOARD_RADIUS2, ang1, ang2);
                p[2] = new SegmentPath(Engine.BOARD_POSITION2_MID[side2i], Engine.BOARD_POSITION[side2]);
                break;
            case BoardManager.chZ:
                side1i = (side1 + 2) % 8;
                side2i = (side1 + 6) % 8;
                p = new Path[3];
                p[0] = new SegmentPath(Engine.BOARD_POSITION[side1], Engine.BOARD_POSITION2[side1i]);
                p[1] = new SegmentPath(Engine.BOARD_POSITION2[side1i], Engine.BOARD_POSITION2[side2i]);
                p[2] = new SegmentPath(Engine.BOARD_POSITION2[side2i], Engine.BOARD_POSITION[side2]);
                break;
            case BoardManager.chS:
                side1i = (side1 + 6) % 8;
                side2i = (side1 + 2) % 8;
                p = new Path[3];
                p[0] = new SegmentPath(Engine.BOARD_POSITION[side1], Engine.BOARD_POSITION2[side1i]);
                p[1] = new SegmentPath(Engine.BOARD_POSITION2[side1i], Engine.BOARD_POSITION2[side2i]);
                p[2] = new SegmentPath(Engine.BOARD_POSITION2[side2i], Engine.BOARD_POSITION[side2]);
                break;
            default:
                throw new IllegalStateException("starType \"" + (char) method + "\" is not a valid type.");
        }
        return new MultiPath(p);
    }
}
