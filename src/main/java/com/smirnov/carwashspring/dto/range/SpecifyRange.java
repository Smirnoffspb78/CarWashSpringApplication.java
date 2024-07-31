package com.smirnov.carwashspring.dto.range;

import java.time.temporal.Temporal;
import java.util.Comparator;

public interface SpecifyRange /*extends Comparator<Temporal>*/ {
    Temporal getStart();

    Temporal getFinish();
}
