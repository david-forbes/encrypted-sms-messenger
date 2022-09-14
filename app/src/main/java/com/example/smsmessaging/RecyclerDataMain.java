package com.example.smsmessaging;


public class RecyclerDataMain {

    private String title;
    private int folder;
    private String message;
    private Boolean read;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private String time;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public RecyclerDataMain(String title, int folder, String message, String time, Boolean read) {
        this.title = title;
        this.read = read;
        this.folder = folder;
        this.message = message;
        this.time = time;
    }
}