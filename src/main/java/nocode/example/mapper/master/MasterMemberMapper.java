package nocode.example.mapper.master;

import nocode.example.config.MasterConnection;
import nocode.example.domain.Member;

@MasterConnection
public interface MasterMemberMapper {
    void save(Member member);
}
