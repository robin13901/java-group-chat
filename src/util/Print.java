package util;

import java.awt.*;

public class Print {

    private static Print instance = null;

    private Print() {}

    public static Print getInstance() {
        if (instance == null) instance = new Print();
        return instance;
    }

    private String startAnsiCode(String ansiCode) {
        return "\u001B[" + ansiCode;
    }

    private String endAnsiCode() {
        return "\u001B[0m";
    }

    public void newLine() {
        System.out.println(System.lineSeparator());
    }

    public void out(String text) {
        System.out.println(text);
    }

    public void outWithNewLine(String text) {
        System.out.println(text + System.lineSeparator());
    }

    public void outWithDelimiter(String text) {
        System.out.println(text);
        System.out.println(System.lineSeparator());
        System.out.println(bold("-".repeat(170)));
        System.out.println(System.lineSeparator());
    }

    public String percentage(double fraction) {
        double percentage = Math.round(fraction * 1000) / 10.;
        return (percentage + " %").replace('.', ',');
    }

    public String color(String text, Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        String colorCode = "38;2;" + r + ";" + g + ";" + b + "m";
        return startAnsiCode(colorCode) + text + endAnsiCode();
    }

    public String bold(String text) {
        return startAnsiCode("1m") + text + endAnsiCode();
    }

    public String italic(String text) {
        return startAnsiCode("3m") + text + endAnsiCode();
    }

    public String underlined(String text) {
        return startAnsiCode("4m") + text + endAnsiCode();
    }
}