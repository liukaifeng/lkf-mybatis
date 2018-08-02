package com.lkf.mybatis.demo.mapper;


import com.lkf.mybatis.demo.model.Author;

import java.util.List;
import java.util.Map;

/**
 * @author kaifeng
 */
public interface AuthorMapper {

    List<Author> selectAuthorByPage(Map<String, Object> map);

    int addAuthor(Author author);

    int deleteAuthor(int id);

    int updateAuthor(Author author);

    List<Author> getAllAuthors();

    int getAllAuthorsCount();


}
