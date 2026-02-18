package com.example.smartwastemanagement;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class PieChartView extends View {
    private List<PieSlice> pieSlices = new ArrayList<>();
    private float totalValue = 0;

    public PieChartView(Context context) {
        super(context);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);  // Avoid hardware acceleration issues on custom views
    }

    // Method to add a pie slice (value and color)
    public void addSlice(float value, int color) {
        if (value > 0) {
            pieSlices.add(new PieSlice(value, color));
            totalValue += value;
        }
        invalidate();  // Redraw the chart when new data is added
    }

    // Method to clear all pie slices
    public void clearSlices() {
        pieSlices.clear();
        totalValue = 0;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Calculate the center and radius of the pie chart
        float width = getWidth();
        float height = getHeight();
        float radius = Math.min(width, height) / 2;
        float centerX = width / 2;
        float centerY = height / 2;

        // Set up paint for drawing slices and labels
        Paint slicePaint = new Paint();
        slicePaint.setAntiAlias(true);
        slicePaint.setStyle(Paint.Style.FILL);

        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30);

        // Start drawing the slices
        float currentAngle = -90f;  // Start from the top (12 o'clock position)
        for (PieSlice slice : pieSlices) {
            slicePaint.setColor(slice.getColor());

            // Calculate the sweep angle for this slice
            float sweepAngle = (slice.getValue() / totalValue) * 360f;
            RectF rect = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
            canvas.drawArc(rect, currentAngle, sweepAngle, true, slicePaint);

            // Draw the label at the center of the slice
            float labelAngle = currentAngle + sweepAngle / 2;
            float labelX = (float) (centerX + (radius / 1.5) * Math.cos(Math.toRadians(labelAngle)));
            float labelY = (float) (centerY + (radius / 1.5) * Math.sin(Math.toRadians(labelAngle)));
            String label = String.format("%.1f%%", (slice.getValue() / totalValue) * 100);
            canvas.drawText(label, labelX, labelY, textPaint);

            // Update the angle for the next slice
            currentAngle += sweepAngle;
        }
    }

    // Data class for a single pie slice (value and color)
    public static class PieSlice {
        private float value;
        private int color;

        public PieSlice(float value, int color) {
            this.value = value;
            this.color = color;
        }

        public float getValue() {
            return value;
        }

        public int getColor() {
            return color;
        }
    }
}