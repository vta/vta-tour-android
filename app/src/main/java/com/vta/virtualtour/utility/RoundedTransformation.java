package com.vta.virtualtour.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import com.vta.virtualtour.R;

/**
 * Created by tushar
 * Created on 22/07/18.
 */

public class RoundedTransformation implements com.squareup.picasso.Transformation {
    private final int radius;
    private final int margin;  // dp
    private final Context context;

    // radius is corner radii in dp
    // margin is the board in dp
    public RoundedTransformation(final int radius, final int margin, Context context) {
        this.radius = radius;
        this.margin = margin;
        this.context = context;
    }

    @Override
    public Bitmap transform(final Bitmap source) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawRoundRect(new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin), radius, radius, paint);

        Paint paint1 = new Paint();
        paint1.setColor(this.context.getResources().getColor(R.color.colorPrimary));
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setAntiAlias(true);
        paint1.setStrokeWidth(3);
        canvas.drawRoundRect(new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin), radius, radius, paint);

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