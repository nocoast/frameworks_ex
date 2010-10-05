/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.carouseltest;

import com.android.carouseltest.MyCarouselView;
import com.android.ex.carousel.CarouselView;
import com.android.ex.carousel.CarouselViewHelper;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;

public class CarouselTestActivity extends Activity {
    private static final String TAG = "CarouselTestActivity";
    private static final int CARD_SLOTS = 56;
    private static final int TOTAL_CARDS = 1000;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int TEXTURE_WIDTH = 256;
    private static final int SLOTS_VISIBLE = 7;

    protected static final boolean DBG = false;
    private static final int DETAIL_TEXTURE_WIDTH = 200;
    private static final int DETAIL_TEXTURE_HEIGHT = 80;
    private static final int VISIBLE_DETAIL_COUNT = 3;
    private CarouselView mView;
    private Paint mPaint = new Paint();
    private CarouselViewHelper mHelper;
    private Bitmap mGlossyOverlay;
    private Bitmap mBorder;

    class LocalCarouselViewHelper extends CarouselViewHelper {
        private static final int PIXEL_BORDER = 3;
        private DetailTextureParameters mDetailTextureParameters
                = new DetailTextureParameters(5.0f, 5.0f, 3.0f, 10.0f);

        LocalCarouselViewHelper(Context context) {
            super(context);
        }

        @Override
        public void onCardSelected(int id) {
            Log.v(TAG, "Yay, card " + id + " was selected!");
        }

        @Override
        public DetailTextureParameters getDetailTextureParameters(int id) {
            return mDetailTextureParameters;
        }

        @Override
        public Bitmap getTexture(int n) {
            Bitmap bitmap = Bitmap.createBitmap(TEXTURE_WIDTH, TEXTURE_HEIGHT,
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawARGB(0, 0, 0, 0);
            mPaint.setColor(0x40808080);
            canvas.drawRect(2, 2, TEXTURE_WIDTH-2, TEXTURE_HEIGHT-2, mPaint);
            mPaint.setTextSize(100.0f);
            mPaint.setAntiAlias(true);
            mPaint.setColor(0xffffffff);
            canvas.drawText("" + n, 2, TEXTURE_HEIGHT-10, mPaint);
            canvas.drawBitmap(mGlossyOverlay, null,
                    new Rect(PIXEL_BORDER, PIXEL_BORDER,
                            TEXTURE_WIDTH - PIXEL_BORDER, TEXTURE_HEIGHT - PIXEL_BORDER), mPaint);
            return bitmap;
        }

        @Override
        public Bitmap getDetailTexture(int n) {
            Bitmap bitmap = Bitmap.createBitmap(DETAIL_TEXTURE_WIDTH, DETAIL_TEXTURE_HEIGHT,
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawARGB(32, 10, 10, 10);
            mPaint.setTextSize(15.0f);
            mPaint.setAntiAlias(true);
            canvas.drawText("Detail text for card " + n, 0, DETAIL_TEXTURE_HEIGHT/2, mPaint);
            return bitmap;
        }
    };

    @Override
    public CharSequence onCreateDescription() {
        return getText(R.string.carousel_test_activity_description);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mView = new MyCarouselView(this);
        mView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mPaint.setColor(0xffffffff);
        final Resources res = getResources();

        mHelper = new LocalCarouselViewHelper(this);
        mHelper.setCarouselView(mView);
        mView.setSlotCount(CARD_SLOTS);
        mView.createCards(TOTAL_CARDS);
        mView.setVisibleSlots(SLOTS_VISIBLE);
        mView.setStartAngle((float) -(2.0f*Math.PI * 5 / CARD_SLOTS));
        mBorder = BitmapFactory.decodeResource(res, R.drawable.border);
        mView.setDefaultBitmap(mBorder);
        mView.setLoadingBitmap(mBorder);
        mView.setBackgroundColor(0.25f, 0.25f, 0.5f, 0.5f);
        mView.setRezInCardCount(3.0f);
        mView.setFadeInDuration(250);
        mView.setVisibleDetails(VISIBLE_DETAIL_COUNT);

        mGlossyOverlay = BitmapFactory.decodeResource(res, R.drawable.glossy_overlay);

        /*
        mView.setBackgroundColor(0x80ffffff);
        int flags = WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                mView.getWidth(), mView.getHeight(),
                WindowManager.LayoutParams.TYPE_APPLICATION,
                flags, PixelFormat.TRANSLUCENT);
        getWindow().setAttributes(lp);
        */

        setContentView(mView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHelper.onPause();
    }

}
