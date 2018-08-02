package com.lkf.mybatis.demo.mapper;

import com.lkf.mybatis.demo.entity.Page;
import com.lkf.mybatis.demo.mapper.AuthorMapper;
import com.lkf.mybatis.demo.model.Author;
import com.lkf.mybatis.demo.util.MybatisUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kaifeng
 */
public class AuthorMapperTest {

    private AuthorMapper authorMapper;

    public AuthorMapperTest() {
        this.authorMapper = MybatisUtil.getSqlSession().getMapper(AuthorMapper.class);
    }

    @Test
    public void testUpdateAuthor() throws Exception {
        Author author = new Author(1, "alien", "alien", "email", "bio", "fa");
        List<Author> authors = this.authorMapper.getAllAuthors();
        Assert.assertNotNull(authors);
    }

    @Test
    public void testSelectAuthorByPage() throws Exception {
        Page page = new Page();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("page", page);
        List<Author> authors = this.authorMapper.selectAuthorByPage(map);
        Assert.assertEquals(5, authors.size());
    }
}