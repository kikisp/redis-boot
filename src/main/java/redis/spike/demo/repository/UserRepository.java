package redis.spike.demo.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import redis.spike.demo.model.RoleUsername;

@Mapper
public interface UserRepository {

  @Select("SELECT username, role FROM user")
  List<RoleUsername> getAllUsers();

}
