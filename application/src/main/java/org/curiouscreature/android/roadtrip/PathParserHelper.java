package org.curiouscreature.android.roadtrip;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PathMeasure;

import java.util.ArrayList;
import java.util.List;

public final class PathParserHelper {

    private PathParserHelper() {
    }

    public static SVGPath loadPathList(final Context context, final int resId, final int width, final int height) {
        final TypedArray typedArray = context.getResources().obtainTypedArray(resId);
        final int svgWidth = typedArray.getInt(0, 0);
        final int svgHeight = typedArray.getInt(1, 0);
        final CharSequence[] paths = typedArray.getTextArray(2);
        final List<Path> pathList = new ArrayList<>(paths.length);
        final List<PathMeasure> measureList = new ArrayList<>(paths.length);
        final List<Float> lengthList = new ArrayList<>(paths.length);
        for (CharSequence pathAsString : paths) {
            final Path path = PathParser.createPathFromPathData(pathAsString.toString());
            final float scale = Math.min(width / svgWidth, height / svgHeight);
            final Matrix matrix = new Matrix();
            matrix.preTranslate((width - svgWidth * scale) / 2.0f, (height - svgHeight * scale) / 2.0f);
            matrix.preScale(scale, scale);
            path.transform(matrix);
            pathList.add(path);
            final PathMeasure measure = new PathMeasure(path, false);
            measureList.add(measure);
            final float length = measure.getLength();
            lengthList.add(length);
        }
        typedArray.recycle();
        return new SVGPath(pathList, measureList, lengthList);
    }
}
