package redis.spike.demo.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RoleUsername {

  private String username;
  private String role;

  public RoleUsername(String username, String role){
    this.username = username;
    this.role =  role;
  }
}
