package com.finefinee.websocket.global.jwt.customsetting;

import com.finefinee.websocket.domain.member.entity.MemberEntity;
import com.finefinee.websocket.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberEntity memberEntity = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("username에 알맞은 user가 없습니다."));

        return (UserDetails) memberEntity;
    }
}
