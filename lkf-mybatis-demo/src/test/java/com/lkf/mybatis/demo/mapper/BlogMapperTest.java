package com.lkf.mybatis.demo.mapper;

import com.lkf.mybatis.demo.model.Blog;
import com.lkf.mybatis.demo.util.MybatisUtil;
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author kaifeng
 */
public class BlogMapperTest {

    private BlogMapper blogMapper;

    public BlogMapperTest() {
        blogMapper = MybatisUtil.getSqlSession().getMapper(BlogMapper.class);
    }


    @Test
    public void testGetBlogDynamic() throws Exception {
        List<Blog> blogList = blogMapper.getBlogDynamic("title", "section");
        Assert.assertNotNull(blogList);
    }

    @Test
    public void testGetBlogByTitleOrSection() throws Exception {
        List<Blog> blogList = blogMapper.getBlogByTitleOrSection("", "");
        Assert.assertNotNull(blogList);
    }

    @Test
    public void testGetBlogByConditionDynamic() throws Exception {
        List<Blog> blogList = blogMapper.getBlogByConditionDynamic("", "section");
        Assert.assertNotNull(blogList);
    }

    @Test
    public void testGetBlogByConditionDynamicTrim() throws Exception {
        List<Blog> blogList = blogMapper.getBlogByConditionDynamicTrim("title", "section");
        Assert.assertNotNull(blogList);
    }

    @Test
    public void testUpdateBlogTitle() throws Exception {
        Blog blog = new Blog(1, "title1");
        int flag = this.blogMapper.updateBlogTitle(blog);
        Assert.assertEquals(true, flag > 0);
    }

    @Test
    public void testUpdateBlogTitleSet() throws Exception {
        Blog blog = new Blog(1, "");
        int flag = this.blogMapper.updateBlogTitleSet(blog);
        Assert.assertEquals(true, flag > 0);
    }
}