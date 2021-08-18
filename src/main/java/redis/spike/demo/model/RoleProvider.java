package redis.spike.demo.model;
import com.google.common.collect.Sets;
import org.springframework.security.core.authority.SimpleGrantedAuthority;



import java.util.Set;
import java.util.stream.Collectors;

import static redis.spike.demo.model.PermissionProvider.*;

public enum RoleProvider {
    USER(Sets.newHashSet(USER_READ)),
    ADMIN(Sets.newHashSet(USER_READ,USER_WRITE)),
    CLIENT(Sets.newHashSet(USER_READ));

    private final Set<PermissionProvider> permissions;

    RoleProvider(Set<PermissionProvider> permissions) {
        this.permissions = permissions;
    }

    public Set<PermissionProvider> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        Set<SimpleGrantedAuthority> permissionsList = getPermissions().stream()
            .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
            .collect(Collectors.toSet());
        permissionsList.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return permissionsList;
    }
}
