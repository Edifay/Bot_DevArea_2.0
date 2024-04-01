package devarea.fr.discord.workers.self.judge.core.io;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Boilerplate {
    private static final String JAVA_TEMPLATE = """
            %IMPORTS%
            public class Main {
                public static void main(String[] args) {
                    %CODE%
                }
            }
            """;
    private static final String CPP_TEMPLATE = """
            %IMPORTS%
            int main() {
                %CODE%
            }
            """;
    private static final String CSHARP_TEMPLATE = """
            %IMPORTS%
            class EntryPoint {
                static void Main(string[] args)
                {
                    %CODE%
                }
            }
            """;
    private static final String PHP_TEMPLATE = """
            <?php
            %CODE%
            """;
    private static final String GO_TEMPLATE = """
            package main
            %IMPORTS%
            func main() {
                %CODE%
            }
            """;
    private static final String RUST_TEMPLATE = """
            %IMPORTS%
            fn main() {
                %CODE%
            }
            """;

    private static final Pattern JAVA_ISVALID = Pattern.compile("\\bclass\\b");
    private static final Pattern CPP_ISVALID = Pattern.compile("\\bmain\\b");
    private static final Pattern CSHARP_ISVALID = Pattern.compile("\\bclass\\b");
    private static final Pattern PHP_ISVALID = Pattern.compile("<\\?php\\b");
    private static final Pattern GO_ISVALID = Pattern.compile("\\bmain\\b");
    private static final Pattern RUST_ISVALID = Pattern.compile("\\bmain\\b");

    private static final Pattern JAVA_ISIMPORT = Pattern.compile("^import\\b", Pattern.MULTILINE);
    private static final Pattern CPP_ISIMPORT = Pattern.compile("^#include\\b", Pattern.MULTILINE);
    private static final Pattern CSHARP_ISIMPORT = Pattern.compile("^using\\b", Pattern.MULTILINE);
    private static final Pattern PHP_ISIMPORT = null;
    private static final Pattern GO_ISIMPORT = Pattern.compile("^import\\b", Pattern.MULTILINE);
    private static final Pattern RUST_ISIMPORT = Pattern.compile("^use\\b", Pattern.MULTILINE);

    private static final Pattern REPLACE_IMPORTS = Pattern.compile("^(\\s*)%IMPORTS%", Pattern.MULTILINE);
    private static final Pattern REPLACE_CODE = Pattern.compile("^(\\s*)%CODE%", Pattern.MULTILINE);

    public static String addBoilerplate(String language, String code) {
        return switch (language) {
            case "Java" -> injectCode(JAVA_TEMPLATE, JAVA_ISVALID, JAVA_ISIMPORT, code);
            case "C++", "C" -> injectCode(CPP_TEMPLATE, CPP_ISVALID, CPP_ISIMPORT, code);
            case "C#" -> injectCode(CSHARP_TEMPLATE, CSHARP_ISVALID, CSHARP_ISIMPORT, code);
            case "PHP" -> injectCode(PHP_TEMPLATE, PHP_ISVALID, PHP_ISIMPORT, code);
            case "Go" -> injectCode(GO_TEMPLATE, GO_ISVALID, GO_ISIMPORT, code);
            case "Rust" -> injectCode(RUST_TEMPLATE, RUST_ISVALID, RUST_ISIMPORT, code);
            default -> code;
        };
    }

    private static String injectCode(String template, Pattern isValid, Pattern isImport, String sourceCode) {
        if (isValid.matcher(sourceCode).find()) {
            return sourceCode;
        }

        List<String> imports = new ArrayList<>();
        List<String> code = new ArrayList<>();

        String[] lines = sourceCode.split("(?<=;[^\\n])|\\n");

        if (isImport != null) {
            for (String line : lines) {
                if (isImport.matcher(line.stripLeading()).find()) {
                    imports.add(line);
                } else {
                    code.add(line);
                }
            }
        } else {
            code.addAll(List.of(lines));
        }

        return REPLACE_CODE.matcher(
                        REPLACE_IMPORTS.matcher(template)
                                .replaceFirst("$1" + String.join("\n$1", imports)))
                .replaceFirst("$1" + String.join("\n$1", code));
    }
}
