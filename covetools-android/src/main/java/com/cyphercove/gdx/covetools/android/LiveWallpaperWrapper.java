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

/**
 * Wrap a {@link LiveWallpaperListener} in a LiveWallpaperWrapper when passing it to
 * {@link AndroidLiveWallpaper#initialize(ApplicationListener, AndroidApplicationConfiguration) initialize()} to enable its
 * extra features.
 *
 * @author cypherdare
 */
public class LiveWallpaperWrapper implements ApplicationListener, AndroidWallpaperListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * A listener that provides convenient non-core project access to events on the GLThread
     */
    public interface WallpaperEventListener {
        /**
         * Called immediately after {@code create()} is called on the live wallpaper.
         *
         * @param liveWallpaper The wrapped live wallpaper.
         */
        void onPostCreate(LiveWallpaperListener liveWallpaper);

        /**
         * Called once per render loop immediately before {@code render()} is called on the live wallpaper.
         *
         * @param liveWallpaper The wrapped live wallpaper.
         * @param deltaTime     The frame delta time reported by LibGDX.
         */
        void onRender(LiveWallpaperListener liveWallpaper, float deltaTime);

        /**
         * Called on the GL thread immediately after the SharedPreferences have changed.
         *
         * @param liveWallpaper The wrapped live wallpaper.
         */
        void onSettingsChanged(LiveWallpaperListener liveWallpaper);

        /**
         * Called when the user taps multiple times in the same spot within a small time window.
         *
         * @param tapCount The number of taps that occurred in the same location.
         * @return True if the tap count should be reset.
         */
        boolean onMultiTap(int tapCount);
    }

    private LiveWallpaperListener liveWallpaper;
    private SharedPreferences sharedPreferences;
    private WallpaperEventListener wallpaperEventListener;
    private volatile boolean needSettingsUpdate;

    // Screen offsets
    private float xOffset = 0.5f;
    private float yOffset;
    private float xOffsetFake = 0.5f;
    private float xOffsetFakeTarget = 0.5f;
    private static final float DIP_TO_XOFFSET_FAKE_RATIO = 750f;
    private float swipeXStart;

    // Screen tapping
    private int tapCount = 0;
    private float timeSinceLastTap = 0;
    private float multiTapMaxInterval = 0.35f;
    private float firstTapX;
    private float firstTapY;
    private float tapThresholdDIP = 80;

    /**
     * Pixels per DIP unit.
     */
    private float density;

    //Homescreen looping params
    protected float xOffsetSmooth, xOffsetLooping;
    private float offsetDelta;
    private float previousOffset = 0.5f;
    private float offsetVelocity = 0;
    private float offsetMaxVelocity = 1.5f; //per second
    private float offsetMinVelocity = .4f; //per second
    private static final float DELTA_OFFSET_MIN_LOOPING = 0.9f;
    private boolean catchingUp;

    /**
     * Wraps a LiveWallpaperListener in an ApplicationAdapter that can be passed to
     * {@link AndroidLiveWallpaper#initialize(ApplicationListener, AndroidApplicationConfiguration)}.
     *
     * @param liveWallpaper The live wallpaper wallpaperEventListener to wrap
     * @param context       The Android application context.
     */
    public LiveWallpaperWrapper(LiveWallpaperListener liveWallpaper, Context context) {
        this(liveWallpaper, context, null);
    }

    public LiveWallpaperWrapper(LiveWallpaperListener liveWallpaper, Context context, SharedPreferences sharedPreferences) {
        this(liveWallpaper, context, sharedPreferences, null);
    }

    /**
     * Wraps a LiveWallpaperListener in an ApplicationAdapter that can be passed to
     * {@link AndroidLiveWallpaper#initialize(ApplicationListener, AndroidApplicationConfiguration)}.
     *
     * @param liveWallpaper          The live wallpaper wallpaperEventListener to wrap
     * @param context                The Android application context.
     * @param sharedPreferences      Optional SharedPreferences to watch for changes, which will be reported in
     *                               {@link LiveWallpaperListener#onSettingsChanged()}.
     * @param wallpaperEventListener Optional listener to events related to the live wallpaper.
     */
    public LiveWallpaperWrapper(LiveWallpaperListener liveWallpaper, Context context, SharedPreferences sharedPreferences,
                                WallpaperEventListener wallpaperEventListener) {
        this.liveWallpaper = liveWallpaper;
        density = context.getResources().getDisplayMetrics().density;
        this.sharedPreferences = sharedPreferences;
        if (sharedPreferences != null)
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        this.wallpaperEventListener = wallpaperEventListener;
    }

    public void setWallpaperEventListener(WallpaperEventListener wallpaperEventListener) {
        this.wallpaperEventListener = wallpaperEventListener;
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
            if (wallpaperEventListener != null)
                wallpaperEventListener.onSettingsChanged(liveWallpaper);
            liveWallpaper.onSettingsChanged();
        }

        updateSpecialXOffsets();
        handleTouch();
        handleMultiTaps();
        if (wallpaperEventListener != null)
            wallpaperEventListener.onRender(liveWallpaper, Gdx.graphics.getDeltaTime());
        liveWallpaper.render();
        liveWallpaper.render(xOffset, yOffset, xOffsetLooping, xOffsetFake);
    }

    private void updateSpecialXOffsets() {

        //Handle xOffsetFake
        offsetDelta = xOffsetFakeTarget - xOffsetFake;
        offsetVelocity = offsetMaxVelocity * MathUtils.sin(offsetDelta * MathUtils.PI);
        if (offsetDelta < 0) { // moving left
            offsetVelocity -= offsetMinVelocity;
        } else if (offsetDelta > 0) { // moving right
            offsetVelocity += offsetMinVelocity;
        }
        float offsetAdder = offsetVelocity * Gdx.graphics.getDeltaTime();
        if (Math.abs(offsetAdder) >= Math.abs(offsetDelta)) {
            xOffsetFake = xOffsetFakeTarget;
        } else {
            xOffsetFake += offsetAdder;
        }

        //Handle xOffsetSmooth
        offsetDelta = xOffset - xOffsetSmooth;
        offsetVelocity = offsetMaxVelocity*MathUtils.sin(offsetDelta*MathUtils.PI);
        if (offsetDelta < 0){ // moving left
            offsetVelocity -= offsetMinVelocity;
        } else if (offsetDelta > 0){ // moving right
            offsetVelocity += offsetMinVelocity;
        }
        offsetAdder = offsetVelocity * Gdx.graphics.getDeltaTime();
        if (Math.abs(offsetAdder) >= Math.abs(offsetDelta)){
            xOffsetSmooth = xOffset;
            catchingUp = false;
        } else {
            xOffsetSmooth += offsetAdder;
        }

        //Handle xOffsetLooping. Uses xOffset if not looping/catching up. Otherwise, use same value as xOffsetSmooth.
        if (!catchingUp){
            final float offsetChange = xOffset - previousOffset;
            if (offsetChange >= DELTA_OFFSET_MIN_LOOPING || -offsetChange >= DELTA_OFFSET_MIN_LOOPING)
                catchingUp=true;
        }
        if (!catchingUp){ //Not catching up. Just pass through xOffset.
            xOffsetLooping = xOffset;
        } else if (offsetDelta == 0){  //Just caught up.
            xOffsetLooping = xOffset;
            catchingUp = false;
        } else {  //Still catching up, so use smoothed value.
            xOffsetLooping = xOffsetSmooth;
        }

        previousOffset = xOffset;

    }

    @Override
    public void dispose() {
        liveWallpaper.dispose();
        if (sharedPreferences != null) {
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
    public void iconDropped (int x, int y) {
        liveWallpaper.onIconDropped(x, y);
    }

    @Override
    public void create() {
        liveWallpaper.create();
        if (wallpaperEventListener != null) {
            wallpaperEventListener.onPostCreate(liveWallpaper);
        }
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

    private void handleTouch() {
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

    private void handleMultiTaps() {
        timeSinceLastTap += Gdx.graphics.getDeltaTime();
        Input input = Gdx.input;

        if (input.justTouched()) {
            if (tapCount == 0) {
                timeSinceLastTap = 0;
                firstTapX = input.getX();
                firstTapY = input.getY();
                tapCount = 1;
            } else if (input.isTouched(1) // Multi touches immediately cancel multi tap.
                    || timeSinceLastTap > multiTapMaxInterval) { // Cancel if past time interval.
                tapCount = 0;
                return;
            } else {
                float tapThreshold = tapThresholdDIP * density;
                float dx = firstTapX - input.getX();
                float dy = firstTapY - input.getY();
                if (dx * dx + dy * dy > tapThreshold * tapThreshold) { // outside threshold radius
                    tapCount = 0;
                    return;
                }
                timeSinceLastTap = 0;
                tapCount++;
                if (wallpaperEventListener != null){
                    if (wallpaperEventListener.onMultiTap(tapCount))
                        tapCount = 0;
                }
            }
        } else if (input.isTouched(0)){
            float tapThreshold = tapThresholdDIP * density;
            float dx = firstTapX - input.getX(0);
            float dy = firstTapY - input.getY(0);
            if (dx * dx + dy * dy > tapThreshold * tapThreshold) { // swiped to outside threshold radius
                tapCount = 0;
                return;
            }
        }
    }
}

