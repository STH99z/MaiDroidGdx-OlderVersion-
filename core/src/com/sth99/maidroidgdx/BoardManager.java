package com.sth99.maidroidgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.StringBuilder;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;

/**
 * Created by STH99 on 2016/10/5.
 */

public final class BoardManager {

    private static PathCheckPoint pathCheckPoint;
    private static MultiPath multiPath;

    public enum RotDirrection {
        clockwise,
        counterClockwise
    }

    public enum GameScene {
        none,
        autoPlay, normalPlay, selectSong, settings
    }

    private static boolean exitRender = false;
    private static final int BPMSPMUL = 4;
    public static boolean hitToStart = false;
    public static final int chE = (int) 'E';       //end
    public static final int chQ = (int) 'q';       //start- circle inner circle-end
    public static final int chP = (int) 'p';       //start- circle inner circle-end
    public static final int chH = (int) 'h';       //hold
    public static final int chV = (int) 'v';       //start-center-end
    public static final int chZ = (int) 'z';       //start-z shape-end
    public static final int chS = (int) 's';       //start-s shape-end
    public static final int chB = (int) 'b';       //break
    public static final int ch0 = (int) '0';
    public static final int ch1 = (int) '1';
    public static final int chComma = (int) ',';   //next beat
    public static final int chPeriod = (int) '.';
    public static final int chSlash = (int) '/';   //note2
    public static final int[] chParenthesis = new int[]{(int) '(', (int) ')'};
    public static final int[] chSqBrackets = new int[]{(int) '[', (int) ']'};
    public static final int[] chBrace = new int[]{(int) '{', (int) '}'};
    public static final int chDash = (int) '-';    //start-end
    public static final int chGreater = (int) '>'; //start-c_Clockwise out circle-end
    public static final int chLess = (int) '<';    //start-  clockwise out circle-end
    public static final int chColon = (int) ':';   //spbs : length
    public static final String[] ratingP = {
            "SSS", "SS", "S", "AAA", "AA", "A", "B", "C", "D", "E", "F"};
    public static final float[] ratingPScore = {
            100f, 99f, 97f, 94f, 90f, 80f, 60f, 40f, 20f, 10f, 0f};
    public static final String[] ratingOP = {
            "SS", "S", "AA", "AA", "A+", "A", "A-", "B+", "B", "B", "B-", "C", "C", "C", "D"};
    public static final String[] ratingOPText = {
            "FANTASTIC", "EXCELLENT", "GREAT", "GREAT", "NICE", "CLEAR", "CLEAR", "FAILED", "FAILED", "FAILED", "FAILED", "FAILED", "FAILED", "FAILED", "FAILED"};
    public static final float[] ratingOPScore = {
            100f, 97f, 95f, 94f, 90f, 85f, 80f, 75f, 70f, 65f, 55f, 50f, 30f, 20f, 0f
    };
    public static int[] scores = new int[4];//Tap Hold Slide Break
    public static int[] hitCounts = new int[4];//Per Great Good Miss
    public static int maxCombo, currCombo;

    static ArrayList<MaiBase> noteList = new ArrayList<MaiBase>(512);
    /**
     * a note queue for each side of game board.
     * when a note(note,hold,star) is about to show,
     * it should be first added to noteQueue[side].
     */
    static ArrayList<MaiBase>[] noteQueue = new ArrayList[]{
            new ArrayList<MaiBase>(16), new ArrayList<MaiBase>(16),
            new ArrayList<MaiBase>(16), new ArrayList<MaiBase>(16),
            new ArrayList<MaiBase>(16), new ArrayList<MaiBase>(16),
            new ArrayList<MaiBase>(16), new ArrayList<MaiBase>(16)
    };
    /**
     * when a star in one of the noteQueue is hit,
     * it is removed from the queue and moved in this
     * starList
     */
    static ArrayList<MaiStar> starList = new ArrayList<MaiStar>(16);

    public static float noteDispTime = 0.8f;//note display time, from start drawing to end draw
    public static float noteScalingTime = 0.3f;//note scale time, from start drawing to start moving
    public static float bpm = 100f;         //beats per minute
    private static int beatSeperate = 4;    //seperate each beat

    public static double getSpb() {
        return 60d / bpm;
    }

    private static double spb = 0.6d;       //second per beat

    public static double getSpbs() {
        return 60d / bpm / 4;
    }

    private static double spbs = 0.15d;     //second per beatSeperate
    public static int listSize = 0;
    public static GameScene gameScene = GameScene.none;

    private static FileHandle mapdata;
    private static BufferedReader buf;

    public static void init() {
        hitToStart = false;
        initStats();
    }

    private static void initStats() {
        for (int i = 0; i < scores.length; i++) {
            scores[i] = 0;
        }
        for (int i = 0; i < hitCounts.length; i++) {
            hitCounts[i] = 0;
        }
        maxCombo = 0;
        currCombo = 0;
    }

    public static void comboAdd() {
        currCombo++;
        if (currCombo > maxCombo)
            maxCombo = currCombo;
    }

    public static void comboReset() {
        currCombo = 0;
    }

    /**
     * <p>
     * ボタンを押す（または画面をタッチする）タイミングで判定する。
     * </p>
     * <p>PERFECT 500点</p>
     * <p>GREAT 400点</p>
     * <p>GOOD 250点</p>
     * <p>MISS 0点</p>
     */
    private static int[] SCORE_TAP = {500, 400, 250, 0};

    /**
     * <p>他のノーツと違い判定基準が細かく、2f毎に判定を行う。</p>
     * <p>maimaiPLUS（Ver.1.10）へのバージョンアップによって下記のように判定を改められた</p>
     * <p>BONUS 2600～2500 (PERFECT) 2600点、2550点、2500点</p>
     * <p>BONUS 2000～1250 (GREAT) 2000点、1500点、1250点</p>
     * <p>BONUS 1000 (GOOD) 1000点</p>
     * <p>MISS 0点</p>
     * <p>maimaiPiNKより、スライド始点の☆もBREAKとして出てくるようになった。</p>
     * <p>他のノーツとは違い、ヒットエフェクトは☆が重なったものになっている。</p>
     * <p>(ver1.1より)色が水色、ピンク色、緑色となっている。得点7つで全てSOUND EFFECTの効果音が違い、点数が低いほど音程や音量が低くなるが、高い点数になるほど響くような綺麗な音が鳴る。2600点のみ歓声が起きる。</p>
     */
    private static int[] SCORE_BREAK = {2600, 2550, 2500, 2000, 1500, 1250, 1000, 0};

    /**
     * <p>はじめにボタンを叩く（または画面をタッチする）ことによる得点(つまり☆のタップ・タッチ)はTAPの項目に含まれる。</p>
     * <p>直線のSLIDEは5つ/曲線のSLIDEは4つの判定ポイントがあり、始点から終点に向かって順番にタッチしなければ判定されない。</p>
     * <p>タイミング判定は終点まで取り切った時に行われる。判定幅が他と比べてかなり長く、半拍(0.25秒?)程度のズレならJUSTで取れる。</p>
     * <p>スライド速度にかかわらず最後の1箇所をタッチできずに残すと強制的にLATE GOODになり、2つ以上残った場合TOO LATEになりミス。</p>
     * <p>純粋に速いとFast Great 、逆に遅いとLate Great となる。</p>
     * <p>JUST(PERFECT) 1500点</p>
     * <p>FAST or LATE(GREAT) 1200点</p>
     * <p>FAST or LATE(GOOD) 750点</p>
     * <p>TOO LATE(MISS) 0点</p>
     */
    private static int[] SCORE_SLIDE = {1500, 1200, 750, 0};//// FIXME: 2016/12/4 总分和逐个加不对应

    /**
     * @param note
     * @param type 0=Per 1=Great 2=Good 3=Miss
     */
    public static void scoreAdd(MaiNote note, int type) {
        scoreAddNoCombo(note, type);
        int missIndex = 3;
        if (note.isBreak)
            missIndex = 7;
        if (type != missIndex)
            comboAdd();
        else
            comboReset();
    }

    public static void scoreAddNoCombo(MaiNote note, int type) {
        //Tap Hold Slide Break
        int s;
        int missIndex = 3;
        boolean needAdd = true;
        int scoreSetIndex;
        if (note.isBreak) {
            missIndex = 7;
            s = SCORE_BREAK[type];
            scoreSetIndex = 3;
            needAdd = false;
            int hc = 0;
            if (type == 7)
                hc = 3;
            else if (type == 6)
                hc = 2;
            else if (type > 2)
                hc = 1;
            hitCounts[hc]++;
        } else if (note instanceof MaiStar) {
            s = SCORE_SLIDE[type];
            scoreSetIndex = 2;
        } else if (note instanceof MaiHold) {
            s = SCORE_TAP[type];
            scoreSetIndex = 1;
        } else {
            s = SCORE_TAP[type];
            scoreSetIndex = 0;
        }
        scores[scoreSetIndex] += s;
        if (needAdd)
            hitCounts[type]++;
    }

    public static boolean getShouldExit() {
        return exitRender;
    }

    private static String itoa(int v) {
        return v + "";
    }

    private static int atoi(String s) {
        return Integer.parseInt(s);
    }

    private static int cton(int ch) {
        return ch - ch0;
    }

    private static boolean isNumber(int ch) {
        int n = cton(ch);
        return n >= 0 && n < 10;
    }

    /**
     * read bpm from text like "(180)"
     *
     * @return true if success
     */
    private static boolean readBPM() {
        try {
            if (buf.read() != chParenthesis[0])
                return false;
            int chRead;
            String a = "";
            while ((chRead = buf.read()) != chParenthesis[1]) {
                a += (char) chRead;
            }
            bpm = atoi(a);
            spb = 60d / bpm;
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * read beatsseperate from text like {"16}"
     *
     * @return true if success
     */
    private static boolean readBeats() {
        try {
            int chRead;
            String a = "";
            while ((chRead = buf.read()) != chBrace[1]) {
                a += (char) chRead;
            }
            beatSeperate = atoi(a);
            spbs = spb / beatSeperate * BPMSPMUL;
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * read a from text like 4h"[16:3]"
     *
     * @return a MaiHold instance if success
     */
    private static MaiHold readHold(double timing, byte side) {
        try {
            double t2 = readLastingTime();
            return new MaiHold(timing, side, timing + t2);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * read beatsseperate from text like 1<"8[4:4]"
     *
     * @param starType chRead from buf, can be int values of -,v,>,<,p,q
     * @return a MaiStar instance if success
     */
    private static MaiStar readStar(double timing, byte side, int starType) {
        try {
            byte side2 = (byte) (buf.read() - ch1);
            double t2 = readLastingTime();
            switch (starType) {
                default:
                    throw new IllegalStateException("starType \"" + (char) starType + "\" is not a valid type.");
                case chDash:
                case chV:
                case chGreater:
                case chLess:
                case chP:
                case chQ:
                case chS:
                case chZ:
                    break;
            }
            PathCheckPoint pcp = new PathCheckPoint(side, side2, starType);
            MultiPath mp = MultiPath.CreatePath(side, side2, starType);
            return new MaiStar(timing, side, timing + spb, timing + spb + t2,//// FIXME: 2016/11/5
                    pcp,
                    mp,
                    pcp.generateStarEndEff());
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * read a time from SqBrackets like "[16:3]"
     *
     * @return lasting time
     */
    private static double readLastingTime() {
        try {
            int ch;
            int sp;
            int len;
            StringBuilder s = new StringBuilder(3);
            if ((ch = buf.read()) != chSqBrackets[0])
                return Double.NaN;
            while ((ch = buf.read()) != chColon) {
                s.append((char) ch);
            }
            sp = atoi(s.toString());
            s = new StringBuilder(3);
            while ((ch = buf.read()) != chSqBrackets[1]) {
                s.append((char) ch);
            }
            len = atoi(s.toString());
            return spb / sp * BPMSPMUL * len;
        } catch (Throwable e) {
            e.printStackTrace();
            return Double.NaN;
        }
    }

    public static void readMaidata(String internalFilepath) {
        try {
            //TODO try writing a parser lexer and tokenizer
            mapdata = Gdx.files.internal(internalFilepath);
            buf = mapdata.reader(64);
            int noteReading = -1;
            MaiNote[] noteTemp = new MaiNote[]{new MaiNote(0d, (byte) 0), new MaiNote(0d, (byte) 0)};
            MaiBase[] mn = new MaiBase[2];
            double curTiming = 1d + Engine.noteTimingOffset;
            noteList.clear();
            if (!readBPM())
                return;
            int chRead;
            while ((chRead = buf.read()) != -1) {
                //System.out.print((char) chRead);
                if (chRead == chE)
                    break;
                if (chRead == chBrace[0]) {
                    readBeats();
                    continue;
                }
                if (chRead == chComma) {
                    //dont change this part code
                    for (int i = 0; i <= noteReading; i++) {
                        noteTemp[i].setTiming(curTiming);
                    }
                    if (noteReading == 1) {
                        noteTemp[0].setOtherNote(noteTemp[1]);
                        noteTemp[1].setOtherNote(noteTemp[0]);
                    }
                    for (int i = 0; i <= noteReading; i++) {
                        mn[i] = noteTemp[i].clone();
                        noteList.add(mn[i]);
                    }
                    for (int i = 0; i <= noteReading; i++) {
                        if (noteTemp[i] instanceof MaiHold || noteTemp[i] instanceof MaiStar)
                            noteTemp[i] = new MaiNote(0d, (byte) 0);
                        else
                            noteTemp[i].reset();
                    }
                    if (noteReading == 1) {
                        mn[0].setOtherNote(mn[1]);
                        mn[1].setOtherNote(mn[0]);
                        //System.out.println(mn[0] + " " + mn[1]);
                    }
                    curTiming += spbs;
                    noteReading = -1;
                    continue;
                }
                if (isNumber(chRead)) {
                    if (noteReading == -1) {
                        noteReading = 0;
                        noteTemp[0].side = (byte) (cton(chRead) - 1);
                    } else {
                        noteReading = 1;
                        noteTemp[1].side = (byte) (cton(chRead) - 1);
                    }
                    continue;
                }
                if (chRead == chSlash) {
                    noteReading = 1;
                    continue;
                }
                if (chRead == chB) {
                    noteTemp[noteReading].isBreak = true;
                    continue;
                }
                if (chRead == chH) { //MaiHold
                    noteTemp[noteReading] = readHold(curTiming, noteTemp[noteReading].side);
                    continue;
                }
                if (chRead == chDash || //MaiStar
                        chRead == chV ||
                        chRead == chZ ||
                        chRead == chS ||
                        chRead == chGreater ||
                        chRead == chLess ||
                        chRead == chP ||
                        chRead == chQ) {
                    noteTemp[noteReading] = readStar(curTiming, noteTemp[noteReading].side, chRead);
                    continue;
                }
            }
            buf.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        listSize = noteList.size();
        indexReadIn = 0;
        EffectManager.clearEffectList();
    }

    /**
     * play map with auto play, means no miss and all perfect
     */
    public static void playMapAuto() {
        exitRender = false;
        gameScene = GameScene.autoPlay;
    }

    public static void playMap() {
        exitRender = false;
        gameScene = GameScene.normalPlay;
    }

    /**
     * wrapper for Gdx.render() function.
     */
    public static void frameProcessFunc() {
        if (!hitToStart) {
            Engine.frameRefresh();
            Engine.procInput();
            Engine.batch.begin();
            Engine.drawText("hit screen when ready", 0, 0);
            Engine.batch.end();
            if (Engine.getInputTouched(0)) {
                hitToStart = true;
                Engine.bgm.play();
                if (Engine.isPlayHitSE || Engine.isPlayStarSE) {
                    Engine.seHit.loop(0f);
//                  Engine.seStar.loop(0f);
                }
            }
            return;
        }
        switch (gameScene) {
            case autoPlay:
                autoPlayFrameRender();
                break;
            case normalPlay:
                playFrameRender();
                break;
            case selectSong:

                break;
            case settings:

                break;
            case none:
                //nothing here
                break;
        }
    }

    static int indexReadIn = 0;

    private static void autoPlayFrameRender() {
        //checked if should exit
        if (!Engine.bgm.isPlaying()) {
            gameScene = GameScene.none;
            exitRender = true;
            return;
        }

        //do basic frame refresh works
        Engine.frameRefresh();
        Engine.procInput();

        //do all the calc and draw
        Engine.batch.begin();
        drawDebugInfo();
        Engine.drawImageCC(Engine.imgFrame[4], Engine.BOARD_CENTER);

        PointF leftBottom = Engine.getScreenCorner(true, false);
        Engine.drawText("test combo:" + currCombo, leftBottom.x, leftBottom.y + 30);

        readNotesIntoQueues();
        drawNotesInQueue();
        drawStarsInList();
        EffectManager.drawEffectList();

        Engine.batch.end();
    }

    private static void playFrameRender() {
        //checked if should exit
        if (!Engine.bgm.isPlaying()) {
            gameScene = GameScene.none;
            exitRender = true;
            return;
        }

        //do basic frame refresh works
        Engine.frameRefresh();
        Engine.procInput();
        if (Engine.useAccuratejudgementMethod) {
            InputManager.updateAllArea2();
            InputManager.drawInputAreaStatus2();
        } else {
            InputManager.updateAllArea();
            InputManager.drawInputAreaStatus();
        }

        //do all the calc and draw
        Engine.batch.begin();
        drawDebugInfo();
        Engine.drawImageCC(Engine.imgFrame[4], Engine.BOARD_CENTER);
        Engine.drawText(Engine.bmpFontBig,"Combo", 0, 0);
        Engine.drawText(Engine.bmpFontBig, BoardManager.currCombo, 0, -Engine.bmpFontBig.getLineHeight());

        readNotesIntoQueues();
        drawStarsInList();
        drawNotesInQueue();
        EffectManager.drawEffectList();

        Engine.batch.end();
    }

    private static void drawNotesInQueue() {
        double bgmPos = Engine.getBgmPosition();
        for (int i = 0; i < noteQueue.length; i++) {
            ArrayList<MaiBase> list = noteQueue[i];
            if (list.size() == 0) {
                if (InputManager.getArea(i).getInputStatus() == InputStatus.justTouched) {
                    EffectManager.addToDrawList(new RippleEffect(Engine.BOARD_POSITION[i]));
                }
                continue;
            }
            MaiBase m = list.get(0);
            m.calcPos(bgmPos);
            m.checkHit(bgmPos);
            if (m.remaining(bgmPos)) {
                //m.drawNote();
                Engine.drawTextDebug(m.toString());
                //Star转移存储
                if (m instanceof MaiStar) {
                    if (((MaiStar) m).getAlreadyHit()) {
                        starList.add((MaiStar) m);
                        list.remove(0);
                    }
                }
            } else {
                list.remove(0);
            }
            for (int j = 0; j < list.size(); j++) {
                m = list.get(j);
                m.calcPos(bgmPos);
                m.drawNote();
            }
        }
    }

    private static void drawStarsInList() {
        double bgmPos = Engine.getBgmPosition();
        for (int i = 0; i < starList.size(); i++) {
            MaiStar m = starList.get(i);
            m.calcPos(bgmPos);
            m.checkHit(bgmPos);
            if (m.remaining(bgmPos)) {
                m.drawNote();
                Engine.drawTextDebug(m.toString());
            } else {
                starList.remove(i--);
            }
        }
    }

    private static void readNotesIntoQueues() {
        while (indexReadIn < listSize) {
            MaiBase m = noteList.get(indexReadIn);
            if (m.getTiming() - BoardManager.noteDispTime <= Engine.getBgmPosition()) {
                //因为MaiStar的第一下和note判定还是一样的，所以也放到Queue里边，
                //如果按下了第一下，再放到StarList里边。
                //否则会和hold冲突，详见Cycles紫谱。

//                @deprecated
//                if (m instanceof MaiStar) {
//                    starList.add((MaiStar) m);
//                } else {
//                    noteQueue[m.getSide()].add(m);
//                }
                noteQueue[m.getSide()].add(m);
                indexReadIn++;
            } else {
                break;
            }
        }
    }

    private static void drawDebugInfo() {
        String screenOrientation = Engine.scrWidth > Engine.scrHeight ? "Landscape," : "Portrait,";
        screenOrientation += "W=" + Gdx.graphics.getWidth() + ",H=" + Gdx.graphics.getHeight();
        Engine.drawTextDebug(screenOrientation);
        int poolSize = 0;
        for (int i = 0; i < Engine.BOARD_SIDE; i++) {
            poolSize += noteQueue[i].size();
        }
        Engine.drawTextDebug(Gdx.graphics.getFramesPerSecond());
        Engine.drawTextDebug("tot:" + BoardManager.listSize + " pool:" + poolSize);
        Engine.drawTextDebug("idxR:" + indexReadIn);
    }

    public static void dispose() {
        for (ArrayList<MaiBase> list :
                noteQueue) {
            list.clear();
        }
        starList.clear();
    }

}

