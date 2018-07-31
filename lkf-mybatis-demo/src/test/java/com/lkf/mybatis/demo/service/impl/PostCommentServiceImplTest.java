package com.lkf.mybatis.demo.service.impl;

import com.lkf.mybatis.demo.model.PostComment;
import com.lkf.mybatis.demo.service.PostCommentService;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class PostCommentServiceImplTest {

    private PostCommentService postCommentService;

    @Before
    public void setUp() throws Exception {
        postCommentService = new PostCommentServiceImpl();
    }

    @Test
    public void testSelectPostComment() throws Exception {
        PostComment postComment = postCommentService.selectPostComment();
        Assert.assertNotNull(postComment);
    }
}