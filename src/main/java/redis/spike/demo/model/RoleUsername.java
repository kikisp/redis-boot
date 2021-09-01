package redis.spike.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
public class RoleUsername implements Serializable {

  private String username;
  private String role;

  public RoleUsername(String username, String role){
    this.username = username;
    this.role =  role;
  }
}
