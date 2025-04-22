package com.example.jpaboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.example.jpaboard.entity.Member;
import com.example.jpaboard.entity.MemberOnlyMemberId;


@Repository
public interface MemberRepository extends JpaRepository<Member, Integer>{
	// 아이디 중복 검사
	boolean existsByMemberId(String memberId);
	
	// 로그인 하는 추상메서드 : findBy...
	MemberOnlyMemberId findByMemberIdAndMemberPw(String memberId, String memberPw);
	
	// 멤버목록
	Page<MemberOnlyMemberId> findByMemberIdContaining(PageRequest pageable, String word);

	// 삭제
	Member findByMemberId(String memberId);
}
