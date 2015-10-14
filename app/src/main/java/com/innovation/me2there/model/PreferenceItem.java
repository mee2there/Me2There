package com.innovation.me2there.model;

/**
 * Created by ashley on 6/4/15.
 */

public  class PreferenceItem {
    String _prefernce;
    Boolean _isSelected;
    public PreferenceItem(String inPref){
        _isSelected = false;
        _prefernce = inPref;

    }
    public int length(){
        if(_prefernce == null){
            return 0;
        }
        return _prefernce.length();
    }
    public String getPreference(){return _prefernce;}
    public Boolean getIsSelected(){return _isSelected;}

    public void setSelected(Boolean selection){_isSelected = selection;}


}

