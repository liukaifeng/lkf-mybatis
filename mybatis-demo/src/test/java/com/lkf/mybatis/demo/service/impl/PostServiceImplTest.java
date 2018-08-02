package com.lkf.mybatis.demo.service.impl;

import com.lkf.mybatis.demo.model.Post;
import com.lkf.mybatis.demo.service.PostService;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class PostServiceImplTest {

    private PostService postService;

    @Before
    public void setUp() throws Exception {
        postService = new PostServiceImpl();
    }

    @Test
    public void testGetPostById() throws Exception {
        Post post = postService.getPostById(1);
        Assert.assertNotNull(post);
    }
}