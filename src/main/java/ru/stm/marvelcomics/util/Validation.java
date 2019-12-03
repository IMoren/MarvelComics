package ru.stm.marvelcomics.util;

public class Validation {
    public static int LimitIsValid(int limit){
       return limit < 1 || limit > Const.LIM ? Const.LIM : limit;
    }

    public static int OffsetIsValid(int offset){
        return offset < 0 ? Const.OFS : offset;
    }
}
