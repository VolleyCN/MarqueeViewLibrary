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
    public StringMF(Context context) {
        super(context);
    }

    private String content;

    public void setData(String content) {
        this.content = content;
        if (mMarqueeView != null && mMarqueeView.getWidth() > 0) {
            if (mMarqueeView instanceof SimpleMarqueeView) {
                SimpleMarqueeView simpleMarqueeView = (SimpleMarqueeView) mMarqueeView;
                fitContent(content, simpleMarqueeView);
            }
        }
    }

    @Override
    protected void attachedToMarqueeView(final MarqueeView marqueeView) {
        super.attachedToMarqueeView(marqueeView);
        if (TextUtils.isEmpty(content)) return;
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
                        if (marqueeView instanceof SimpleMarqueeView) {
                            SimpleMarqueeView simpleMarqueeView = (SimpleMarqueeView) marqueeView;
                            fitContent(content, simpleMarqueeView);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fitContent(String message, SimpleMarqueeView marqueeView) {
        try {
            float textSize = marqueeView.getTextSize();
            int mW = marqueeView.getWidth();
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

    private List<String> splitMessage(String message, int mW, float textSize) {
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

    public int getScreenWidth() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) return -1;
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.x;
    }
}