package com.sth99.maidroidgdx;

/**
 * Created by STH99 on 2016/10/3.
 */

public class MaiHold extends MaiNote implements MaiBase {
    protected double timing2 = 0d;
    protected float pos2 = 0f;
    protected boolean sePlayed2 = false;
    protected boolean alreadyHit2 = false;

    public double getTiming2() {
        return timing2;
    }

    public MaiHold(double timing, byte side, double timing2) {
        super(timing, side);
        this.timing2 = timing2;
    }

    public MaiHold(double timing, byte side, boolean isBreak, boolean isBoth, MaiBase other, double timing2) {
        super(timing, side, isBreak, isBoth, other);
        img = Engine.imgHold[0];
        if (isBoth) {
            img = Engine.imgHold[1];
        }
        this.timing2 = timing2;
    }

    @Override
    public void autoJudge(double audioTiming) {
        //hold tail hit
        super.autoJudge(audioTiming);
        if (pos2 >= 1f && !alreadyHit2) {
            sePlayed2 = true;
            alreadyHit2 = true;
            //TODO auto judge logic(hold)
            Engine.playSeHit();
            EffectManager.addNoteHitEffect(NoteHitEffect.Type.perfect, side);
            BoardManager.scoreAdd(this, 0);
        }
    }

    @Override
    public void checkHit(double audioTiming) {
        //hold head hit
        if (BoardManager.gameScene == BoardManager.GameScene.autoPlay) {
            autoJudge(audioTiming);
            return;
        }
        judge(audioTiming + Engine.judgeTimingOffset);
    }

    @Override
    public void calcPos(double audioTiming) {
        if (audioTiming > timing) {
            pos = 1.0f;
        } else {
            super.calcPos(audioTiming);
        }
        if (audioTiming < timing2 - BoardManager.noteDispTime) {
            pos2 = 0f;
        } else {
            pos2 = (float) ((audioTiming - (timing2 - BoardManager.noteDispTime)) / BoardManager.noteDispTime);
        }
    }

    @Override
    public void reset() {
        super.reset();
        timing2 = 0d;
        pos2 = 0f;
        sePlayed2 = false;
    }


    //0px-38px-78px-106px
    @Override
    public MaiHold clone() {
        return new MaiHold(timing, side, isBreak, isBoth, otherNote, timing2);
    }

    //Top- pos-pos2-Bot
    //scale=(500-76)/40
    private static final float bdScale = 400;

    @Override
    public boolean drawNote() {
        if (pos < 0f || pos > 1f)
            return false;
        drawArc();
        float ptemp = pos < BoardManager.noteScalingTime ? BoardManager.noteScalingTime : pos;
        float ptemp2 = pos2 < BoardManager.noteScalingTime ? BoardManager.noteScalingTime : pos2;
        if (pos < BoardManager.noteScalingTime) {
            Engine.batch.setColor(1f, 1f, 1f, pos / BoardManager.noteScalingTime);
            Engine.drawImageCC(img,
                    Engine.BOARD_POSITION[side].mul(ptemp),
                    pos / BoardManager.noteScalingTime * Engine.noteScale,
                    Engine.BOARD_ANGLE[side]);
        } else if (pos2 >= 1f) {
            Engine.drawImageCC(img,
                    Engine.BOARD_POSITION[side].mul(1f),
                    Engine.noteScale,
                    Engine.BOARD_ANGLE[side]);
        } else {
            PointF headPos = Engine.BOARD_POSITION[side].mul(pos + 0.136f);
            PointF bodyPos = Engine.BOARD_POSITION[side].mul(ptemp2 + 0.076f);
            PointF tailPos = Engine.BOARD_POSITION[side].mul(ptemp2);
//            Engine.drawImageCC(img,
//                    Engine.BOARD_POSITION[side].mul(ptemp),
//                    1f,
//                    Engine.BOARD_ANGLE[side]);
            //hold head
            Engine.batch.draw(img,
                    +headPos.x - img.getWidth() / 2,
                    +headPos.y - img.getHeight() / 2,
                    img.getWidth() / 2, img.getHeight() / 2,//origin xy (scale and rot, the image
                    img.getWidth(), 38,                     //target wh  still be drawn originates
                    Engine.noteScale, 1f,                                 //scale xy   from left bottom)
                    -Engine.BOARD_ANGLE[side],               //rotation
                    0, 0,                                   //src xy
                    img.getWidth(), 38,                     //src wh
                    false, false);                          //flip xp
            //hold body
            Engine.batch.draw(img,
                    +bodyPos.x - img.getWidth() / 2,
                    +bodyPos.y - img.getHeight() / 2,
                    img.getWidth() / 2, img.getHeight() / 2,
                    img.getWidth(), bdScale * (pos - ptemp2 + 0.06f) / 0.8f,
                    Engine.noteScale, 1f,
                    -Engine.BOARD_ANGLE[side],
                    0, 39,
                    img.getWidth(), 30,
                    false, false);
            //hold tail
            Engine.batch.draw(img,
                    +tailPos.x - img.getWidth() / 2,
                    +tailPos.y - img.getHeight() / 2,
                    img.getWidth() / 2, img.getHeight() / 2,
                    img.getWidth(), 38,
                    Engine.noteScale, 1f,
                    -Engine.BOARD_ANGLE[side],
                    0, 68,
                    img.getWidth(), 38,
                    false, false);
        }
        Engine.batch.setColor(1f, 1f, 1f, 1f);
        return true;
    }

    @Override
    public void judge(double audioTiming) {
        super.judge(audioTiming);
        if (!alreadyHit && audioTiming > timing + JudgeTiming.good.v()) {
            //first hit failed
            alreadyHit2 = true;
            return;
        }
        if (alreadyHit && !alreadyHit2) {
            //should checked hold and release
            if (!alreadyHit2 && audioTiming > timing2 + JudgeTiming.good.v()) {
                EffectManager.addNoteHitEffect(NoteHitEffect.Type.miss, side);
                BoardManager.scoreAdd(this, 3);
                return;
            }
            //checked release first
            if (InputManager.getArea(side).getInputStatus() == InputStatus.justRelease) {
                alreadyHit2 = true;
                JudgeResult r = new JudgeResult(timing2, audioTiming);
                if (!(r.hitOnNote)) {
                    //release too fast
                    EffectManager.addNoteHitEffect(NoteHitEffect.Type.miss, side);
                    BoardManager.scoreAdd(this, 3);
                    return;
                }
                if (r.exactLevel == 0) {
                    Engine.playSeHit();
                    EffectManager.addNoteHitEffect(NoteHitEffect.Type.perfect, side);
                    BoardManager.scoreAdd(this, 0);
                    return;
                }
                if (r.exactLevel == 1) {
                    Engine.playSeHit();
                    EffectManager.addNoteHitEffect(NoteHitEffect.Type.great, side);
                    BoardManager.scoreAdd(this, 1);
                    return;
                }
                if (r.exactLevel == 2) {
                    Engine.playSeHit();
                    EffectManager.addNoteHitEffect(NoteHitEffect.Type.good, side);
                    BoardManager.scoreAdd(this, 2);
                    return;
                }
            } else if (InputManager.getArea(side).isTouched()) {//then checked hold
                //TODO make ripples or other
                //// FIXME: 2016/12/4
            }
        }
    }

    @Override
    public boolean remaining(double bgmTime) {
        return !alreadyHit2 && bgmTime < timing2 + JudgeTiming.good.v();
    }

    @Override
    public String toString() {
        String s = super.toString();
        return "hold:" + s.substring(5);
    }

    @Override
    public Effect createHitEffect() {
        if (pos2 <= 1f) {
            EffectManager.addToDrawList(new HoldEffect(
                    (int) ((timing2 - timing) / (1d / 60)),
                    Engine.BOARD_POSITION[side],
                    Engine.BOARD_ANGLE[side]));
        }
        return super.createHitEffect();
    }
}
