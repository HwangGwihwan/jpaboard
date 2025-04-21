package com.example.jpaboard.dto;

import com.example.jpaboard.entity.Board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardForm {
	private int boardNo;
	private String boardTitle;
	private String boardContent;
	
	public Board toEntity() {
		Board board = new Board();
		board.setBoardNo(this.boardNo);
		board.setBoardTitle(this.boardTitle);
		board.setBoardContent(this.boardContent);
		return board;
	}
}
