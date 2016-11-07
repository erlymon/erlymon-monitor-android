/*
 * Copyright (c) 2016, Sergey Penkovsky <sergey.penkovsky@gmail.com>
 *
 * This file is part of Erlymon Monitor.
 *
 * Erlymon Monitor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Erlymon Monitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Erlymon Monitor.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.erlymon.monitor.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.erlymon.monitor.R;

/**
 * Created by sergey on 6/25/16.
 */
public class Utils {
    public static Bitmap createDrawableText(Context context, String label, int color) {

        TextView text = new TextView(context);
        text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        text.setPadding(5, 5, 5, 5);
        text.setBackgroundResource(R.drawable.bg_label);
        text.setText(label);

        text.setTextColor(color);
/*
        if (time == 0) {
            text.setTextColor(context.getResources().getColor(R.color.accent));
        } else {
            long diff = System.currentTimeMillis() - time;
            if (diff <= 15 * 60 * 1000) {
                text.setTextColor(context.getResources().getColor(R.color.green));
            } else if (diff > 15 * 60 * 1000 && diff <= 60 * 60 * 1000) {
                text.setTextColor(context.getResources().getColor(R.color.yelow));
            } else if (diff > 60 * 60 * 1000) {
                text.setTextColor(context.getResources().getColor(R.color.red));
            }
        }
*/
        FrameLayout layout = new FrameLayout(context);
        layout.setPadding(5,5,5,5);
        layout.addView(text);

        View view = layout;

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        view.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }
}