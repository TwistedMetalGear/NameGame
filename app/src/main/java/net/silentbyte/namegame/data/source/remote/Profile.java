package net.silentbyte.namegame.data.source.remote;

import java.util.List;

/**
 * Models an individual employee profile from the response of a GET /profiles request from the WillowTree server.
 */
public class Profile {

    private String id;
    private String type;
    private String slug;
    private String jobTitle;
    private String firstName;
    private String lastName;
    private Headshot headshot;
    private List<SocialLink> socialLinks;

    private Profile(Builder builder) {
        id = builder.id;
        type = builder.type;
        slug = builder.slug;
        jobTitle = builder.jobTitle;
        firstName = builder.firstName;
        lastName = builder.lastName;
        headshot = builder.headshot;
        socialLinks = builder.socialLinks;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getSlug() {
        return slug;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Headshot getHeadshot() {
        return headshot;
    }

    public List<SocialLink> getSocialLinks() {
        return socialLinks;
    }

    public static class Builder {
        private String id;
        private String type;
        private String slug;
        private String jobTitle;
        private String firstName;
        private String lastName;
        private Headshot headshot;
        private List<SocialLink> socialLinks;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder slug(String slug) {
            this.slug = slug;
            return this;
        }

        public Builder jobTitle(String jobTitle) {
            this.jobTitle = jobTitle;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder headshot(Headshot headshot) {
            this.headshot = headshot;
            return this;
        }

        public Builder socialLinks(List<SocialLink> socialLinks) {
            this.socialLinks = socialLinks;
            return this;
        }

        public Profile build() {
            return new Profile(this);
        }
    }
}
