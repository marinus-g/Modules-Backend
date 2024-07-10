package academy.mischok.modules.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RegisteredExamSheetRow {

    EMAIL(6),
    DATE(8),
    STATUS(10),
    POINTS(12),
    MAX_POINTS(13);

    private final int index;
}
