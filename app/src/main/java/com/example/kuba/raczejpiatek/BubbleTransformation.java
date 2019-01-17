package com.example.kuba.raczejpiatek;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;

public class BubbleTransformation implements com.squareup.picasso.Transformation {
    private static final int outerMargin = 40;
    private final int margin;
    private final int color;

    public BubbleTransformation(final int margin, final int color) {
        this.margin = margin;
        this.color = color;
    }

    @Override
    public Bitmap transform(final Bitmap source) {
        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paintBorder = new Paint();
        paintBorder.setColor(color);
        paintBorder.setStrokeWidth(margin);
        canvas.drawRoundRect(new RectF(outerMargin, outerMargin, source.getWidth() - outerMargin, source.getHeight() - outerMargin), 0, 0, paintBorder);

        Paint trianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        trianglePaint.setStrokeWidth(2);
        trianglePaint.setColor(color);
        trianglePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        trianglePaint.setAntiAlias(true);

        Path triangle = new Path();
        triangle.setFillType(Path.FillType.EVEN_ODD);
        triangle.moveTo(outerMargin, source.getHeight() / 2);
        triangle.lineTo(source.getWidth()/2,source.getHeight());
        triangle.lineTo(source.getWidth()-outerMargin,source.getHeight()/2);
        triangle.close();

        canvas.drawPath(triangle, trianglePaint);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect(new RectF(margin+outerMargin, margin+outerMargin, source.getWidth() - (margin + outerMargin), source.getHeight() - (margin + outerMargin)), 0, 0, paint);

        if (source != output) {
            source.recycle();
        }

        return output;
    }

    @Override
    public String key() {
        return "rounded";
    }
}