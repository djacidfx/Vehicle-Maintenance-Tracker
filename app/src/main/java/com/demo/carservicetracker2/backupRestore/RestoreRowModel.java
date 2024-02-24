package com.demo.carservicetracker2.backupRestore;


public class RestoreRowModel {
    String DateModified;
    String path;
    String size;
    long timestamp;
    String title;

    public RestoreRowModel() {
    }

    public RestoreRowModel(String title, String path, String dateModified, String size, long timestamp) {
        this.title = title;
        this.path = path;
        this.DateModified = dateModified;
        this.size = size;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDateModified() {
        return this.DateModified;
    }

    public void setDateModified(String dateModified) {
        this.DateModified = dateModified;
    }

    public Long getTimestamp() {
        return Long.valueOf(this.timestamp);
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp.longValue();
    }
}
