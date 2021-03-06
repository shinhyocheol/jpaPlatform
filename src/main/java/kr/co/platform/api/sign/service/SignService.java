package kr.co.platform.api.sign.service;

import kr.co.platform.api.member.domain.entity.Members;

import kr.co.platform.api.member.domain.repository.MemberRepository;
import kr.co.platform.api.sign.dto.AuthenticationDto;
import kr.co.platform.api.sign.dto.JoinDto;
import kr.co.platform.api.sign.dto.LoginDto;
import kr.co.platform.model.CustomModelMapper;
import kr.co.platform.util.advice.exception.DuplicatedException;
import kr.co.platform.util.advice.exception.ForbiddenException;
import kr.co.platform.util.advice.exception.UserNotFoundException;
import kr.co.platform.util.empty.Empty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

import java.security.SecureRandom;
import java.util.Base64;

@Service("signService")
@Slf4j
@AllArgsConstructor
public class SignService {

	private MemberRepository memberRepository;

	private BCryptPasswordEncoder passwordEncoder;
	
	private CustomModelMapper modelMapper;

	public Boolean joinService(JoinDto joinDto) {

		// 아이디 중복체크
		if (!Empty.validation(memberRepository.countByEmail(joinDto.getEmail())))
			throw new DuplicatedException("Duplicated ID");

		// 연락처 중복체크
		if (!Empty.validation(memberRepository.countByMobile(joinDto.getMobile())))
			throw new DuplicatedException("Duplicated Mobile");

		// 비밀번호 암호화처리
		joinDto.setPassword(passwordEncoder.encode(joinDto.getPassword()));

		// 데이터 등록(저장)
		memberRepository.save(joinDto.toEntity());

		return true;
	}

	public AuthenticationDto loginService(LoginDto loginDto) {
		
		// dto -> entity
		Members loginEntity = loginDto.toEntity();

		// 회원 엔티티 객체 생성 및 조회시작
		Members member = memberRepository.findByEmail(loginEntity.getEmail())
				.orElseThrow(() -> new UserNotFoundException("User Not Found"));

		if (!passwordEncoder.matches(loginEntity.getPassword(), member.getPassword()))
			throw new ForbiddenException("Passwords do not match");

		// 회원정보를 인증클래스 객체(authentication)로 매핑
		AuthenticationDto authentication = modelMapper.toMapping(member, AuthenticationDto.class);

		return authentication;
	}

}
