package com.demo.carservicetracker2.utils;


public interface ImageListener {
        void onImageReady(String filename);
        void onImageError(Throwable error);

}
