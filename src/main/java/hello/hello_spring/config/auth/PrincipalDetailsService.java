package hello.hello_spring.config.auth;

import hello.hello_spring.domain.Member;
import hello.hello_spring.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        Member memberEntity = memberRepository.findByLoginId(username)
                .orElseThrow(()-> new UsernameNotFoundException("해당 아이디를 찾을 수 없습니다." + username));

        return new PrincipalDetails(memberEntity);
    }
}
