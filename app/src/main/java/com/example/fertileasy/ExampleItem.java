package com.example.fertileasy;


//Classe que define forma do item da lista
 class ExampleItem {
    private int mImageResource;
    private String mText1;
    private String mText2;

     ExampleItem(int imageResource, String text1,String text2){
        mImageResource = imageResource;
        mText1 = text1;
        mText2 = text2;


    }

     int getImageResource(){
        return mImageResource;


    }

    String getmText1() {
        return mText1;
    }
    String getmText2() {
        return mText2;
    }
}
