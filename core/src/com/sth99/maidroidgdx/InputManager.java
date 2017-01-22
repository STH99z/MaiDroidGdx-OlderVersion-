package com.sth99.maidroidgdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * manages input conditions for each input area
 * which simulates touch inputs of Maimai.
 * Created by STH99 on 2016/10/18.
 */

public final class InputManager {
    static int InputValidRadius = 150;
    static int InputValidRadiusOutter = 200;
    static ShapeRenderer sr;
    static InputArea[] area;
    /**
     * input area's edge point vertices
     * 3 circles, 8 points per circle, x and y for 2 elements
     */
    private static float[] inputAreaVertices = new float[3 * 8 * 2];

    public static void init() {
        updateShapeRenderer();
        area = new InputArea[]{
                new InputArea(), new InputArea(), new InputArea(), new InputArea(),
                new InputArea(), new InputArea(), new InputArea(), new InputArea(),
                new InputArea(), new InputArea(), new InputArea(), new InputArea(),
                new InputArea(), new InputArea(), new InputArea(), new InputArea(),
                new InputArea()
        };//area 0-16
        float[] radius = new float[]{
                Engine.BOARD_RADIUS + 200f,
                (Engine.BOARD_RADIUS + Engine.BOARD_RADIUS2) / 2,
                100f};
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 8; j++) {
                int indexX = i * 16 + j * 2;
                inputAreaVertices[indexX + 0] = (float) Math.sin(Math.PI / 4 * j) * radius[i] / Engine.combinedScale
                        + Engine.scrWidthHalf;//x pos
                inputAreaVertices[indexX + 1] = (float) Math.cos(Math.PI / 4 * j) * radius[i] / Engine.combinedScale
                        + Engine.scrHeightHalf;//y pos
            }
        }
    }

    static void updateShapeRenderer() {
        sr = new ShapeRenderer();
    }

    public static void dispose() {
        sr.dispose();
    }

    /**
     * called at each frame begin
     * refresh all input area's status
     * <p>
     * input areas are updated as a web
     */
    public static void updateAllArea() {
        boolean[] update = new boolean[17];
        for (int i = 0; i < Engine.POINTERS_MAX; i++) {
            if (Engine.getInputTouched(i)) {
                int index = Engine.getInputSide(i);//default is outter
                float dist = Engine.getInputDistance(i) * Engine.combinedScale;
                if (dist < (Engine.BOARD_RADIUS + Engine.BOARD_RADIUS2) / 2) {
                    if (dist < 100f)//center
                        index = 16;
                    else
                        index += 8;//inner
                } else if (dist > Engine.BOARD_RADIUS + 200f) {
                    index = -1;//invalid
                }
                if (index != -1)
                    update[index] = true;//mark touched status
            }
        }
        for (int i = 0; i < 17; i++) {
            if (update[i]) {
                if (!area[i].isTouched())
                    area[i].setInputStatus(InputStatus.justTouched);
                else
                    area[i].setInputStatus(InputStatus.hold);
            } else {
                if (area[i].isTouched())
                    area[i].setInputStatus(InputStatus.justRelease);
                else
                    area[i].setInputStatus(InputStatus.untouch);
            }
        }
    }

    /**
     * input areas are updated as circles
     */
    public static void updateAllArea2() {
        boolean[] update = new boolean[17];
        for (int i = 0; i < Engine.POINTERS_MAX; i++) {
            if (Engine.getInputTouched(i)) {
                PointF pTouch = new PointF(Engine.getInputX(i) - Engine.scrWidthHalf,
                        Engine.getInputY(i) - Engine.scrHeightHalf).mul(Engine.combinedScale);
                for (int j = 0; j < Engine.BOARD_POSITION.length; j++) {
                    if (pTouch.inRange(Engine.BOARD_POSITION[j], InputValidRadiusOutter))
                        update[j] = true;
                }
                for (int j = 0; j < Engine.BOARD_POSITION2.length; j++) {
                    if (pTouch.inRange(Engine.BOARD_POSITION2[j], InputValidRadius))
                        update[j + 8] = true;
                }
                if (pTouch.inRange(Engine.BOARD_CENTER, InputValidRadius))
                    update[16] = true;
            }
        }
        for (int i = 0; i < 17; i++) {
            if (update[i]) {
                if (!area[i].isTouched())
                    area[i].setInputStatus(InputStatus.justTouched);
                else
                    area[i].setInputStatus(InputStatus.hold);
            } else {
                if (area[i].isTouched())
                    area[i].setInputStatus(InputStatus.justRelease);
                else
                    area[i].setInputStatus(InputStatus.untouch);
            }
        }
    }

    private static Color vertColor;

    private static void setCorrespondingColor(InputStatus status) {
        switch (status) {
            case justTouched:
                vertColor = new Color(1f, 0f, 0f, 1f);
                break;
            case hold:
                vertColor = new Color(.5f, .5f, .5f, 1f);
                break;
            case justRelease:
                vertColor = new Color(0f, 0f, 1f, 1f);
                break;
        }
    }

    static void drawSingleInputArea(int index) {
        InputStatus st = area[index].getInputStatus();
        if (st == InputStatus.untouch)
            return;
        setCorrespondingColor(st);
        if (index == 16) {
            sr.setColor(vertColor);
            sr.circle(Engine.scrWidthHalf, Engine.scrHeightHalf, 100f);
            sr.setColor(1f, 1f, 1f, 1f);
            return;
        }
        int circle = index / 8;
        int side1 = index % 8;
        int side2 = (side1 + 1) % 8;
        sr.triangle(
                inputAreaVertices[circle * 16 + side1 * 2 + 0], inputAreaVertices[circle * 16 + side1 * 2 + 1],
                inputAreaVertices[circle * 16 + side2 * 2 + 0], inputAreaVertices[circle * 16 + side2 * 2 + 1],
                inputAreaVertices[circle * 16 + side2 * 2 + 16], inputAreaVertices[circle * 16 + side2 * 2 + 17],
                vertColor, vertColor, vertColor);
        sr.triangle(
                inputAreaVertices[circle * 16 + side2 * 2 + 16], inputAreaVertices[circle * 16 + side2 * 2 + 17],
                inputAreaVertices[circle * 16 + side1 * 2 + 16], inputAreaVertices[circle * 16 + side1 * 2 + 17],
                inputAreaVertices[circle * 16 + side1 * 2 + 0], inputAreaVertices[circle * 16 + side1 * 2 + 1],
                vertColor, vertColor, vertColor);
    }

    static void drawSingleInputArea2(int index) {
        InputStatus st = area[index].getInputStatus();
        if (st == InputStatus.untouch)
            return;
        setCorrespondingColor(st);
        sr.setColor(vertColor);
        if (index == 16) {
            sr.circle(Engine.scrWidthHalf, Engine.scrHeightHalf, InputValidRadius / Engine.combinedScale);
        } else if (index < 8) {
            sr.circle(Engine.scrWidthHalf + Engine.BOARD_POSITION[index].x / Engine.combinedScale,
                    Engine.scrHeightHalf + Engine.BOARD_POSITION[index].y / Engine.combinedScale,
                    InputValidRadiusOutter / Engine.combinedScale);
        } else {
            sr.circle(Engine.scrWidthHalf + Engine.BOARD_POSITION2[index - 8].x / Engine.combinedScale,
                    Engine.scrHeightHalf + Engine.BOARD_POSITION2[index - 8].y / Engine.combinedScale,
                    InputValidRadius / Engine.combinedScale);
        }
        sr.setColor(1f, 1f, 1f, 1f);
    }

    /**
     * a debug method for showing input area status
     */
    public static void drawInputAreaStatus() {
        if (!Engine.showDebugInfo)
            return;
        sr.setColor(1f, 1f, 1f, 1f);
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.circle(Engine.scrWidthHalf, Engine.scrHeightHalf, (Engine.BOARD_RADIUS + 200f) / Engine.combinedScale);
        sr.circle(Engine.scrWidthHalf, Engine.scrHeightHalf, (Engine.BOARD_RADIUS + Engine.BOARD_RADIUS2) / 2 / Engine.combinedScale);
        sr.circle(Engine.scrWidthHalf, Engine.scrHeightHalf, 100f / Engine.combinedScale);
        float l = Engine.BOARD_RADIUS + 100f;
        for (int i = 0; i < 8; i++) {
            sr.line(inputAreaVertices[i * 2 + 0], inputAreaVertices[i * 2 + 1],
                    inputAreaVertices[i * 2 + 32], inputAreaVertices[i * 2 + 33]);
        }
        sr.end();
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < 17; i++) {
            drawSingleInputArea(i);
        }
        sr.end();
    }

    public static void drawInputAreaStatus2() {
        if (!Engine.showDebugInfo)
            return;
        sr.setColor(1f, 1f, 1f, 1f);
        sr.begin(ShapeRenderer.ShapeType.Line);
        for (int i = 0; i < 8; i++) {
            sr.circle(Engine.scrWidthHalf + Engine.BOARD_POSITION[i].x / Engine.combinedScale,
                    Engine.scrHeightHalf + Engine.BOARD_POSITION[i].y / Engine.combinedScale,
                    InputValidRadiusOutter / Engine.combinedScale);
            sr.circle(Engine.scrWidthHalf + Engine.BOARD_POSITION2[i].x / Engine.combinedScale,
                    Engine.scrHeightHalf + Engine.BOARD_POSITION2[i].y / Engine.combinedScale,
                    InputValidRadius / Engine.combinedScale);
        }
        sr.circle(Engine.scrWidthHalf, Engine.scrHeightHalf, InputValidRadius / Engine.combinedScale);
        sr.end();
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < 17; i++) {
            drawSingleInputArea2(i);
        }
        sr.end();
    }

    public static InputArea getArea(int i) {
        return area[i];
    }

    public static int getAreaIndex(byte side, boolean isOutter) {
        return side + (isOutter ? 0 : 8);
    }
}
