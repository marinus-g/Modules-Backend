package academy.mischok.modules.model.project;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProjectPresentationCriteria {

    TIME_SCHEDULE("Zeitlicher Ablauf", 1.0, new String[]{
            "Nutzung / Einhaltung der Vorgabezeit (15 Min. +/-2)",
            "Sachliche / Zeitliche Gliederung"
    }),
    DOCUMENTS("Unterlagen", 1.0, new String[]{
            "Qualität / Gestaltung"
    }),
    PRESENTATION_TECHNIQUE("Präsentationstechnik", 1.0, new String[]{
            "Medieneinsatz",
            "Sprache / Qualität",
            "Blickkontakt / Körpersprache"
    }),
    TECHNICAL_QUALITY("Fachliche Qualität", 1.0, new String[]{
            "fachlich richtig",
            "begründet"
    }),
    PROJECT_KNOWLEDGE("Projektbezogenes Fachwissen", 1.0, new String[]{
            "Hintergrundwissen"
    });

    private final String name;
    private final double multiplier;
    private final String[] subCriteria;


}
