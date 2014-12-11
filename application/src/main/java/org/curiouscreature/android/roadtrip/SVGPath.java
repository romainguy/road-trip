package org.curiouscreature.android.roadtrip;

import android.graphics.Path;
import android.graphics.PathMeasure;

import java.util.List;

public final class SVGPath {
    public final List<Path> pathList;
    public final List<PathMeasure> measureList;
    public final List<Float> lengthList;

    public SVGPath(final List<Path> pathList, final List<PathMeasure> measureList, final List<Float> lengthList) {
        this.pathList = pathList;
        this.measureList = measureList;
        this.lengthList = lengthList;
    }
}
