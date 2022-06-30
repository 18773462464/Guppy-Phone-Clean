package com.guppy.phoneclean.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class AutoSplitTextView extends AppCompatTextView {
    private String autoText;
    private float textWidth;
    private float textHeight;
    private Paint textPaint;

    public AutoSplitTextView(Context context) {
        this(context, null);
    }

    public AutoSplitTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoSplitTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //解决首次渲染，没有补全的bug。
    private int mWidth = -1;

    @Override
    protected void onDraw(Canvas canvas) {
        //onDraw可能会被多次调用，因此不是每次调用都需要重绘，这里做了个判断，text是否跟上一次一样，若一样，不再计算，否则重新计算赋值
        if (mWidth != getWidth() || !autoText.equals(getText().toString())) {
            autoText = autoSplitText(this);
            setText(autoText);
            mWidth = getWidth();
        }
        super.onDraw(canvas);

    }

    private String autoSplitText(AutoSplitTextView textView) {
        CharSequence rawCharSequence = textView.getText();
        String originText = rawCharSequence.toString();//获取原始文本
        textPaint = textView.getPaint();
        textWidth = textView.getWidth() - textView.getPaddingLeft() - textView.getPaddingRight();
        textHeight = textView.getHeight();
        String allTextLines = originText.replaceAll("\n", "");
        StringBuilder stringBuilder = new StringBuilder();
        if (textPaint.measureText(allTextLines) > textWidth) {
            //如果整行宽度超过控件所用宽度，则按字符测量，在超过可用宽度的最后一个字符添加换行符
            float lineWidth = 0;
            for (int i = 0; i < allTextLines.length(); i++) {
                char textChar = allTextLines.charAt(i);
                lineWidth += textPaint.measureText(String.valueOf(textChar));
                if (lineWidth <= textWidth) {
                    stringBuilder.append(textChar);
                } else {
                    stringBuilder.append("\n");
                    i--;
                    lineWidth = 0;
                }
            }
        } else {
            stringBuilder.append(allTextLines);
        }
        return stringBuilder.toString();
    }

}