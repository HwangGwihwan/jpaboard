package com.example.jpaboard.repository;

import java.awt.print.Pageable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.example.jpaboard.entity.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>{
	// CrudRepository : insert, select one, select all, update, delete
	
	// JpaRepsository(CrudRepository 자식 인터페이스) : select limit, select order by, ... 
	
	// findAll() : 원하는 컬럼만 가지고 오도록 ...
	//List<ArticleMapping> findAllBy(PageRequest p);
	Page<Article> findByTitleContaining(PageRequest pageable, String searchTitle);
}
