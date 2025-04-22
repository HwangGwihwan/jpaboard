package com.example.jpaboard.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.jpaboard.dto.ArticleForm;
import com.example.jpaboard.entity.Article;
import com.example.jpaboard.repository.ArticleRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ArticleController {
	@Autowired // 의존성주입
	private ArticleRepository articleRepository;

	@GetMapping("/articles/sqlTest")
	public String sqlTest(Model model) {
		Map<String, Object> map = articleRepository.getMinMaxCount("a%");
		log.debug(map.toString());
		model.addAttribute("map", map);
		return "articles/sqlTest";
	}
	
	@GetMapping("/articles/new") // doGet()
	public String newArticleForm() {
		return "articles/new"; // forward
	}
	
	@PostMapping("/articles/create") // doPost()
	public String createArticle(ArticleForm form) { // @RequestParam, DTO(커멘드객체)
		System.out.println(form.toString());
		// DTO --> Entity
		Article entity = form.toEntity();
		articleRepository.save(entity); // 레포지토리 호출할때는 Entity가 필요
		return "redirect:/articles/index"; // "/"으로 리다이렉트... redirect?
	}
	
	@GetMapping("/articles/index")
	public String articleList(Model model
							, @RequestParam(value = "currentPage", defaultValue = "0") int currentPage
							, @RequestParam(value = "rowPerPage", defaultValue = "10") int rowPerPage
							, @RequestParam(value = "word", defaultValue = "") String word) {
		
		Sort sort = Sort.by("id").descending();
		
		PageRequest pageable = PageRequest.of(currentPage, rowPerPage, sort); // 0페이지, 10개
		Page<Article> list = articleRepository.findByTitleContaining(pageable, word);
		
		// Page의 추가 속성
		log.debug("list.getTotalElements(): " + list.getTotalElements()); // 전체 행의 사이즈
		log.debug("list.getTotalPages(): " + list.getTotalPages()); // 전체 페이지 사이즈
		log.debug("list.getNumber(): " + list.getNumber()); // 현재 페이지
		log.debug("list.getSize(): " + list.getSize()); // rowPerPage
		log.debug("list.isFirst(): " + list.isFirst()); // 1페이지인지 : 이전링크유무
		log.debug("list.hasNext(): " + list.hasNext()); // 다음이 있는지 : 다음링크유무
		
		model.addAttribute("articleList", list);
		model.addAttribute("prePage", list.getNumber()-1);
		model.addAttribute("nextPage", list.getNumber()+1);
		model.addAttribute("word", word);
		// redirect로 호출되었다면 + RedirectAttribute
		return "articles/index";
	}
	
	@GetMapping("/articles/show")
	public String show(Model model, @RequestParam Long id) {
		Article article = articleRepository.findById(id).orElse(null);
		model.addAttribute("article", article);
		return "articles/show";
	}
	
	@GetMapping("/articles/edit")
	public String edit(Model model, @RequestParam Long id) {
		Article article = articleRepository.findById(id).orElse(null);
		model.addAttribute("article", article);
		return "articles/edit";
	}
	
	@PostMapping("/articles/update")
	public String update(ArticleForm form) {
		Article article = form.toEntity(); 
		// entity가 키값을 가지고 있으면 새로운 행을 추가하는게 아니고
		// 존재하는 키값을 행을 수정
		articleRepository.save(article);
		return "redirect:/articles/show?id=" + form.getId();
	}
	
	@GetMapping("/articles/delete")
	public String delete(@RequestParam long id, RedirectAttributes rda) {
		Article article = articleRepository.findById(id).orElse(null);
		
		if (article == null) {
			rda.addFlashAttribute("msg", "삭제 실패");  // redirect의 뷰의 모델에서 자동으로 출력
			return "redirect:/articles/show?id=" + article.getId();
		}
		
		articleRepository.delete(article);
		rda.addFlashAttribute("msg", "삭제 성공");  // redirect의 뷰의 모델에서 자동으로 출력
		return "redirect:/articles/index";
	}
}
