/*******************************************************************************
 * Copyright 2017 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.cyphercove.gdx.covetools.android;

import android.content.Context;
import android.content.SharedPreferences;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidLiveWallpaper;
import com.badlogic.gdx.backends.android.AndroidWallpaperListener;
import com.badlogic.gdx.math.MathUtils;

/** Wrap a {@link LiveWallpaperListener} in a LiveWallpaperWrapper when passing it to
 * {@link AndroidLiveWallpaper#initialize(ApplicationListener, AndroidApplicationConfiguration) initialize()} to enable its
 * extra features.
 *
 * @author cypherdare
 */
public class LiveWallpaperWrapper implements ApplicationListener, AndroidWallpaperListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    /** A listener that provides convenient non-core project access to events on the GLThread */
    public interface UpdateListener {
        /**
         * Called once per render loop immediately before {@code render()} is called on the live wallpaper.
         * @param deltaTime The frame delta time reported by LibGDX.
         */
        void onUpdate(float deltaTime);

        /** Called on the GL thread immediately after the SharedPreferences have changed. */
        void onSettingsChanged();
    }

    private LiveWallpaperListener liveWallpaper;
    private SharedPreferences sharedPreferences;
    private UpdateListener updateListener;
    private volatile boolean needSettingsUpdate;

    private float xOffset = 0.5f;
    private float yOffset;
    private float xOffsetFake = 0.5f;
    private float xOffsetFakeTarget = 0.5f;
    private static final float DIP_TO_XOFFSET_FAKE_RATIO = 750f;
    private float swipeXStart;

    /**
     * Pixels per DIP unit.
     */
    private float density;

    //Homescreen looping params
    protected float xOffsetSmooth;
    private float offsetDelta;
    private float previousOffset = 0.5f;
    private float offsetAdder;
    private float offsetVelocity = 0;
    private float offsetMaxVelocity = 1.5f; //per second
    private float offsetMinVelocity = .4f; //per second
    private static final float DELTA_OFFSET_MIN_LOOPING = 0.9f;
    private boolean catchingUp;

    /**
     * Wraps a LiveWallpaperListener in an ApplicationAdapter that can be passed to
     * {@link AndroidLiveWallpaper#initialize(ApplicationListener, AndroidApplicationConfiguration)}.
     *
     * @param liveWallpaper The live wallpaper updateListener to wrap
     * @param context The Android application context.
     */
    public LiveWallpaperWrapper(LiveWallpaperListener liveWallpaper, Context context){
        this(liveWallpaper, context, null);
    }

    public LiveWallpaperWrapper(LiveWallpaperListener liveWallpaper, Context context, SharedPreferences sharedPreferences){
        this(liveWallpaper, context, sharedPreferences, null);
    }

    /**
     * Wraps a LiveWallpaperListener in an ApplicationAdapter that can be passed to
     * {@link AndroidLiveWallpaper#initialize(ApplicationListener, AndroidApplicationConfiguration)}.
     *
     * @param liveWallpaper The live wallpaper updateListener to wrap
     * @param context The Android application context.
     * @param sharedPreferences Optional SharedPreferences to watch for changes, which will be reported in
     * {@link LiveWallpaperListener#onSettingsChanged()}.
     * @param updateListener Optional listener to be called once per frame before {@code render()}.
     */
    public LiveWallpaperWrapper(LiveWallpaperListener liveWallpaper, Context context, SharedPreferences sharedPreferences,
                                UpdateListener updateListener) {
        this.liveWallpaper = liveWallpaper;
        density = context.getResources().getDisplayMetrics().density;
        this.sharedPreferences = sharedPreferences;
        if (sharedPreferences != null)
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        this.updateListener = updateListener;
    }

    public void setUpdateListener(UpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    /**
     * Adjust these parameters to control the motion when using {@code xOffsetSmoothLooping}.
     *
     * @param minVelocity Minimum xOffset pan speed in units per second. Default is 0.4.
     * @param maxVelocity Maximum xOffset pan speed in units per second. Default is 1.5.
     */
    public void setSmoothLoopingXOffsetParams(float minVelocity, float maxVelocity) {
        offsetMinVelocity = minVelocity;
        offsetMaxVelocity = maxVelocity;
    }

    @Override
    public void render() {

        if (needSettingsUpdate) {
            needSettingsUpdate = false;
            if (updateListener != null)
                updateListener.onSettingsChanged();
            liveWallpaper.onSettingsChanged();
        }

        updateSpecialXOffsets();
        handleTouch();
        if (updateListener != null)
            updateListener.onUpdate(Gdx.graphics.getDeltaTime());
        liveWallpaper.render();
        liveWallpaper.render(xOffset, yOffset, xOffsetSmooth, xOffsetFake);
    }

    private void updateSpecialXOffsets() {

        //Handle xOffsetFake
        offsetDelta = xOffsetFakeTarget - xOffsetFake;
        offsetVelocity = offsetMaxVelocity * MathUtils.sin(offsetDelta * MathUtils.PI);
        if (offsetDelta < 0) {//moving left
            offsetVelocity -= offsetMinVelocity;
        } else if (offsetDelta > 0) {//moving right
            offsetVelocity += offsetMinVelocity;
        }
        offsetAdder = offsetVelocity * Gdx.graphics.getDeltaTime();
        if (Math.abs(offsetAdder) >= Math.abs(offsetDelta)) {
            xOffsetFake = xOffsetFakeTarget;
        } else {
            xOffsetFake += offsetAdder;
        }

        //Handle xOffsetSmooth
        offsetDelta = xOffset - xOffsetSmooth;
        offsetVelocity = offsetMaxVelocity * MathUtils.sin(offsetDelta * MathUtils.PI);
        if (offsetDelta < 0) {//moving left
            offsetVelocity -= offsetMinVelocity;
        } else if (offsetDelta > 0) {//moving right
            offsetVelocity += offsetMinVelocity;
        }
        offsetAdder = offsetVelocity * Gdx.graphics.getDeltaTime();
        if (Math.abs(offsetAdder) >= Math.abs(offsetDelta)) {
            xOffsetSmooth = xOffset;
            catchingUp = false;
        } else {
            xOffsetSmooth += offsetAdder;
        }

        if (!catchingUp) {
            final float offsetChange = xOffset - previousOffset;
            if (offsetChange >= DELTA_OFFSET_MIN_LOOPING || -offsetChange >= DELTA_OFFSET_MIN_LOOPING)
                catchingUp = true;
        }

        previousOffset = xOffset;

    }

    @Override
    public void dispose() {
        liveWallpaper.dispose();
        if (sharedPreferences != null){
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        needSettingsUpdate = true;
    }

    @Override
    public void offsetChange(float xOffset, float yOffset, float xOffsetStep,
                             float yOffsetStep, int xPixelOffset, int yPixelOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    @Override
    public void previewStateChange(boolean isPreview) {
        liveWallpaper.onPreviewStateChange(isPreview);
    }

    @Override
    public void create() {
        liveWallpaper.create();
    }

    @Override
    public void resize(int width, int height) {
        liveWallpaper.resize(width, height);
    }

    @Override
    public void pause() {
        liveWallpaper.pause();
    }

    @Override
    public void resume() {
        liveWallpaper.resume();
    }

    public final void handleTouch() {
        Input input = Gdx.input;

        if (input.justTouched()) {
            swipeXStart = input.getX(0);
        }

        if (input.isTouched(0) && !input.isTouched(1)) { //only one finger down and swiping
            //Measure swipe distance since last frame and apply. Then reset for next frame.
            float swipeDelta = swipeXStart - input.getX();
            xOffsetFakeTarget += (swipeDelta / density) / DIP_TO_XOFFSET_FAKE_RATIO;
            if (xOffsetFakeTarget < 0)
                xOffsetFakeTarget = 0;
            else if (xOffsetFakeTarget > 1)
                xOffsetFakeTarget = 1;
            swipeXStart = input.getX();
        }

    }
}

