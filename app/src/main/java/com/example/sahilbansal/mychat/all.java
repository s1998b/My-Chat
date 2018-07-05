package com.example.sahilbansal.mychat;

/**
 * Created by Sahil Bansal on 23-03-2018.
 */

public class all {
    private String User_name;
    private String User_Status;
    private String User_image;
    private String User_thumb_image;

    public all(String user_name, String user_Status, String user_image, String user_thumb_image) {
        User_name = user_name;
        User_Status = user_Status;
        User_image = user_image;
        User_thumb_image = user_thumb_image;
    }

    public all() {
    }

    public String getUser_name() {
        return User_name;
    }

    public String getUser_Status() {
        return User_Status;
    }

    public String getUser_image() {
        return User_image;
    }

    public void setUser_name(String user_name) {
        User_name = user_name;
    }

    public void setUser_Status(String user_Status) {
        User_Status = user_Status;
    }

    public void setUser_image(String user_image) {
        User_image = user_image;
    }

    public String getUser_thumb_image() {
        return User_thumb_image;
    }

    public void setUser_thumb_image(String user_thumb_image) {
        User_thumb_image = user_thumb_image;
    }
}
