package com.example.jpaboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.example.jpaboard.entity.Board;
import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer>{
	
	Page<Board> findByBoardTitleContaining(PageRequest page, String searchTitle);
}
