package com.lkf.mybatis.demo.service.impl;

import com.lkf.mybatis.demo.mapper.PostMapper;
import com.lkf.mybatis.demo.model.Post;
import com.lkf.mybatis.demo.service.PostService;
import com.lkf.mybatis.demo.util.MybatisUtil;
import org.apache.ibatis.session.SqlSession;

/**
 * @author kaifeng
 */
public class PostServiceImpl implements PostService {

    @Override
    public Post getPostById(int id) {
        SqlSession sqlSession = null;
        try {
            sqlSession = MybatisUtil.getSqlSession();
            PostMapper postMapper = sqlSession.getMapper(PostMapper.class);
            return postMapper.getPostById(id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
        return null;
    }
}
