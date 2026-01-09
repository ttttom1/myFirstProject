package hello.hello_spring.service;

import hello.hello_spring.domain.Member;
import hello.hello_spring.repository.MemberRepository;
import hello.hello_spring.repository.MemoryMemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


class MemberServiceTest {

    MemberService memberService ;
    MemoryMemberRepository memoryMemberRepository ;

    @BeforeEach
    public void beforeEach(){
        memoryMemberRepository = new MemoryMemberRepository();
        memberService = new MemberService(memoryMemberRepository);
    }


    @AfterEach
    public void afterEach(){
        memoryMemberRepository.clearStore();
    }

    @Test
    void join() {
        //given
        Member member = new Member();
        member.setName("Hello");

        //when
        Long id = memberService.join(member);

        //then
        Member result = memberService.findOne(id).get();

        assertThat(member.getName()).isEqualTo(result.getName());
    }

    @Test
    void 회원_중복_예외() {
        //given
        Member member1 = new Member();
        member1.setName("chanho");
        memberService.join(member1);

        Member member2 = new Member();
        member2.setName("chanho");

        //when
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
        //then


    }

    @Test
    void findMembers() {
    }

    @Test
    void findOne() {
    }
}