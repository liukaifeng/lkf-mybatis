package com.lkf.mybatis.demo.service;


import com.lkf.mybatis.demo.model.Author;

import java.util.List;

/**
 * @author kaifeng
 */
public interface AuthorService {

    int addAuthor(Author author);

    int deleteAuthor(int id);

    int updateAuthor(Author author);

    List<Author> getAllAuthors();

    int getAllAuthorsCount();
}
