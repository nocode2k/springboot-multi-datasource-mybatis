package nocode.example.mapper.slave;

import nocode.example.config.SlaveConnection;
import nocode.example.domain.Member;

@SlaveConnection
public interface SlaveMemberMapper {
    Member findById(int id);
}
