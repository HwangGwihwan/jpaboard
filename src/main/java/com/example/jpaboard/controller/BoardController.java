package com.example.jpaboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.jpaboard.dto.BoardForm;
import com.example.jpaboard.entity.Board;
import com.example.jpaboard.repository.BoardRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class BoardController {
	@Autowired
	BoardRepository boardRepository;

	@GetMapping("/board/boardList")
	public String boardList(Model model
						, @RequestParam(value = "currentPage", defaultValue = "0") int currentPage
						, @RequestParam(value = "rowPerPage", defaultValue = "10") int rowPerPage
						, @RequestParam(value = "word", defaultValue = "") String word) {
		
		Sort sort = Sort.by("boardNo").descending();
		
		PageRequest pageable = PageRequest.of(currentPage, rowPerPage, sort); // 0페이지, 10개
		Page<Board> list = boardRepository.findByBoardTitleContaining(pageable, word);
		
		log.debug("list.getTotalElements(): " + list.getTotalElements()); // 전체 행의 사이즈
		log.debug("list.getTotalPages(): " + list.getTotalPages()); // 전체 페이지 사이즈
		log.debug("list.getNumber(): " + list.getNumber()); // 현재 페이지
		log.debug("list.getSize(): " + list.getSize()); // rowPerPage
		log.debug("list.isFirst(): " + list.isFirst()); // 1페이지인지 : 이전링크유무
		log.debug("list.hasNext(): " + list.hasNext()); // 다음이 있는지 : 다음링크유무
		
		model.addAttribute("boardList", list);
		model.addAttribute("prePage", list.getNumber()-1);
		model.addAttribute("nextPage", list.getNumber()+1);
		model.addAttribute("word", word);
		
		return "board/boardList";
	}
	
	@GetMapping("/board/addBoard")
	public String addBoard() {
		return "board/addBoard";
	}
	
	@PostMapping("/board/createBoard")
	public String createBoard(BoardForm form) {
		System.out.println(form.toString());
		Board board = form.toEntity();
		boardRepository.save(board);
		return "redirect:/board/boardList";
	}
	
	@GetMapping("/board/boardOne")
	public String boardOne(Model model, @RequestParam int boardNo) {
		Board board = boardRepository.findById(boardNo).orElse(null);
		model.addAttribute("board", board);
		return "board/boardOne";
	}
	
	@GetMapping("/board/modifyBoard")
	public String modifyBoard(Model model, @RequestParam int boardNo) {
		Board board = boardRepository.findById(boardNo).orElse(null);
		model.addAttribute("board", board);
		return "board/modifyBoard";
	}
	
	@PostMapping("/board/updateBoard")
	public String updateBoard(BoardForm form) {
		Board board = form.toEntity();
		boardRepository.save(board);
		return "redirect:/board/boardOne?boardNo=" + form.getBoardNo();
	}
	
	@GetMapping("/board/deleteBoard")
	public String deleteBoard(@RequestParam int boardNo) {
//		Board board = boardRepository.findById(boardNo).orElse(null);
//		boardRepository.delete(board);
		boardRepository.deleteById(boardNo);
		return "redirect:/board/boardList";
	}
}
