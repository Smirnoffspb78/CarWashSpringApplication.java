package com.smirnov.carwashspring.dto.range;

import java.time.temporal.Temporal;

public interface SpecifyRange {
    Temporal getStart();
    Temporal getFinish();
}
