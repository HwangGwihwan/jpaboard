package com.example.jpaboard.repository;

import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
	
	@Query(nativeQuery = true,
			value="select max(id) minId, min(id) maxId, count(*) cnt"
					+ " from article"
					+ " where title like :word")
	Map<String, Object> getMinMaxCount(String word); // word = "a%"
}
