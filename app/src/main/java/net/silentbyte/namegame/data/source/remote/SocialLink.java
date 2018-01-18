package net.silentbyte.namegame.data.source.remote;

public class SocialLink {

    private String type;
    private String callToAction;
    private String url;

    public SocialLink(Builder builder) {
        type = builder.type;
        callToAction = builder.callToAction;
        url = builder.url;
    }

    public static class Builder {
        private String type;
        private String callToAction;
        private String url;

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder callToAction(String callToAction) {
            this.callToAction = callToAction;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public SocialLink build() {
            return new SocialLink(this);
        }
    }
}
