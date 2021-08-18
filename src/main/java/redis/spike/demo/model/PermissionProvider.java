package redis.spike.demo.model;

public enum PermissionProvider {
    USER_READ("user:read"),
    USER_WRITE("user:write");


    private final String permission;

    PermissionProvider(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
