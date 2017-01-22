package com.sth99.maidroidgdx;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by STH99 on 2016/10/3.
 */

public class MaiStar extends MaiNote implements MaiBase {
    protected double timing2 = 0d;
    protected double timing3 = 0d;
    protected float pos2 = 0f;
    protected boolean sePlayed2 = false;
    protected boolean alreadyHit2 = false;
    private MultiPath path;
    private Texture imgPath;
    private boolean isBothStar = false;
    private PathCheckPoint pathCheckPoint;
    private static float IMG_PATH_SCALE = 1.3f;
    private StarEndEffect starEndEffect = null;

    private static double getJLevel1() {
        return BoardManager.getSpb();
    }

    private static double getJLevel2() {
        return BoardManager.getSpb() * 1.5;
    }

    private static double getJLevel3() {
        return BoardManager.getSpb() * 2;
    }

    public boolean getAlreadyHit() {
        return alreadyHit;
    }

    public double getTiming2() {
        return timing2;
    }

    public MaiStar(double timing, byte side, double timing2, double timing3, PathCheckPoint pathCheckPoint, MultiPath path,
                   StarEndEffect effect) {
        super(timing, side);
        this.timing2 = timing2;
        this.timing3 = timing3;
        this.pathCheckPoint = pathCheckPoint;
        this.path = path;
        imgEdge = Engine.imgEdge[3];
        imgPath = Engine.imgPath[0];
        this.starEndEffect = effect;
    }

    public MaiStar(double timing, byte side, boolean isBreak, boolean isBoth, MaiBase other,
                   double timing2, double timing3, PathCheckPoint pathCheckPoint, MultiPath path,
                   StarEndEffect effect) {
        super(timing, side, isBreak, isBoth, other);
        img = Engine.imgStar[0];
        imgEdge = Engine.imgEdge[3];
        imgPath = Engine.imgPath[0];
        if (isBoth) {
            img = Engine.imgStar[1];
            imgEdge = Engine.imgEdge[1];
            if (other instanceof MaiStar) {
                isBothStar = true;
                imgPath = Engine.imgPath[1];
            }
        }
        this.timing2 = timing2;

        this.timing3 = timing3;
        this.pathCheckPoint = pathCheckPoint;
        this.path = path;
        this.starEndEffect = effect;
    }

    @Override
    public void calcPos(double audioTiming) {
        if (audioTiming > timing) {
            pos = 1.0f;
        } else {
            super.calcPos(audioTiming);
        }
        if (audioTiming < timing2) {
            pos2 = 0f;
        } else {
            pos2 = (float) ((audioTiming - timing2) / (timing3 - timing2));
        }
    }

    @Override
    public void reset() {
        super.reset();
    }

    @Override
    public MaiStar clone() {
        return new MaiStar(timing, side, isBreak, isBoth, otherNote, timing2, timing3, pathCheckPoint, path, starEndEffect);
    }

    void showCP() {
        pathCheckPoint.showCP();
    }

    @Override
    public boolean drawNote() {
        if (pos < 0f)
            return false;
        if (pos2 == 0f) {
            this.noteRotationAngle = pos * 720;
            super.drawNote();
        }
        float l = path.getLength();
        float step = (Engine.imgPath[0].getWidth() * 1.2f) / l;
        float start = BoardManager.gameScene == BoardManager.GameScene.autoPlay ?
                pos2 :
                pathCheckPoint.getCurrentPos();
        Engine.batch.setColor(1f, 1f, 1f, pos);
        for (float f = 1f; f >= start; f -= step) {
            PointF ppos = path.getPos(f);
            Engine.batch.draw(imgPath,
                    ppos.x - imgPath.getWidth() / 2,
                    ppos.y - imgPath.getHeight() / 2,
                    imgPath.getWidth() / 2, imgPath.getHeight() / 2,
                    imgPath.getWidth(), imgPath.getHeight(),
                    1f, IMG_PATH_SCALE,
                    -path.getTowards(f),
                    0, 0, imgPath.getWidth(), imgPath.getHeight(),
                    false, false);
        }
        Engine.batch.setColor(1f, 1f, 1f, 1f);
        if (pos2 > 0f) {
            Engine.drawImageCC(isBothStar ? Engine.imgStar[1] : Engine.imgStar[0],
                    path.getPos(pos2), 1.5f, path.getTowards(pos2));
        }
        return true;
    }

    @Override
    protected void drawArc() {
        super.drawArc();
    }

    @Override
    public void judge(double audioTiming) {
        super.judge(audioTiming);//// FIXME: 2016/11/5 第一下按下之前没有后续判定
        if (!alreadyHit && audioTiming > timing + JudgeTiming.good.v()) {
            //// FIXME: 2016/11/1 如果是timing2的话会不会有考虑不到的时间区域
            //first hit failed
            alreadyHit2 = true;
            return;
        }
        if (alreadyHit && !alreadyHit2) {
            alreadyHit2 |= pathCheckPoint.checkFinish();
            if (!sePlayed2 && pathCheckPoint.getCurrentIndex() > 1) {
                Engine.playSeStar();
                sePlayed2 = true;
            }
            if (alreadyHit2) {
                int exactLevel;
                double delta = timing3 - audioTiming;
                int fac = timing3 > audioTiming ? -1 : 1;
                if (Math.abs(delta) < getJLevel1()) {
                    exactLevel = 3;
                } else if (Math.abs(delta) < getJLevel2()) {
                    exactLevel = 3 + fac;
                } else if (Math.abs(delta) < getJLevel3()) {
                    exactLevel = 3 + fac * 2;
                } else {
                    exactLevel = 1;
                }
                int scoreIndex = exactLevel - 3;
                if (scoreIndex < 0)
                    scoreIndex = -scoreIndex;
                starEndEffect.setStarEffIndex(exactLevel);
                EffectManager.addToDrawList(starEndEffect);
                BoardManager.scoreAdd(this, scoreIndex);
                return;
            } else if (audioTiming > timing3 + getJLevel3()) {
                //too late, not reach the star tail
                starEndEffect.setStarEffIndex(0);
                EffectManager.addToDrawList(starEndEffect);
                return;
            }

        }
    }

    @Override
    public boolean remaining(double bgmTime) {
        return !alreadyHit2 && bgmTime <= timing3 + getJLevel3();
    }

    @Override
    public String toString() {
        String s = super.toString();
        return "star:" + s.substring(5);
    }

    @Override
    public void checkHit(double audioTiming) {
        if (BoardManager.gameScene == BoardManager.GameScene.autoPlay) {
            autoJudge(audioTiming);
            return;
        }
        judge(audioTiming + Engine.judgeTimingOffset);
    }

    public void autoJudge(double audioTiming) {
        //star head hit
        super.autoJudge(audioTiming);
        //star start se
        if (pos2 > 0f && !sePlayed2) {
            sePlayed2 = true;
            Engine.playSeStar();
            return;
        }
        //star tail fx
        if (pos2 >= 1f) {
            alreadyHit2 = true;
            starEndEffect.setStarEffIndex(3);
            EffectManager.addToDrawList(starEndEffect);
            BoardManager.scoreAdd(this, 0);
            return;
        }
    }
}

class PathCheckPoint {
    private SingleCheckPoint[] checkPoints;

    private int currentIndex = 0;

    private static int abs(int v) {
        return v < 0 ? -v : v;
    }

    private static int min(int v1, int v2) {
        return v1 < v2 ? v1 : v2;
    }

    private static int mod(int v) {
        if (v < 0) return v + 8;
        return v % 8;
    }

    private static float getArcLen(int IAcount) {
        return 2f * fPI * Engine.BOARD_RADIUS2 * IAcount / 8;
    }

    private static float fPI = (float) Math.PI;
    private static boolean alreadyGetIOsetLength = false;
    private static float IOsegLength;

    private static StarEndEffect genedSEE = null;//architecture fail

    private static float getIOsegLength() {
        if (!alreadyGetIOsetLength) {
            IOsegLength = (float) Engine.BOARD_POSITION[0].getDistance(Engine.BOARD_POSITION2_MID[2]);
            alreadyGetIOsetLength = true;
        }
        return IOsegLength;
    }

    StarEndEffect generateStarEndEff() {
        return genedSEE;
    }

    public PathCheckPoint(int side1, int side2, int starType) {
        int side1i;
        int side2i;
        int side3i;
        int side4i;
        int direction;
        switch (starType) {
            case BoardManager.chDash:
                //// FIXME: 2016/10/31 优化各种判定，能用两个区域判定的就用两个
                procDash(side1, side2, (char) starType);
                break;
            case BoardManager.chV:
                procV(side1, side2, (char) starType);
                break;
            case BoardManager.chGreater:
                direction = side1 > 1 && side1 < 6 ? -1 : 1;
                procGL(side1, side2, direction);
                break;
            case BoardManager.chLess:
                direction = -(side1 > 1 && side1 < 6 ? -1 : 1);
                procGL(side1, side2, direction);
                break;
            case BoardManager.chP:
                side1i = (side1 + 6) % 8;
                side2i = (side2 + 2) % 8;
                direction = -1;
                procPQ(side1, side2, side1i, side2i, direction);
                break;
            case BoardManager.chQ:
                side1i = (side1 + 2) % 8;
                side2i = (side2 + 6) % 8;
                direction = 1;
                procPQ(side1, side2, side1i, side2i, direction);
                break;
            case BoardManager.chZ:
                side1i = (side1 + 1) % 8;
                side2i = (side1 + 2) % 8;
                side3i = (side1 + 6) % 8;
                side4i = (side1 + 5) % 8;
                procSZ(side1, side2, side1i, side2i, side3i, side4i);
                break;
            case BoardManager.chS:
                side1i = (side1 + 7) % 8;
                side2i = (side1 + 6) % 8;
                side3i = (side1 + 2) % 8;
                side4i = (side1 + 3) % 8;
                procSZ(side1, side2, side1i, side2i, side3i, side4i);
                break;
            default:
                throw new IllegalStateException("starType \"" + (char) starType + "\" is not a valid type.");
        }
    }

    private void procSZ(int side1, int side2, int side1i, int side2i, int side3i, int side4i) {
        //// FIXME: 2016/11/1 减少分段，把多个分段整合到一个一个分段，变成多个判定
        setCPcount(7);
        setCPIndexPos(0, 0f, side1);
        setCPIndexPos(1, .2f, side1i + 8);
        setCPIndexPos(2, .4f, side2i + 8);
        setCPIndexPos(3, .5f, 16);
        setCPIndexPos(4, .6f, side3i + 8);
        setCPIndexPos(5, .8f, side4i + 8);
        setCPIndexPos(6, 1f, side2);
        genedSEE = new StarEndEffect(Engine.BOARD_POSITION2[side3i], Engine.BOARD_POSITION[side2]);
    }

    private void procPQ(int side1, int side2, int side1i, int side2i, int direction) {
        int count = 0;
        while (mod(side1i + direction * count) != side2i)
            count++;
        count++;
        float al = getArcLen(count);
        float len = getIOsegLength() * 2 + al;
        float pio = getIOsegLength() / len;
        float pal = al / len;
        setCPcount(count + 4);
        setCPIndexPos(0, 0f, side1, side1 + 8);
        setCPIndexPos(1, pio * 0.5f, side1 + 8, mod(side1 + direction) + 8);
        setCPIndexPos(count + 2, pio * 1.5f + pal, side2 + 8, mod(side2 - direction) + 8);
        setCPIndexPos(count + 3, 1f, side2, side2 + 8);
        for (int i = 0; i < count; i++) {
            setCPIndexPos(i + 2, pio + pal * (i + 1) / count, mod(side1i + direction * i) + 8);
        }
        genedSEE = new StarEndEffect(Engine.BOARD_POSITION2[side2i], Engine.BOARD_POSITION[side2]);
    }

    private void procGL(int side1, int side2, int direction) {
        int count = 1;
        while (mod(side1 + direction * count) != side2)
            count++;
        count++;
        setCPcount(count);
        for (int i = 0; i < count; i++) {
            setCPIndexPos(i, .9f * i / (count - 1), mod(side1 + direction * i));
        }
        genedSEE = new StarEndEffect(mod(side2 - direction), side2);
    }

    private void procV(int side1, int side2, char starType) {
        if (side1 == side2)
            throw new IllegalStateException("starType \"" + starType + "\" can't link from " +
                    side1 + " to " + side2 + ".");
        setCPcount(5);
        setCPIndexPos(0, .0f, side1);
        setCPIndexPos(1, .2f, side1 + 8, side1);
        setCPIndexPos(2, .5f, 16);
        setCPIndexPos(3, .75f, side2 + 8, side2);
        setCPIndexPos(4, 1.f, side2);
        genedSEE = new StarEndEffect(Engine.BOARD_CENTER, Engine.BOARD_POSITION[side2]);
    }

    private void procDash(int side1, int side2, char starType) {
        int sideDiff = 4 - abs(4 - abs(side1 - side2));
        int factor;
        switch (sideDiff) {
            case 1:
                setCPcount(2);
                setCPIndexPos(0, .0f, side1);
                setCPIndexPos(1, 1.f, side2);
                genedSEE = new StarEndEffect(side1, side2);
                break;
            case 2:
                factor = mod(side1 + 2) == side2 ? 1 : -1;//get direction
                setCPcount(3);
                setCPIndexPos(0, .0f, side1);
                setCPIndexPos(1, .5f, mod(side1 + factor), mod(side1 + factor) + 8);
                setCPIndexPos(2, 1.f, side2);
                genedSEE = new StarEndEffect(side1, side2);
                break;
            case 3:
                setCPcount(4);
                factor = mod(side1 + 3) == side2 ? 1 : -1;//get direction
                int side1i = mod(side1 + factor);
                int side2i = mod(side2 + factor * 2);
                setCPIndexPos(0, .0f, side1, side1 + 8);
                setCPIndexPos(1, .3f, side1i + 8, side1 + 8);
                setCPIndexPos(2, .7f, side2i + 8, side2 + 8);
                setCPIndexPos(3, 1f, side2, side2 + 8);
                genedSEE = new StarEndEffect(side1, side2);
                break;
            case 4:
                procV(side1, side2, starType);
                break;
            default:
                throw new IllegalStateException("starType \"" + starType + "\" can't link from " +
                        side1 + " to " + side2 + ".");
        }
    }

    private void setCPcount(int count) {
        checkPoints = new SingleCheckPoint[count];
    }

    private void setCPIndexPos(int index, float pos, int... inputAreaIndex) {
        checkPoints[index] = new SingleCheckPoint(inputAreaIndex, pos);
    }

    /**
     * check if the multi section touch is finished
     */
    public boolean checkFinish() {
        while (currentIndex < checkPoints.length) {
            if (checkPoints[currentIndex].checked())
                currentIndex++;
            else
                break;
        }
        return currentIndex == checkPoints.length;
    }

    public float getCurrentPos() {
        if (currentIndex < checkPoints.length) {
            int index = currentIndex > 0 ? currentIndex - 1 : currentIndex;
            return checkPoints[index].getPos();
//            return checkPoints[currentIndex].getPos();
        }
        return 1f;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    void showCP() {
        for (int i = currentIndex; i < checkPoints.length; i++) {
            checkPoints[i].showArea();
        }
    }

    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < checkPoints.length; i++) {
            result = result + checkPoints[i].toString();
        }
        return result;
    }
}

class SingleCheckPoint {
    private int[] inputArea;
    private float pos;

    public SingleCheckPoint(int[] inputArea, float pos) {
        this.inputArea = inputArea;
        this.pos = pos;
    }

    public SingleCheckPoint(int inputArea, float pos) {
        this.inputArea = new int[]{inputArea};
        this.pos = pos;
    }

    public boolean checked() {
        for (int i = 0; i < inputArea.length; i++) {
            if (InputManager.getArea(inputArea[i]).isTouched())
                return true;
        }
        return false;
    }

    public float getPos() {
        return pos;
    }

    void showArea() {
        for (int i = 0; i < inputArea.length; i++) {
            InputManager.drawSingleInputArea(inputArea[i]);
        }
    }

    @Override
    public String toString() {
        if (inputArea.length == 1) {
            return inputArea[0] + "";
        }
        StringBuffer buffer = new StringBuffer(inputArea.length * 2 + 1);
        buffer.append('[');
        for (int i = 0; i < inputArea.length; i++) {
            buffer.append(inputArea[i] + ",");
        }
        buffer.deleteCharAt(buffer.length() - 1);
        buffer.append(']');
        return buffer.toString();
    }
}