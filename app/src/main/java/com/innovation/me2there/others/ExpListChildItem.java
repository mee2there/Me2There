package com.innovation.me2there.others;


import java.util.List;

public class ExpListChildItem {

    public String Text;
    public List<String> childTexts;

    public String getMyText() {
        return Text;
    }

    public String getChildText() {
        return Text;
    }
    public List<String> getChildTexts() {
        return childTexts;
    }

    public ExpListChildItem(String text) {
        Text = text;
    }

    public ExpListChildItem(List<String> texts) {
        childTexts = texts;
    }

}
