package com.example.smsmessaging;

public class RecyclerData {




    private String title;
    private int folder;
    private String date;
    private int image;

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }










    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getFolder() {
        return folder;
    }

    public void setFolder(int folder) {
        this.folder = folder;
    }

    public RecyclerData(String title, int folder,String date, int image) {
        this.title = title;
        this.image = image;
        this.folder = folder;
        this.date = date;
    }

}
