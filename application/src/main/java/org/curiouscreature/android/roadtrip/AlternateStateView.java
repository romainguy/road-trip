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

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public final class AlternateStateView extends View {
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private SVGPath svgPath;
    private int mSvgResource;

    private float mPhase;
    private float mFadeFactor;
    private int mDuration;
    private float mParallax = 1.0f;
    private float mOffsetY;

    private ObjectAnimator mSvgAnimator;

    public AlternateStateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlternateStateView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mPaint.setStyle(Paint.Style.STROKE);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IntroView, defStyle, 0);
        try {
            if (a != null) {
                mPaint.setStrokeWidth(a.getFloat(R.styleable.StateView_strokeWidth, 1.0f));
                mPaint.setColor(a.getColor(R.styleable.StateView_strokeColor, 0xff000000));
                mPhase = a.getFloat(R.styleable.StateView_phase, 1.0f);
                mDuration = a.getInt(R.styleable.StateView_duration, 4000);
                mFadeFactor = a.getFloat(R.styleable.StateView_fadeFactor, 10.0f);
            }
        } finally {
            if (a != null) a.recycle();
        }
    }

    private void updatePathsPhaseLocked() {
        final int count = svgPath.pathList.size();
        for (int i = 0; i < count; i++) {
            // SvgHelper.SvgPath svgPath = mPaths.get(i);
            final Path path = svgPath.pathList.get(i);
            path.reset();
            svgPath.measureList.get(i).getSegment(0.0f, svgPath.lengthList.get(i) * mPhase, path, true);
            path.rLineTo(0.0f, 0.0f);
            // svgPath.measure.getSegment(0.0f, svgPath.length * mPhase, svgPath.renderPath, true);
            // Required only for Android 4.4 and earlier
            // svgPath.renderPath.rLineTo(0.0f, 0.0f);
        }
    }

    public float getParallax() {
        return mParallax;
    }

    public void setParallax(float parallax) {
        mParallax = parallax;
        invalidate();
    }

    public float getPhase() {
        return mPhase;
    }

    public void setPhase(float phase) {
        mPhase = phase;
        updatePathsPhaseLocked();
        invalidate();
    }

    public int getSvgResource() {
        return mSvgResource;
    }

    public void setSvgResource(int svgResource) {
        mSvgResource = svgResource;
    }

    @Override
    protected void onSizeChanged(final int w, final int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        svgPath = PathParserHelper.loadPathList(getContext(), mSvgResource,
                w - getPaddingLeft() - getPaddingRight(), h - getPaddingTop() - getPaddingBottom());
        updatePathsPhaseLocked();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (svgPath == null) {
            return;
        }

        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop() + mOffsetY);
        final int count = svgPath.pathList.size();
        for (int i = 0; i < count; i++) {
            final Path path = svgPath.pathList.get(i);
            int alpha = (int) (Math.min(mPhase * mFadeFactor, 1.0f) * 255.0f);
            mPaint.setAlpha(alpha);
            canvas.drawPath(path, mPaint);
        }
        canvas.restore();
    }

    public void reveal(View scroller, int parentBottom) {
        if (mSvgAnimator == null) {
            mSvgAnimator = ObjectAnimator.ofFloat(this, "phase", 0.0f, 1.0f);
            mSvgAnimator.setDuration(mDuration);
            mSvgAnimator.start();
        }

        float previousOffset = mOffsetY;
        mOffsetY = Math.min(0, scroller.getHeight() - (parentBottom - scroller.getScrollY()));
        if (previousOffset != mOffsetY) invalidate();
    }
}
