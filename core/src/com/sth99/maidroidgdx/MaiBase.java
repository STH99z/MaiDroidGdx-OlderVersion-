package com.sth99.maidroidgdx;

import javax.swing.JTabbedPane;

/**
 * Created by STH99 on 2016/10/3.
 */

public interface MaiBase {
    /***
     * 绘制当前note
     *
     * @return 返回该note是否被绘制
     */
    boolean drawNote();

    /**
     * 对note进行判定
     *
     * @param audioTiming 当前时间
     * @return 与note所在时间的时间差
     */
    void judge(double audioTiming);

    void autoJudge(double audioTiming);

    /**
     * 返回当前时间和按键状态下note是否还应该留在显示池中
     *
     * @param bgmTime 当前bgm时间，通常为Engine.bgmPosition
     * @return 留或者不留
     */
    boolean remaining(double bgmTime);

    /**
     * process input and note hit status, play se and
     * create hit effects if should.
     */
    void checkHit(double audioTiming);

    void calcPos(double audioTiming);

    double getTiming();

    void setTiming(double timing);

    void setOtherNote(MaiBase otherNote);

    byte getSide();

    void setSide(byte side);

    Effect createHitEffect();
}

class JudgeResult {
    /**
     * shows input is hit on noe or not
     */
    public boolean hitOnNote;
    /**
     * shows how exact the input is
     * 0 ~ 2 = perfect, great, good
     */
    public int exactLevel;
    /**
     * shos input timing is earlier
     * than note timing or not
     */
    public boolean earlierThanNote;

    public JudgeResult(double noteTiming, double inputTiming) {
        double delta = noteTiming - inputTiming;
        if (Math.abs(noteTiming - inputTiming) >= JudgeTiming.good.v()) {//miss
            hitOnNote = false;
            exactLevel = 3;//unreachable
            return;
        }
        hitOnNote = true;
        earlierThanNote = delta > 0f;
        delta = Math.abs(delta);
        if (delta < JudgeTiming.perfect.v()) {
            exactLevel = 0;
            return;
        }
        if (delta < JudgeTiming.great.v()) {
            exactLevel = 1;
            return;
        }
        if (delta < JudgeTiming.good.v()) {
            exactLevel = 2;
            return;
        }
        return;
    }

    public JudgeResult(double noteTiming, double inputTiming, double starScale) {
        double delta = noteTiming - inputTiming;
        if (Math.abs(noteTiming - inputTiming) >= JudgeTiming.good.v() * starScale) {//miss
            hitOnNote = false;
            exactLevel = 3;
            return;
        }
        hitOnNote = true;
        earlierThanNote = delta > 0f;
        delta = Math.abs(delta);
        if (delta < JudgeTiming.perfect.v() * starScale) {
            exactLevel = 0;
            return;
        }
        if (delta < JudgeTiming.great.v() * starScale) {
            exactLevel = 1;
            return;
        }
        if (delta < JudgeTiming.good.v() * starScale) {
            exactLevel = 2;
            return;
        }
        return;
    }
}
