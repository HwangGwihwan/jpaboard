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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.jpaboard.dto.MemberForm;
import com.example.jpaboard.entity.Member;
import com.example.jpaboard.entity.MemberOnlyMemberId;
import com.example.jpaboard.repository.MemberRepository;
import com.example.jpaboard.util.SHA256Util;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MemberController {
	@Autowired
	MemberRepository memberRepository;
	
	// 회원가입 + 아이디 중복 확인
	@GetMapping("/member/joinMember")
	public String joinMember() {
		return "/member/joinMember";
	}
	
	@PostMapping("/member/joinMember")
	public String joinMember(MemberForm memberform, RedirectAttributes rda) {
		log.debug(memberform.toString());
		log.debug("isMemberId : " + memberRepository.existsByMemberId(memberform.getMemberId()));
		
		if (memberRepository.existsByMemberId(memberform.getMemberId())) {
			rda.addFlashAttribute("msg", memberform.getMemberId() + " ID가 이미 존재합니다");
			return "redirect:/member/joinMember";
		}
		
		// 회원가입 진행
		// memberform.getMemberPw()값을 SHA-256방식으로 암호화
		memberform.setMemberPw(SHA256Util.encoding(memberform.getMemberPw()));
		Member member = memberform.toEntity();
		memberRepository.save(member);
		
		return "redirect:/";
	}
	
	// 로그인
	@GetMapping("/member/login")
	public String login() {
		return "member/login";
	}
	
	@PostMapping("/member/login")
	public String login(HttpSession session, MemberForm memberform, RedirectAttributes rda) {
		memberform.setMemberPw(SHA256Util.encoding(memberform.getMemberPw()));
		MemberOnlyMemberId member 
			= memberRepository.findByMemberIdAndMemberPw(memberform.getMemberId(), memberform.getMemberPw());
		
		if (member == null) {
			rda.addFlashAttribute("msg", "로그인 실패");
			return "redirect:/member/login";
		}
		
		session.setAttribute("member", member);
		return "redirect:/member/memberList";
	}
	
	// 로그아웃
	@GetMapping("member/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
	
	// 회원목록
	@GetMapping("/member/memberList")
	public String memberList(HttpSession session, Model model
						, @RequestParam(value="currentPage", defaultValue = "0") int currentPage
						, @RequestParam(value="rowPerPage", defaultValue = "6") int rowPerPage
						, @RequestParam(value="word", defaultValue = "") String word) {
		
		// session 인증/인가 검사
		if (session.getAttribute("member") == null) {
			return "redirect:/member/login";
		}
		
		// 사용자 목록 + 페이징 + id 검색
		Sort sort = Sort.by("memberId").ascending();
		PageRequest pageable = PageRequest.of(currentPage, rowPerPage, sort);
		Page<MemberOnlyMemberId> list = memberRepository.findByMemberIdContaining(pageable, word);
		
		model.addAttribute("memberList", list);
		model.addAttribute("prePage", list.getNumber()-1);
		model.addAttribute("nextPage", list.getNumber()+1);
		model.addAttribute("word", word);
		
		return "member/memberList";
	}
	
	// 회원정보수정
	@GetMapping("/member/modifyMemberPw")
	public String update(HttpSession session, Model model) {
		// session 인증/인가 검사
		if (session.getAttribute("member") == null) {
			return "redirect:/member/login";
		}
		
		return "/member/modifyMemberPw";
	}
	
	@PostMapping("/member/modifyMemberPw")
	public String update(MemberForm memberform, @RequestParam String memberupdatePw, RedirectAttributes rda, HttpSession session) {
		memberform.setMemberPw(SHA256Util.encoding(memberform.getMemberPw()));
		MemberOnlyMemberId member = memberRepository.findByMemberIdAndMemberPw(memberform.getMemberId(), memberform.getMemberPw());
		
		if (member == null) { // 수정실패
			rda.addFlashAttribute("msg", "비밀번호수정 실패");
			return "redirect:/member/modifyMemberPw";
		}

		memberform.setMemberPw(SHA256Util.encoding(memberupdatePw));
		memberRepository.save(memberform.toEntity());
				
		session.invalidate();
		return "redirect:/";
	}
	
	// 회원탈퇴
	@GetMapping("/member/removeMember")
	public String removeMembmer(HttpSession session) {
		// session 인증/인가 검사
		if (session.getAttribute("member") == null) {
			return "redirect:/member/login";
		}
		
		return "member/removeMember";
	}
	
	@PostMapping("/member/removeMember")
	public String removeMember(MemberForm memberform, RedirectAttributes rda, HttpSession session) {
		memberform.setMemberPw(SHA256Util.encoding(memberform.getMemberPw()));
		MemberOnlyMemberId member = memberRepository.findByMemberIdAndMemberPw(memberform.getMemberId(), memberform.getMemberPw());
		
		if (member == null) { // 탈퇴실패
			rda.addFlashAttribute("msg", "탈퇴실패");
			return "redirect:/member/removeMember";
		}
		
		// MemberOnlyMemberId member 는 Member 객체가 아니므로 delete 하면 오류 -> 삭제하려면 Member 객체로...
		Member entity = memberRepository.findByMemberId(member.getMemberId());
		memberRepository.delete(entity);
		session.invalidate();
		return "redirect:/";
	}	
}
