package com.gongwen.marqueen;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.text.TextUtils;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.gongwen.marqueen.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GongWen on 17/9/15.
 */
public class StringMF extends SimpleMF<String> {
    protected int mw;
    protected float textSize = 15;

    public StringMF(Context context) {
        super(context);
    }

    private String content;

    public void setData(String content) {
        setData(content, mw, textSize);
    }

    public void setData(String content, int mw, float textSize) {
        this.content = content;
        if (mw > 0 && textSize > 0) {
            fitContent(content, mw, textSize);
        }
    }

    @Override
    protected void attachedToMarqueeView(final MarqueeView marqueeView) {
        super.attachedToMarqueeView(marqueeView);
        try {
            addOnGlobalLayoutListener(marqueeView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addOnGlobalLayoutListener(final MarqueeView marqueeView) {
        try {
            marqueeView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            marqueeView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            marqueeView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                        mw = marqueeView.getWidth();
                        if (marqueeView instanceof SimpleMarqueeView) {
                            SimpleMarqueeView simpleMarqueeView = (SimpleMarqueeView) marqueeView;
                            textSize = simpleMarqueeView.getTextSize();
                        }
                        fitContent(content, mw, textSize);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void fitContent(String message, int mW, float textSize) {
        try {
            this.mw = mW;
            this.textSize = textSize;
            if (TextUtils.isEmpty(message)) return;
            if (message.contains("\n")) {
                String replace = message.replace("\n", "@#&");
                String[] splits = replace.split("@#&");
                List<String> contents = new ArrayList<>();
                for (String split : splits) {
                    contents.addAll(splitMessage(split, mW, textSize));
                }
                setData(contents);
            } else {
                setData(splitMessage(message, mW, textSize));
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    protected List<String> splitMessage(String message, int mW, float textSize) {
        List<String> contents = new ArrayList<>();
        try {
            int messageLength = message.length();
            int width = Util.px2Dp(mContext, mW == 0 ? getScreenWidth() : mW);
            int limit = (int) (width / textSize);
            if (messageLength <= limit) {
                contents.add(message);
            } else {
                int size = messageLength / limit + (messageLength % limit != 0 ? 1 : 0);
                for (int i = 0; i < size; i++) {
                    int startIndex = i * limit;
                    int endIndex = ((i + 1) * limit >= messageLength ? messageLength : (i + 1) * limit);
                    contents.add(message.substring(startIndex, endIndex));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contents;
    }

    protected int getScreenWidth() {
        try {
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            if (wm == null) return -1;
            Point point = new Point();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                wm.getDefaultDisplay().getRealSize(point);
            } else {
                wm.getDefaultDisplay().getSize(point);
            }
            return point.x;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 100;
    }
}