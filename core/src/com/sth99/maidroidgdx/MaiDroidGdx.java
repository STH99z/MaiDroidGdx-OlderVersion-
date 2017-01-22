package com.sth99.maidroidgdx;

import com.badlogic.gdx.ApplicationAdapter;

public class MaiDroidGdx extends ApplicationAdapter {

    private Runnable exitFunction;

    public MaiDroidGdx(Runnable exitFunction) {
        this.exitFunction = exitFunction;
    }

    @Override
    public void create() {
        Engine.init();
    }

    @Override
    public void render() {
        BoardManager.frameProcessFunc();
        if (BoardManager.getShouldExit())
            exitFunction.run();
    }

    @Override
    public void dispose() {
        BoardManager.dispose();
        Engine.dispose();
    }

    @Override
    public void pause() {
        Engine.bgm.pause();
    }

    @Override
    public void resume() {
        Engine.bgm.play();
    }

}
