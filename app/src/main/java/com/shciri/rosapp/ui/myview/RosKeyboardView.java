package com.shciri.rosapp.ui.myview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.KeyboardView;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.shciri.rosapp.R;

/**
 * TODO: document your custom view class.
 */
public class RosKeyboardView extends KeyboardView implements KeyboardView.OnKeyboardActionListener {
    public RosKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {

    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}