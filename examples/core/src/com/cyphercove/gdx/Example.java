package com.cyphercove.gdx;

import com.badlogic.gdx.Screen;

public abstract class Example implements Screen {

    protected ExampleRunner exampleRunner;

    void setExampleRunner (ExampleRunner exampleRunner) {
        this.exampleRunner = exampleRunner;
    }

    protected void exit (){
        exampleRunner.exitCurrentExample();
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

}
