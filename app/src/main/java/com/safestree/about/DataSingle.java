package com.safestree.about;

public class DataSingle {
    private String image,caption;
    public DataSingle(){};

    public DataSingle(String image, String caption) {
        this.image = image;
        this.caption = caption;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
