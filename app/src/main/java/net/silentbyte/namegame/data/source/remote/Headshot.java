package net.silentbyte.namegame.data.source.remote;

public class Headshot {

    private String type;
    private String mimeType;
    private String id;
    private String url;
    private String alt;
    private int height;
    private int width;

    private Headshot(Builder builder) {
        type = builder.type;
        mimeType = builder.mimeType;
        id = builder.id;
        url = builder.url;
        alt = builder.alt;
        height = builder.height;
        width = builder.width;
    }

    public String getType() {
        return type;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getAlt() {
        return alt;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public static class Builder {
        private String type;
        private String mimeType;
        private String id;
        private String url;
        private String alt;
        private int height;
        private int width;

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder mimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder alt(String alt) {
            this.alt = alt;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Headshot build() {
            return new Headshot(this);
        }
    }
}
