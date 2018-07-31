package com.lkf.mybatis.demo.service;

import com.lkf.mybatis.demo.model.Blog;

import java.util.List;

/**
 * @author kaifeng
 */
public interface BlogService {

    Blog getBlogWithPosts();

    Blog getBlogWithPostsNested();

    /**
     * Only fill id, Author properties of Blog.
     *
     * @return Blog info with id and author.
     */
    Blog getBlogOneToOne();

    /**
     * Get Blog full info.
     *
     * @return Blog
     */
    List<Blog> getAllBlog();

}
