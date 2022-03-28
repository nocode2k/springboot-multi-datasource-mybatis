package nocode.example.service;

import lombok.RequiredArgsConstructor;
import nocode.example.domain.Member;
import nocode.example.mapper.master.MasterMemberMapper;
import nocode.example.mapper.slave.SlaveMemberMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MasterMemberMapper masterMemberMapper;
    private final SlaveMemberMapper slaveMemberMapper;

    public void viewMember(){
        System.out.println(slaveMemberMapper.findById(1));
    }

    public void saveMember() {
        Member member = new Member();
        member.setId(1);
        member.setName("Kimchi");
        masterMemberMapper.save(member);
    }
}
