package com.lkf.mybatis.demo.util;

import com.lkf.mybatis.demo.mapper.AuthorMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;

public class MybatisUtilTest {

    @Test
    public void testGetSqlSessionFactory() throws Exception {
        SqlSessionFactory sqlSessionFactory = MybatisUtil.getSqlSessionFactory();
        Assert.assertNotNull(sqlSessionFactory);
    }

    @Test
    public void testGetSqlSession() throws Exception {
        SqlSession sqlSession = MybatisUtil.getSqlSession();
        Assert.assertNotNull(sqlSession);

        AuthorMapper authorMapper = sqlSession.getMapper(AuthorMapper.class);
        Assert.assertNotNull(authorMapper);
        authorMapper.getAllAuthors();
    }
}