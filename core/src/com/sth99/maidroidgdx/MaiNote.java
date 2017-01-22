package com.sth99.maidroidgdx;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by STH99 on 2016/10/3.
 */

public class MaiNote implements MaiBase {
    protected static float note_appear_pos = 0.25f;

    /**
     * the exact timing of the note be pressed
     */
    protected double timing;
    /**
     * the inputSide of the note. ranging from 0 to 7.
     */
    protected byte side;

    protected boolean alreadyHit = false;
    protected float noteRotationAngle = 0f;

    /**
     * the pos of note from board center to edge, or from start to end
     */
    protected float pos = 0f;
    protected boolean sePlayed = false;
    public boolean isBoth = false;
    public boolean isBreak = false;
    private boolean bothArcDrawed = false;
    public MaiBase otherNote = null;
    public Texture img = Engine.imgNote[0];
    public Texture[] imgEdge = Engine.imgEdge[0];

    public MaiNote(double timing, byte side) {
        this.timing = timing;
        this.side = side;
    }

    public MaiNote(double timing, byte side, boolean isBreak, boolean isBoth, MaiBase other) {
        this.timing = timing;
        this.side = side;
        this.isBreak = isBreak;
        this.isBoth = isBoth;
        if (isBoth) {
            this.otherNote = other;
            img = Engine.imgNote[1];
            imgEdge = Engine.imgEdge[1];
        }
        if (isBreak) {
            img = Engine.imgBreak;
            imgEdge = Engine.imgEdge[2];
        }
    }

    public void calcPos(double audioTiming) {
        pos = (float) ((audioTiming - (timing - BoardManager.noteDispTime)) / BoardManager.noteDispTime);
    }

    public void reset() {
        timing = 0d;
        side = -1;
        isBreak = false;
        isBoth = false;
        otherNote = null;
        bothArcDrawed = false;
        sePlayed = false;
    }

    @Override
    public MaiNote clone() {
        return new MaiNote(timing, side, isBreak, isBoth, otherNote);
    }

    @Override
    public boolean drawNote() {
        if (pos < 0f || pos > 1f)
            return false;
        drawArc();
        float posSplit = BoardManager.noteScalingTime / BoardManager.noteDispTime;
        float ptemp = pos < posSplit ?
                note_appear_pos :
                note_appear_pos + (pos - posSplit) / (1f - posSplit) * (1 - note_appear_pos);
        if (pos < posSplit) {
            Engine.batch.setColor(1f, 1f, 1f, pos / posSplit);
            Engine.drawImageCC(img,
                    Engine.BOARD_POSITION[side].mul(ptemp),
                    pos / posSplit * Engine.noteScale,
                    -(Engine.BOARD_ANGLE[side] + noteRotationAngle));
        } else {
            Engine.drawImageCC(img,
                    Engine.BOARD_POSITION[side].mul(ptemp),
                    Engine.noteScale,
                    -(Engine.BOARD_ANGLE[side] + (isBreak ? ptemp * 720f : 0f) + noteRotationAngle));
        }
        Engine.batch.setColor(1f, 1f, 1f, 1f);
        return true;
    }

    @Override
    public void setTiming(double timing) {
        this.timing = timing;
    }

    @Override
    public void setSide(byte side) {
        this.side = side;
    }

    @Override
    public void checkHit(double audioTiming) {
        if (BoardManager.gameScene == BoardManager.GameScene.autoPlay) {
            autoJudge(audioTiming);
            return;
        }
        judge(audioTiming + Engine.judgeTimingOffset);
    }

    protected void drawArc() {
        float posSplit = BoardManager.noteScalingTime / BoardManager.noteDispTime;
        float ptemp = pos < posSplit ?
                note_appear_pos :
                note_appear_pos + (pos - posSplit) / (1f - posSplit) * (1 - note_appear_pos);
        if (isBoth) {
            if (bothArcDrawed) {
                bothArcDrawed = false;
                ((MaiNote) otherNote).bothArcDrawed = false;
            } else {
                bothArcDrawed = true;
                ((MaiNote) otherNote).bothArcDrawed = true;
                int side1, side2, delta;
                if (side < otherNote.getSide()) {
                    side1 = side;
                    side2 = otherNote.getSide();
                } else {
                    side2 = side;
                    side1 = otherNote.getSide();
                }
                delta = side2 - side1;
                if (delta == 4) {
                    Engine.drawImageCC(Engine.imgArc[3],
                            Engine.BOARD_CENTER,
                            ptemp,
                            -Engine.BOARD_ANGLE[side1 + 2]);
                    Engine.drawImageCC(Engine.imgArc[3],
                            Engine.BOARD_CENTER,
                            ptemp,
                            -Engine.BOARD_ANGLE[(side2 + 2) % 8]);
                } else if (delta < 4) {
                    Engine.drawImageCC(Engine.imgArc[delta - 1],
                            Engine.BOARD_CENTER,
                            ptemp,
                            -(Engine.BOARD_ANGLE[side1] + Engine.BOARD_ANGLE[side2]) / 2);
                    Engine.batch.draw(imgEdge[0],
                            -500f + Engine.BOARD_POSITION[side1].x * ptemp,
                            -500f + Engine.BOARD_POSITION[side1].y * ptemp,
                            imgEdge[0].getWidth(), imgEdge[0].getHeight(),
                            500f, 500f,
                            ptemp, ptemp,
                            -Engine.BOARD_ANGLE[side1],
                            0, 0, 500, 500, false, false);
                    Engine.batch.draw(imgEdge[1],
                            +Engine.BOARD_POSITION[side2].x * ptemp,
                            -500f + Engine.BOARD_POSITION[side2].y * ptemp,
                            0, imgEdge[1].getHeight(),
                            500f, 500f,
                            ptemp, ptemp,
                            -Engine.BOARD_ANGLE[side2],
                            0, 0, 500, 500, false, false);
                } else {
                    Engine.drawImageCC(Engine.imgArc[7 - delta],
                            Engine.BOARD_CENTER,
                            ptemp,
                            -(Engine.BOARD_ANGLE[side1] + Engine.BOARD_ANGLE[side2]) / 2 - 180f);
                    Engine.batch.draw(imgEdge[0],
                            -500f + Engine.BOARD_POSITION[side2].x * ptemp,
                            -500f + Engine.BOARD_POSITION[side2].y * ptemp,
                            imgEdge[0].getWidth(), imgEdge[0].getHeight(),
                            500f, 500f,
                            ptemp, ptemp,
                            -Engine.BOARD_ANGLE[side2],
                            0, 0, 500, 500, false, false);
                    Engine.batch.draw(imgEdge[1],
                            +Engine.BOARD_POSITION[side1].x * ptemp,
                            -500f + Engine.BOARD_POSITION[side1].y * ptemp,
                            0, imgEdge[1].getHeight(),
                            500f, 500f,
                            ptemp, ptemp,
                            -Engine.BOARD_ANGLE[side1],
                            0, 0, 500, 500, false, false);
                }
            }
        } else {
            Engine.batch.draw(imgEdge[0],
                    -500f + Engine.BOARD_POSITION[side].x * ptemp,
                    -500f + Engine.BOARD_POSITION[side].y * ptemp,
                    imgEdge[0].getWidth(), imgEdge[0].getHeight(),
                    500f, 500f,
                    ptemp, ptemp,
                    -Engine.BOARD_ANGLE[side],
                    0, 0, 500, 500, false, false);
            Engine.batch.draw(imgEdge[1],
                    +Engine.BOARD_POSITION[side].x * ptemp,
                    -500f + Engine.BOARD_POSITION[side].y * ptemp,
                    0, imgEdge[1].getHeight(),
                    500f, 500f,
                    ptemp, ptemp,
                    -Engine.BOARD_ANGLE[side],
                    0, 0, 500, 500, false, false);
        }
    }

    @Override
    public void judge(double audioTiming) {
        if (!alreadyHit && audioTiming > timing + JudgeTiming.good.v()) {
            //didnt touch the note at all
            EffectManager.addNoteHitEffect(NoteHitEffect.Type.miss, side);
            BoardManager.scoreAdd(this, 3);
            return;
        }
        if (!alreadyHit && InputManager.getArea(side).getInputStatus() == InputStatus.justTouched) {
            JudgeResult r = new JudgeResult(timing, audioTiming);
            if (r.hitOnNote) {
                alreadyHit = true;
                Engine.playSeHit();
                if (r.exactLevel == 0) {
                    EffectManager.addNoteHitEffect(NoteHitEffect.Type.perfect, side);
                    BoardManager.scoreAdd(this, 0);
                    return;
                }
                if (r.exactLevel == 1) {
                    EffectManager.addNoteHitEffect(NoteHitEffect.Type.great, side);
                    BoardManager.scoreAdd(this, 1);
                    return;
                }
                if (r.exactLevel == 2) {
                    EffectManager.addNoteHitEffect(NoteHitEffect.Type.good, side);
                    BoardManager.scoreAdd(this, 2);
                    return;
                }
                //TODO 把得点算出来，然后吧MaiBase加个接口叫getScore？
                //TODO 或者直接通过判断操作加BoardManager的总分
            } else {
                EffectManager.addToDrawList(new RippleEffect(Engine.BOARD_POSITION[side]));
            }
        }
    }

    @Override
    public void autoJudge(double audioTiming) {
        if (pos >= 1f && !alreadyHit) {
            sePlayed = true;
            alreadyHit = true;
            Engine.playSeHit();
            EffectManager.addNoteHitEffect(NoteHitEffect.Type.perfect, side);
            if (this instanceof MaiStar) {
                BoardManager.scoreAdd(this, 0);
            } else if (this instanceof MaiHold) {
                BoardManager.scoreAddNoCombo(this, 0);
            } else {
                BoardManager.scoreAdd(this, 0);
            }
        }
    }

    @Override
    public boolean remaining(double bgmTime) {
        return !alreadyHit && bgmTime < timing + JudgeTiming.good.v();
    }

    @Override
    public String toString() {
        return "note:" + (float) timing + ",s" + side + (isBreak ? "b" : "") + (isBoth ? "-s" + otherNote.getSide() : "");
    }

    @Override
    public double getTiming() {
        return timing;
    }

    @Override
    public byte getSide() {
        return side;
    }

    @Override
    public void setOtherNote(MaiBase otherNote) {
        this.otherNote = otherNote;
        this.isBoth = true;
    }

    @Override
    public Effect createHitEffect() {
        Effect e = new NoteHitEffect(
                NoteHitEffect.Type.perfect,
                Engine.BOARD_POSITION[side],
                Engine.BOARD_ANGLE[side]);
        EffectManager.addToDrawList(e);
        return e;
    }
}
