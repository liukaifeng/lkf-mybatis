package com.lkf.mybatis.demo.model;

/**
 * @author kaifeng
 */
@SuppressWarnings("unused")
public class PostComment {
    private int id;
    private String name;
    private String commentText;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    @Override
    public String toString() {
        return "PostComment{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", commentText='" + commentText + '\'' +
                '}';
    }
}
