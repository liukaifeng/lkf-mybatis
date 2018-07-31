package com.lkf.mybatis.demo.service.impl;

import com.lkf.mybatis.demo.mapper.BlogMapper;
import com.lkf.mybatis.demo.model.Blog;
import com.lkf.mybatis.demo.service.BlogService;
import com.lkf.mybatis.demo.util.MybatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * @author kaifeng
 */
public class BlogServiceImpl implements BlogService {
    @Override
    public Blog getBlogWithPosts() {
        SqlSession sqlSession = null;
        try {
            sqlSession = MybatisUtil.getSqlSession();
            BlogMapper blogMapper = sqlSession.getMapper(BlogMapper.class);
            return blogMapper.getBlogWithPosts();
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    @Override
    public Blog getBlogWithPostsNested() {
        SqlSession sqlSession = null;
        try {
            sqlSession = MybatisUtil.getSqlSession();
            BlogMapper blogMapper = sqlSession.getMapper(BlogMapper.class);
            return blogMapper.getBlogWithPostsNested();
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    @Override
    public Blog getBlogOneToOne() {
        SqlSession sqlSession = null;
        try {
            sqlSession = MybatisUtil.getSqlSession();
            BlogMapper blogMapper = sqlSession.getMapper(BlogMapper.class);
            return blogMapper.getBlogOneToOne();
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    @Override
    public List<Blog> getAllBlog() {
        SqlSession sqlSession = null;
        try {
            sqlSession = MybatisUtil.getSqlSession();
            BlogMapper blogMapper = sqlSession.getMapper(BlogMapper.class);
            return blogMapper.getAllBlog();
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }
}
