package academy.mischok.modules.model;

public enum ExamState {

    SUBMITTED,
    NOT_SUBMITTED,
    ;

    public static ExamState getFromGermanString(String str) {
        return switch (str) {
            case "Abgegeben" -> SUBMITTED;
            case "Nicht abgegeben." -> NOT_SUBMITTED;
            default -> throw new IllegalArgumentException("Unknown state: " + str);
        };
    }
}
