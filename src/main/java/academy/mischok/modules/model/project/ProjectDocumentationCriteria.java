package academy.mischok.modules.model.project;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProjectDocumentationCriteria {

    DOCUMENTATION_FORMAL_COMPLETENESS("Ist die Dokumentation formal vollständig?", 0.5, new String[] {
            "Seitenangaben",
            "Quellenangaben",
            "Inhaltsverzeichnis/Gliederung",
            "Zeitplan",
            "Anlagenverzeichnis (falls Anlagen vorhanden sind)"
    }),
    OPTICAL_IMPRESSION("Wie ist der optische Eindruck?", 1.0, new String[] {
            "Seitenlayout, Design, Gestaltung",
            "Übersichtlichkeit"
    }),
    LANGUAGE_STYLE("Entspricht die sprachliche Gestaltung einer fachlichen Doku?", 1.0, new String[] {
            "Ausdruck",
            "Satzbau",
            "Stil",
            "Rechtschreibung"
    }),
    PROJECT_WORKFLOW("Praxisbezogene und zielorientierte Arbeitsabläufe?", 2.0, new String[] {
            "Projektansatz",
            "Planung und Durchführung",
            "Abschluss/Projektergebnis"
    }),
    REQUIREMENTS_DESCRIPTION("Beschreibung der Vorgaben / Einflussfaktoren", 2.0, new String[] {
            "wirtschaftlichkeit",
            "technisch",
            "organisatorisch",
            "zeitlich"
    }),
    TECHNICAL_QUALITY("Fachliche Qualität", 4.0, new String[] {
            "fachlich richtig",
            "begründet",
            "fachliche Tiefe / Komplexität"
    });

    private final String name;
    private final double multiplier;
    private final String[] subCriteria;

}
