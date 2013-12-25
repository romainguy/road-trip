/**
 * Copyright 2013 Romain Guy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.curiouscreature.android.roadtrip;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class MainActivity extends Activity {
    private static final boolean ANIMATE_BACKGROUND = false;

    private static class State {
        int background;
        int map;
        int photos[];
        final List<Bitmap> bitmaps = new ArrayList<Bitmap>();

        State(int background, int map, int[] photos) {
            this.background = background;
            this.map = map;
            this.photos = photos;
        }
    }

    private final State[] mStates = {
            new State(R.color.az, R.raw.map_az, new int[] {
                    R.drawable.photo_01_antelope,
                    R.drawable.photo_09_horseshoe,
                    R.drawable.photo_10_sky
            }),
            new State(R.color.ut, R.raw.map_ut, new int[] {
                    R.drawable.photo_08_arches,
                    R.drawable.photo_03_bryce,
                    R.drawable.photo_04_powell,
            }),
            new State(R.color.ca, R.raw.map_ca, new int[] {
                    R.drawable.photo_07_san_francisco,
                    R.drawable.photo_02_tahoe,
                    R.drawable.photo_05_sierra,
                    R.drawable.photo_06_rockaway
            }),
    };

    private IntroView mIntroView;
    private Drawable mActionBarDrawable;
    private Drawable mWindowBackground;
    private int mAccentColor;
    private int mAccentColor2;

    private final Rect mTempRect = new Rect();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActionBarDrawable = getResources().getDrawable(R.drawable.ab_solid);
        mActionBarDrawable.setAlpha(0);
        getActionBar().setBackgroundDrawable(mActionBarDrawable);

        mAccentColor = getResources().getColor(R.color.accent);
        mAccentColor2 = getResources().getColor(R.color.accent2);

        mIntroView = (IntroView) findViewById(R.id.intro);
        mIntroView.setSvgResource(R.raw.map_usa);
        mIntroView.setOnReadyListener(new IntroView.OnReadyListener() {
            @Override
            public void onReady() {
                loadPhotos();
            }
        });

        ((TrackingScrollView) findViewById(R.id.scroller)).setOnScrollChangedListener(
                new TrackingScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(TrackingScrollView source, int l, int t, int ol, int ot) {
                handleScroll(source, t);
            }
        });
    }

    private void handleScroll(ViewGroup source, int top) {
        final float actionBarHeight = getActionBar().getHeight();
        final float firstItemHeight = findViewById(R.id.scroller).getHeight() - actionBarHeight;
        final float alpha = Math.min(firstItemHeight, Math.max(0, top)) / firstItemHeight;

        mIntroView.setTranslationY(-firstItemHeight / 3.0f * alpha);
        mActionBarDrawable.setAlpha((int) (255 * alpha));

        View decorView = getWindow().getDecorView();
        removeOverdraw(decorView, alpha);
        if (ANIMATE_BACKGROUND) {
            changeBackgroundColor(decorView, alpha);
        }

        ViewGroup container = (ViewGroup) source.findViewById(R.id.container);
        final int count = container.getChildCount();
        for (int i = 0; i < count; i++) {
            View item = container.getChildAt(i);
            View v = item.findViewById(R.id.state);
            if (v != null && v.getGlobalVisibleRect(mTempRect)) {
                ((StateView) v).reveal(source, item.getBottom());
            }
        }
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    private void changeBackgroundColor(View decorView, float alpha) {
        float srcR = ((mAccentColor >> 16) & 0xff) / 255.0f;
        float srcG = ((mAccentColor >>  8) & 0xff) / 255.0f;
        float srcB = ((mAccentColor >>  0) & 0xff) / 255.0f;

        float dstR = ((mAccentColor2 >> 16) & 0xff) / 255.0f;
        float dstG = ((mAccentColor2 >>  8) & 0xff) / 255.0f;
        float dstB = ((mAccentColor2 >>  0) & 0xff) / 255.0f;

        int r = (int) ((srcR + ((dstR - srcR) * alpha)) * 255.0f);
        int g = (int) ((srcG + ((dstG - srcG) * alpha)) * 255.0f);
        int b = (int) ((srcB + ((dstB - srcB) * alpha)) * 255.0f);

        ColorDrawable background = (ColorDrawable) decorView.getBackground();
        if (background != null) {
            background.setColor(0xff000000 | r << 16 | g << 8 | b);
        }
    }

    private void removeOverdraw(View decorView, float alpha) {
        if (alpha >= 1.0f) {
            // Note: setting a large negative translation Y to move the View
            // outside of the screen is an optimization. We could make the view
            // invisible/visible instead but this would destroy/create its backing
            // layer every time we toggle visibility. Since creating a layer can
            // be expensive, especially software layers, we would introduce stutter
            // when the view is made visible again.
            mIntroView.setTranslationY(-mIntroView.getHeight() * 2.0f);
        }
        if (alpha >= 1.0f && decorView.getBackground() != null) {
            mWindowBackground = decorView.getBackground();
            decorView.setBackground(null);
        } else if (alpha < 1.0f && decorView.getBackground() == null) {
            decorView.setBackground(mWindowBackground);
            mWindowBackground = null;
        }
    }

    private void loadPhotos() {
        final Resources resources = getResources();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (State s : mStates) {
                    for (int resId : s.photos) {
                        s.bitmaps.add(BitmapFactory.decodeResource(resources, resId));
                    }
                }

                mIntroView.post(new Runnable() {
                    @Override
                    public void run() {
                        finishLoadingPhotos();
                    }
                });
            }
        }, "Photos Loader").start();
    }

    private void finishLoadingPhotos() {
        mIntroView.stopWaitAnimation();

        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        LayoutInflater inflater = getLayoutInflater();

        Space spacer = new Space(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                findViewById(R.id.scroller).getHeight()));
        container.addView(spacer);

        for (State s : mStates) {
            addState(inflater, container, s);
        }
    }

    private void addState(LayoutInflater inflater, LinearLayout container, final State state) {
        final int margin = getResources().getDimensionPixelSize(R.dimen.activity_peek_margin);

        final View view = inflater.inflate(R.layout.item_state, container, false);
        final StateView stateView = (StateView) view.findViewById(R.id.state);
        stateView.setSvgResource(state.map);
        view.setBackgroundResource(state.background);

        LinearLayout subContainer = (LinearLayout) view.findViewById(R.id.sub_container);
        Space spacer = new Space(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(
                container.getWidth() - margin, LinearLayout.LayoutParams.MATCH_PARENT));
        subContainer.addView(spacer);

        ImageView first = null;
        for (Bitmap b : state.bitmaps) {
            ImageView image =
                    (ImageView) inflater.inflate(R.layout.item_photo, subContainer, false);
            if (first == null) first = image;
            image.setImageBitmap(b);
            subContainer.addView(image);
        }

        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0.0f);
        first.setTag(cm);
        first.setColorFilter(new ColorMatrixColorFilter(cm));

        final ImageView bw = first;

        TrackingHorizontalScrollView s =
                (TrackingHorizontalScrollView) view.findViewById(R.id.scroller);
        s.setOnScrollChangedListener(new TrackingHorizontalScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(TrackingHorizontalScrollView source,
                    int l, int t, int oldl, int oldt) {
                final float width = source.getWidth() - margin;
                final float alpha = Math.min(width, Math.max(0, l)) / width;

                stateView.setTranslationX(-width / 3.0f * alpha);
                stateView.setParallax(1.0f - alpha);

                removeStateOverdraw(view, state, alpha);

                if (alpha < 1.0f) {
                    ColorMatrix cm = (ColorMatrix) bw.getTag();
                    cm.setSaturation(alpha);
                    bw.setColorFilter(new ColorMatrixColorFilter(cm));
                } else {
                    bw.setColorFilter(null);
                }
            }
        });

        container.addView(view);
    }

    private void removeStateOverdraw(View stateView, State state, float alpha) {
        if (alpha >= 1.0f && stateView.getBackground() != null) {
            stateView.setBackground(null);
            stateView.findViewById(R.id.state).setVisibility(View.INVISIBLE);
        } else if (alpha < 1.0f && stateView.getBackground() == null) {
            stateView.setBackgroundResource(state.background);
            stateView.findViewById(R.id.state).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            Toast.makeText(this, R.string.text_about, Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
