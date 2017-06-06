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
package org.erlymon.monitor.map;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

/**
 * Created by sergey on 6/25/16.
 */
public class MarkerWithLabel extends Marker {
    Paint textPaint = null;
    Paint stkPaint = null;
    protected float mLabelAnchorU, mLabelAnchorV;

    public MarkerWithLabel(MapView mapView) {
        super( mapView);
        textPaint = new Paint();
        textPaint.setColor( Color.BLACK);
        textPaint.setTextSize(40f);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.LEFT);

        stkPaint = new Paint();
        stkPaint.setStyle(Paint.Style.STROKE);
        stkPaint.setTextSize(40f);
        stkPaint.setStrokeWidth(8);
        stkPaint.setColor(Color.WHITE);

        mLabelAnchorU = ANCHOR_CENTER;
        mLabelAnchorV = ANCHOR_BOTTOM;
    }

    public void setLabelAnchor(float anchorU, float anchorV){
        mLabelAnchorU = anchorU;
        mLabelAnchorV = anchorV;
    }

    @Override
    public void draw( final Canvas c, final MapView osmv, boolean shadow) {
        super.draw( c, osmv, shadow);

        if (shadow)
            return;

        Rect rectStk = new Rect();
        stkPaint.getTextBounds(getTitle(), 0, getTitle().length(), rectStk);
        c.drawText(getTitle(), mPositionPixels.x - (mLabelAnchorU * rectStk.width()), mPositionPixels.y + (mLabelAnchorV * rectStk.height()), stkPaint);

        Rect rectText = new Rect();
        textPaint.getTextBounds(getTitle(), 0, getTitle().length(), rectText);
        c.drawText(getTitle(), mPositionPixels.x - (mLabelAnchorU * rectText.width()), mPositionPixels.y + (mLabelAnchorV * rectText.height()), textPaint);
    }
}
