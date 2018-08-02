package com.lkf.mybatis.demo.service.impl;

import com.lkf.mybatis.demo.mapper.PostCommentMapper;

import com.lkf.mybatis.demo.model.PostComment;
import com.lkf.mybatis.demo.service.PostCommentService;
import com.lkf.mybatis.demo.util.MybatisUtil;
import org.apache.ibatis.session.SqlSession;

/**
 * @author kaifeng
 */
public class PostCommentServiceImpl implements PostCommentService {
    @Override
    public PostComment selectPostComment() {
        SqlSession sqlSession = null;
        try {
            sqlSession = MybatisUtil.getSqlSession();
            PostCommentMapper postCommentMapper = sqlSession.getMapper(PostCommentMapper.class);
            return postCommentMapper.selectPostComment();
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }
}
