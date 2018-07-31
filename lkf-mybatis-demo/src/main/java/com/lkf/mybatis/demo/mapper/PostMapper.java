package com.lkf.mybatis.demo.mapper;


import com.lkf.mybatis.demo.model.Post;

/**
 * @author kaifeng
 */
public interface PostMapper {

    Post getPostById(int id);
}
