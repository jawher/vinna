package vinna.vinnernews.model;

import vinna.vinnernews.util.Seo;

import java.util.Date;

public class Submission {
    private Long id;
    private String title;
    private String seoTitle;
    private String link;
    private String text;
    private String submitter;
    private String domain="youtube.com";
    private Date creationDate = new Date();

    public Submission() {
    }

    public Submission(String title, String link, String text, String submitter) {
        this.title = title;
        this.seoTitle = Seo.seo(title);
        this.link = link;
        this.text = text;
        this.submitter = submitter;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.seoTitle = Seo.seo(title);
    }

    public String getSeoTitle() {
        return seoTitle;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSubmitter() {
        return submitter;
    }

    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Date getCreationDate() {
        return creationDate;
    }
}
