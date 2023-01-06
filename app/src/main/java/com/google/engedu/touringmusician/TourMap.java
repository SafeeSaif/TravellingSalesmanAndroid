/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.touringmusician;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TourMap extends View {

    private Bitmap mapImage;
    private CircularLinkedList beginList, closestList, smallestList;
    private String insertMode = "Add";
    private Paint pointPaint;

    public TourMap(Context context) {
        super(context);
        mapImage = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.map);
        pointPaint = new Paint();

        Paint beginPaint = new Paint();
        Paint closestPaint = new Paint();
        Paint smallestPaint = new Paint();

        pointPaint.setColor(Color.RED);

        beginPaint.setColor(Color.BLACK);
        closestPaint.setColor(Color.BLUE);
        smallestPaint.setColor(Color.MAGENTA);

        beginPaint.setStyle(Paint.Style.STROKE);
        closestPaint.setStyle(Paint.Style.STROKE);
        smallestPaint.setStyle(Paint.Style.STROKE);

        beginPaint.setStrokeWidth(3);
        closestPaint.setStrokeWidth(3);
        smallestPaint.setStrokeWidth(3);


        beginList = new CircularLinkedList(beginPaint);
        closestList = new CircularLinkedList(closestPaint);
        smallestList = new CircularLinkedList(smallestPaint);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mapImage, 0, 0, null);

        if (insertMode.equals("Closest")) {
            handleMapDrawing(canvas, closestList);
        } else if (insertMode.equals("Smallest")) {
            handleMapDrawing(canvas, smallestList);
        } else if (insertMode.equals("All")){
            handleMapDrawing(canvas, smallestList);
            handleMapDrawing(canvas, closestList);
            handleMapDrawing(canvas, beginList);
        }
        else {
            handleMapDrawing(canvas, beginList);
        }
    }

    public void handleMapDrawing(Canvas canvas, CircularLinkedList list){

        Point prevPoint = null;
        Point initPoint = null;
        for (Point p : list) {
            canvas.drawCircle(p.x, p.y, 20, pointPaint);

            if (prevPoint == null) {
                prevPoint = p;
                initPoint = p;
                continue;
            }
            drawArrow(canvas, prevPoint, p, list.getPaint());
            prevPoint = p;
        }
        if (prevPoint != null)
            drawArrow(canvas, prevPoint, initPoint, list.getPaint());
    }

    public void drawArrow(Canvas canvas, Point from, Point to, Paint paint){
        float stopX = to.x;
        float stopY = to.y;
        float startX = from.x;
        float startY = from.y;

        canvas.drawLine(startX, startY, stopX, stopY, paint);

        float dx = (stopX - startX);
        float dy = (stopY - startY);
        float rad = (float) Math.atan2(dy, dx);
        canvas.drawLine(stopX, stopY,//from   w w  w .ja v  a2 s.c om
                (float) (stopX + Math.cos(rad + Math.PI * 0.75) * 20),
                (float) (stopY + Math.sin(rad + Math.PI * 0.75) * 20),
                paint);
        canvas.drawLine(stopX, stopY,
                (float) (stopX + Math.cos(rad - Math.PI * 0.75) * 20),
                (float) (stopY + Math.sin(rad - Math.PI * 0.75) * 20),
                paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Point p = new Point((int) event.getX(), (int)event.getY());
                if (insertMode.equals("Closest")) {
                    closestList.insertNearest(p);
                } else if (insertMode.equals("Smallest")) {
                    smallestList.insertSmallest(p);
                } else if (insertMode.equals("All")){
                    closestList.insertNearest(p);
                    smallestList.insertSmallest(p);
                    beginList.insertBeginning(p);
                }
                else {
                    beginList.insertBeginning(p);
                }
                TextView message = (TextView) ((Activity) getContext()).findViewById(R.id.game_status);
                if (message != null) {
                    Log.d("onTouchEvent", "message not NULL");

                    message.setText(String.format(
                            "Total Distances for:\ninsertBeginning (BLACK):  %.2f\ninsertClosest(BLUE): %.2f\ninsertSmallest(Magenta): %.2f",
                            beginList.totalDistance(), closestList.totalDistance(), smallestList.totalDistance()));
                }
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void reset() {
        beginList.reset();
        closestList.reset();
        smallestList.reset();
        invalidate();
    }

    public void setInsertMode(String mode) {
        insertMode = mode;
    }
}
