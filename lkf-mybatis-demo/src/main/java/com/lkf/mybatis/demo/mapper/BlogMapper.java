package com.lkf.mybatis.demo.mapper;


import com.lkf.mybatis.demo.model.Blog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author kaifeng
 */
public interface BlogMapper {

    int updateBlogTitleSet(Blog blog);

    int updateBlogTitle(Blog blog);

    List<Blog> getBlogByConditionDynamicTrim(@Param("title") String title, @Param("section") String section);

    List<Blog> getBlogByConditionDynamic(@Param("title") String title, @Param("section") String section);

    List<Blog> getBlogByTitleOrSection(@Param("title") String title, @Param("section") String section);

    List<Blog> getBlogDynamic(@Param("title") String title, @Param("section") String section);

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

    Blog getBlogWithPosts();

    Blog getBlogWithPostsNested();
}
