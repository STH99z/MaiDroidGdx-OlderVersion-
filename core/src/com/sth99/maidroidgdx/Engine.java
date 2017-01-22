package com.sth99.maidroidgdx;

/**
 * Created by STH99 on 2016/10/3.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.Random;

class PointF {
    public float x, y;

    public PointF(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public PointF(PointF pf) {
        x = pf.x;
        y = pf.y;
    }

    @Override
    public String toString() {
        return "{" + x + "," + y + "}";
    }

    public PointF mul(float scale) {
        return new PointF(x * scale, y * scale);
    }

    public PointF div(float scale) {
        if (scale == 0f)
            throw new ArithmeticException("div number cant be zero");
        else
            return mul(1f / scale);
    }

    public PointF add(PointF other) {
        return new PointF(x + other.x, y + other.y);
    }

    public PointF translate(float angle360, float dist) {
        return new PointF(x + (float) Math.sin(angle360 / 180 * Math.PI) * dist,
                y + (float) Math.cos(angle360 / 180 * Math.PI) * dist);
    }

    public PointF sub(PointF other) {
        return new PointF(x - other.x, y - other.y);
    }

    public double getDistance(PointF other) {
        return Math.sqrt(pow2(x - other.x) + pow2(y - other.y));
    }

    public boolean inRange(PointF center, double radius) {
        if (x + radius >= center.x &&
                x - radius <= center.x &&
                y + radius >= center.y &&
                y - radius <= center.y)
            return getDistance(center) < radius;
        return false;
    }

    private static double pow2(double v) {
        return v * v;
    }
}

public final class Engine {
    public static final float STANDARD_RADIUS = 1200f;
    public static Runnable applySettings;
    public static String mapDirName = "setsunatrip";
    public static boolean initOk = false;
    public static boolean autoPlay = true;
    public static SpriteBatch batch;
    public static Camera camera;
    public static Texture[] imgFrame = new Texture[5];
    public static Texture imgFrameEff;
    public static Texture[] imgNote = new Texture[2];
    public static Texture[] imgHold = new Texture[2];
    public static Texture[] imgStar = new Texture[2];
    public static Texture[] imgPath = new Texture[2];
    public static Texture imgBreak;
    public static Texture[] imgArc = new Texture[4];
    public static Texture[][] imgEdge = new Texture[4][2];
    public static Texture[] imgEff = new Texture[3];
    public static Texture[] imgStarEff = new Texture[6];
    public static Texture[] imgEffText = new Texture[4];
    public static Texture imgRipple;
    public static Texture imgBackground;
    private static ShapeRenderer shape;
    private static BitmapFont bmpFont;
    public static BitmapFont bmpFontBig;
    public static final Random rnd = new Random();
    public static Music bgm;
    public static Sound seHit;
    public static Sound seStar;
    private static float bgmPosition;

    public static final int POINTERS_MAX = 10;
    public static int scrWidth, scrHeight;
    public static int scrWidthHalf, scrHeightHalf;
    public static PointF scrCenter;
    public static int scrMaxRadius;
    public static float noteScale = 1.2f;
    private static float textDebugY = 0f;
    private static float textDebugX = 0f;
    public static boolean showDebugInfo = false;
    public static boolean useAccuratejudgementMethod = true;
    public static boolean isPlayHitSE = true;
    public static boolean isPlayStarSE = true;
    public static boolean useBackgroundPic = true;
    public static boolean isLandScape = true;
    public static float viewportScale = 1.0f;
    public static float combinedScale = 1.0f;
    public static float noteTimingOffset = 0f;
    public static float judgeTimingOffset = 0f;
    public static float effTextOffset = 0.1f;
    public static boolean dispFrameEff = true;

    public static final PointF BOARD_SIZE = new PointF(1000f, 1000f);
    public static final float RADIUS_SCALE_RATE = 0.38268f;
    public static final float BOARD_RADIUS = 500f;
    public static final float BOARD_RADIUS2 = BOARD_RADIUS * RADIUS_SCALE_RATE;
    public static final byte BOARD_SIDE = 8;
    public static final float[] BOARD_ANGLE = new float[BOARD_SIDE];
    public static final PointF[] BOARD_POSITION = new PointF[BOARD_SIDE];
    public static final PointF[] BOARD_POSITION2_MID = new PointF[BOARD_SIDE];
    public static final PointF[] BOARD_POSITION2 = new PointF[BOARD_SIDE];
    public static final PointF BOARD_CENTER = new PointF(0f, 0f);

    private static boolean[] touchedPrev = new boolean[POINTERS_MAX];
    private static boolean[] touched = new boolean[POINTERS_MAX];
    private static byte[] inputSidePrev = new byte[POINTERS_MAX];
    private static byte[] inputSide = new byte[POINTERS_MAX];
    private static float[] inputDist = new float[POINTERS_MAX];
    private static int[] inputX = new int[POINTERS_MAX];
    private static int[] inputY = new int[POINTERS_MAX];
    private static int totalInput;

    private static String externalDirPath;

    //runnable execute load from pref
    public static void setApplySettings(Runnable applySettings) {
        Engine.applySettings = applySettings;
    }

    public static void init() {
        initOk = false;
        initBasicData();
        initSettings();
        initEngine();

        BoardManager.init();
        InputManager.init();
        BoardManager.readMaidata("map/" + mapDirName + "/maidata.txt");
        initOk = true;

        if (autoPlay)
            BoardManager.playMapAuto();
        else
            BoardManager.playMap();
    }

    private static void initSettings() {
        applySettings.run();
    }

    private static void initEngine() {
        Gdx.graphics.setContinuousRendering(true);
        Gdx.graphics.setResizable(true);
        Gdx.graphics.setTitle("MaiDroid using libGdx");
        Gdx.graphics.setVSync(false);
        batch = new SpriteBatch();
        updateScreenRotation(true);

        //graph section
        for (int i = 0; i < imgFrame.length; i++) {
            imgFrame[i] = new Texture("game/frame" + i + ".png");
        }
        imgFrameEff = new Texture("game/frameEff.png");
        for (int i = 0; i < imgNote.length; i++) {
            imgNote[i] = new Texture("game/" + ((i == 0) ? "single" : "double") + ".png");
            imgHold[i] = new Texture("game/" + ((i == 0) ? "single" : "double") + "hold.png");
            imgStar[i] = new Texture("game/" + ((i == 0) ? "single" : "double") + "star.png");
            imgPath[i] = new Texture("game/" + ((i == 0) ? "single" : "double") + "path.png");
        }
        imgBreak = new Texture("game/break.png");
        for (int i = 0; i < imgArc.length; i++) {
            imgArc[i] = new Texture("game/arc" + i + ".png");
        }
        for (int i = 0; i < imgEdge.length; i++) {
            for (int j = 0; j < imgEdge[0].length; j++) {
                imgEdge[i][j] = new Texture("game/edge" + i + j + ".png");
            }
        }
        for (int i = 0; i < imgEff.length; i++) {//per gr go st
            imgEff[i] = new Texture("game/eff" + i + ".png");
        }
        for (int i = 0; i < imgStarEff.length; i++) {
            imgStarEff[i] = new Texture("game/stareff" + i + ".png");
        }
        for (int i = 0; i < imgEffText.length; i++) {
            imgEffText[i] = new Texture("game/efftext_0" + (i + 1) + ".png");
        }
        imgRipple = new Texture("game/ripple.png");
        if (useBackgroundPic) {
            try {
                imgBackground = new Texture("map/" + mapDirName + "/bg.png");
            } catch (Exception e) {
                imgBackground = null;
                useBackgroundPic = false;
            }
        }
        shape = new ShapeRenderer();
        bmpFont = new BitmapFont(Gdx.files.internal("Font/Consolas.fnt"));
        bmpFontBig = new BitmapFont(Gdx.files.internal("Font/Big100.fnt"));

        externalDirPath = Gdx.files.getExternalStoragePath() + "MaiDroid/";

        seHit = Gdx.audio.newSound(Gdx.files.internal("se/hit.wav"));
        seStar = Gdx.audio.newSound(Gdx.files.internal("se/star.wav"));
        //TODO file chooser
        bgm = Gdx.audio.newMusic(Gdx.files.internal("map/" + mapDirName + "/track.mp3"));
    }

    private static void initBasicData() {
        for (int i = 0; i < BOARD_SIDE; i++) {
            BOARD_ANGLE[i] = 360f / 8 * i + 360f / 16;
            BOARD_POSITION[i] = new PointF(
                    BOARD_RADIUS * (float) Math.sin(BOARD_ANGLE[i] / 180f * Math.PI),
                    BOARD_RADIUS * (float) Math.cos(BOARD_ANGLE[i] / 180f * Math.PI));
            BOARD_POSITION2_MID[i] = new PointF(
                    BOARD_RADIUS2 * (float) Math.sin(Math.PI / 4 * i),
                    BOARD_RADIUS2 * (float) Math.cos(Math.PI / 4 * i));
            BOARD_POSITION2[i] = new PointF(
                    BOARD_RADIUS2 * (float) Math.sin(BOARD_ANGLE[i] / 180f * Math.PI),
                    BOARD_RADIUS2 * (float) Math.cos(BOARD_ANGLE[i] / 180f * Math.PI));
        }
    }

    private static void updateScreenRotation(boolean forceUpdate) {
        if (scrWidth == Gdx.graphics.getWidth() &&
                scrHeight == Gdx.graphics.getHeight() &&
                !forceUpdate)
            return;
        scrWidth = Gdx.graphics.getWidth();
        scrHeight = Gdx.graphics.getHeight();
        scrWidthHalf = scrWidth / 2;
        scrHeightHalf = scrHeight / 2;
        scrCenter = new PointF(scrWidthHalf, scrHeightHalf);
        scrMaxRadius = scrHeightHalf < scrWidthHalf ? scrHeightHalf : scrWidthHalf;

        int small = scrWidth < scrHeight ? scrWidth : scrHeight;
        combinedScale = STANDARD_RADIUS / small / viewportScale;

        camera = new OrthographicCamera(scrWidth * combinedScale, scrHeight * combinedScale);
//        camera.translate(scrWidthHalf, scrHeightHalf, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        InputManager.updateShapeRenderer();
    }

    public static void dispose() {
        InputManager.dispose();
        batch.dispose();
        for (Texture t : imgFrame)
            t.dispose();
        for (Texture t : imgNote)
            t.dispose();
        for (Texture t : imgHold)
            t.dispose();
        for (Texture t : imgStar)
            t.dispose();
        for (Texture t : imgPath)
            t.dispose();
        for (Texture t : imgArc)
            t.dispose();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                imgEdge[i][j].dispose();
            }
        }
        imgBreak.dispose();
        if (imgBackground != null) imgBackground.dispose();
        shape.dispose();
        bmpFont.dispose();
        bmpFontBig.dispose();
        bgm.dispose();
        seHit.dispose();
        seStar.dispose();
    }

    /**
     * call this func before drawing in each frame. do some cleaning.
     */
    public static void frameRefresh() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        updateScreenRotation(false);
        PointF lt = getScreenCorner(true, true);
        textDebugX = lt.x;
        textDebugY = lt.y;
        bgmPosition = bgm.getPosition();
    }

    public static PointF getScreenCorner(boolean isLeft, boolean isTop) {
        float x = 1f, y = -1f;
        if (isLeft)
            x = -1f;
        if (isTop)
            y = 1f;
        return new PointF(-scrWidthHalf * combinedScale, scrHeightHalf * combinedScale);
    }

    /**
     * call this func before drawing in each frame. get input.
     */
    public static void procInput() {
        System.arraycopy(touched, 0, touchedPrev, 0, POINTERS_MAX);
        System.arraycopy(inputSide, 0, inputSidePrev, 0, POINTERS_MAX);
        totalInput = 0;
        for (int i = 0; i < POINTERS_MAX; i++) {
            touched[i] = Gdx.input.isTouched(i);
            if (touched[i]) {
                totalInput++;
                inputX[i] = Gdx.input.getX(i);
                inputY[i] = scrHeight - Gdx.input.getY(i);
                double ang = Math.atan2(inputX[i] - scrWidthHalf, inputY[i] - scrHeightHalf);
                if (ang < 0d)
                    ang += Math.PI * 2;
                inputSide[i] = (byte) ((int) (ang / (Math.PI * 2 / BOARD_SIDE)) % BOARD_SIDE);
                inputDist[i] = (float) scrCenter.getDistance(new PointF(inputX[i], inputY[i]));
            }
        }
    }

    public static double getBgmPosition() {
        return bgmPosition;
    }

    public static void setOpacity(float percentage) {
        if (percentage > 1f)
            percentage = 1f;
        if (percentage < 0f)
            percentage = 0f;
        Color c = batch.getColor();
        batch.setColor(c.r, c.g, c.b, percentage);
    }

    /**
     * 绘制文字，在batch.begin()内调用
     *
     * @param bmpFont BitmapFont字体
     * @param toDraw  绘制的内容.toString()
     * @param x       x
     * @param y       y
     */
    public static void drawText(BitmapFont bmpFont, Object toDraw, float x, float y) {
        bmpFont.draw(batch, toDraw.toString(), x, y);
    }

    public static void drawText(Object toDraw, float x, float y) {
        bmpFont.draw(batch, toDraw.toString(), x, y);
    }

    /**
     * 绘制文字，专门用于显示debug信息的文字，会默认出现在屏幕左上角。
     *
     * @param toDraw
     */
    public static void drawTextDebug(Object toDraw) {
        if (!showDebugInfo)
            return;
        bmpFont.draw(batch, toDraw.toString(), textDebugX, textDebugY);
        textDebugY -= bmpFont.getLineHeight();
    }

    /**
     * 绘制图像，左下角为起点
     *
     * @param img Texture类对象
     * @param x   x
     * @param y   y
     */
    public static void drawImageLB(Texture img, float x, float y) {
        batch.draw(img, x, y);
    }

    /**
     * 绘制图像，中心为起点
     *
     * @param img
     * @param x
     * @param y
     */
    public static void drawImageC(Texture img, float x, float y) {
        batch.draw(img, x - img.getWidth() / 2, y - img.getHeight() / 2);
    }

    /**
     * 绘制图像，图像中心为起点，可旋转缩放
     *
     * @param img
     * @param x
     * @param y
     * @param scaleX
     * @param scaleY
     * @param rotation
     */
    public static void drawImageC(Texture img, float x, float y, float scaleX, float scaleY, float rotation) {
        int w, h;
        float w2, h2;
        w = img.getWidth();
        h = img.getHeight();
        w2 = w / 2;
        h2 = h / 2;
        batch.draw(img, //imgFrame
                x - w2, y - h2, //dest pos
                w2, h2, //origin pos
                w, h, //dest size
                scaleX, scaleY, //scale
                rotation, //rotation
                0, 0, //scr pos
                w, h, //scr size
                false, false);//flip
    }

    public static void drawImageCC(Texture img, PointF pos) {
        drawImageC(img, pos.x, pos.y, 1f, 1f, 0f);
    }

    public static void drawImageCC(Texture img, PointF pos, float scale, float rotation) {
        drawImageC(img, pos.x, pos.y, scale, scale, rotation);
    }

    public static int getInputX(int pointer) {
        return inputX[pointer];
    }

    public static int getInputX() {
        return inputX[0];
    }

    public static int getInputY(int pointer) {
        return inputY[pointer];
    }

    public static int getInputY() {
        return inputY[0];
    }

    public static int getInputPointers() {
        return totalInput;
    }

    public static boolean getInputTouched(int pointer) {
        return touched[pointer];
    }

    public static boolean getInputJustTouched(int pointer) {
        return touched[pointer] && !touchedPrev[pointer];
    }

    public static double getInputAngle(int pointer) {
        return Math.atan2(inputX[pointer] - scrWidthHalf, inputY[pointer] - scrHeightHalf);
    }

    public static float getInputDistance(int pointer) {
        return inputDist[pointer];
    }

    public static byte getInputSide(int pointer) {
        return inputSide[pointer];
    }

    public static boolean getInputSideChanged(int pointer) {
        return inputSide[pointer] != inputSidePrev[pointer];
    }

    public static void playSeHit() {
        if (isPlayHitSE)
            seHit.play();
    }

    public static void playSeStar() {
        if (isPlayStarSE)
            seStar.play();
    }
}
