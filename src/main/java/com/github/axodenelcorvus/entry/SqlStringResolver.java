package com.github.axodenelcorvus.entry;

import java.util.function.Function;

public class SqlStringResolver implements Function<String, String> {

    public String apply(String field) {
        if (field == null) {
            return "null";
        }

        if (field.contains("'")) {
            field = resolveSingleQuotes(field);
        }

        return "'%s'".formatted(field);
    }


    private String resolveSingleQuotes(String field) {
        String[] fieldSubstrs = field.split("'");
        return String.join("''", fieldSubstrs);
    }

}
