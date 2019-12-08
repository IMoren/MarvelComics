package ru.stm.marvelcomics.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Const {
    public static final int LIM = 20;
    public static final int OFS = 0;
    public static final String LIMIT = "20";
    public static final String OFFSET = "0";
    public static final String PATH_FILE = "/home/simsim/data/comics_files";
    public static final String COMICS_DIR = "/comics";
    public static final String CHARACTER_DIR = "/character";
    public static final DateFormat FORMAT_DATE_TO_STRING = new SimpleDateFormat("d.MM.yyyy");
    public static final DateFormat FORMAT_STRING_TO_DATE = new SimpleDateFormat("d-MM-yyyy");

}
