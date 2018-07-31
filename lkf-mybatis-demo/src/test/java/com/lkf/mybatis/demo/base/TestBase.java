package com.lkf.mybatis.demo.base;


import com.lkf.mybatis.demo.util.MybatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;

/**
 * @author kaifeng
 */
public class TestBase {
    protected SqlSession sqlSession;

    @Before
    public void before() {
        sqlSession = MybatisUtil.getSqlSession();
    }

    @After
    public void after() {
        sqlSession.close();
    }
}
