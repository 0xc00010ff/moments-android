package com.tikkunolam.momentsintime;

public class Video {

    /**
     * INSTANCE VARIABLES
     */

    // the name of the video
    public String name;

    // the description of the video
    public String description;

    // the uri for any further operation on the video
    public String uri;

    // the playback url
    public String url;

    // the url of the video thumbnail
    public String pictureUrl;

    // the width of the video
    public int width;

    // the height of the video
    public int height;

    // if the video is available (relevant after uploading)
    public boolean available;

    /**
     * CONSTRUCTORS
     */

    public Video(String name, String description, String uri, String url, String pictureUrl, int width, int height) {

        this.name = name;
        this.description = description;
        this.uri = uri;
        this.url = url;
        this.pictureUrl = pictureUrl;
        this.width = width;
        this.height = height;

    }

    /**
     * METHODS
     */

    public String getName() {

        return name;

    }

    public String getDescription() {

        return description;

    }


    public String getUri() {

        return uri;

    }

    public String getUrl() {

        return url;

    }

    public String getPictureUrl() {

        return pictureUrl;

    }

    public int getWidth() {

        return width;

    }

    public int getHeight() {

        return height;

    }


}

