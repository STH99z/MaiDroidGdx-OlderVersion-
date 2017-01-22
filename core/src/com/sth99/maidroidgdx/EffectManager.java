package com.sth99.maidroidgdx;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by STH99 on 2016/10/17.
 */

public final class EffectManager {
    private static List<Effect> effectList = new ArrayList<Effect>(16);
    static float frameEffOpacity = 0f;

    public static Effect addToDrawList(Effect effect) {
        effectList.add(effect);
        return effect;
    }

    static Effect addNoteHitEffect(NoteHitEffect.Type type, int side) {
        Effect e = new NoteHitEffect(type, Engine.BOARD_POSITION[side], Engine.BOARD_ANGLE[side]);
        effectList.add(e);
        return e;
    }

    public static void drawEffectList() {
        frameEffOpacity = 0f;
        for (int i = 0; i < effectList.size(); i++) {
            Effect e = effectList.get(i);
            e.display();
            if (e.isPlayOver())
                effectList.remove(i--);
        }
        drawFrameEff();
    }

    private static void drawFrameEff() {
        if (frameEffOpacity != 0f) {
            Engine.setOpacity(frameEffOpacity);
            Engine.drawImageC(Engine.imgFrameEff, 0, 0);
            Engine.setOpacity(1f);
        }
    }

    public static void clearEffectList() {
        effectList.clear();
    }
}
