package com.example.dnb;

public class DataModel {
    private int id;
    private byte[] imageBytes;
    private String textContent;

    public DataModel(int id, byte[] imageBytes, String textContent) {
        this.id = id;
        this.imageBytes = imageBytes;
        this.textContent = textContent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }
}

