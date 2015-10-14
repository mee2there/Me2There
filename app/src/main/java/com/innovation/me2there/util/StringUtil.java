package com.innovation.me2there.util;

/**
 * Created by ashley on 9/16/15.
 */
public class StringUtil {

    public static String[] parseCSV(String inputStr){
        return inputStr.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

    }
}
