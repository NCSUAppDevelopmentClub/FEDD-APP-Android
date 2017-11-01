package com.ncsuappdev.feddapp;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Thomas on 10/25/2017.
 */

public class SquareImageView extends ImageView {
    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(getParent() != null) {
            int w = ((View)getParent()).getWidth();
            Log.e("Stuff", "Measured width: " + getMeasuredWidth() + ", " + widthMeasureSpec + ", " + w);
            setMeasuredDimension(w/2,w/2);//getMeasuredWidth(), getMeasuredWidth()); //Snap to width
        }
    }
}