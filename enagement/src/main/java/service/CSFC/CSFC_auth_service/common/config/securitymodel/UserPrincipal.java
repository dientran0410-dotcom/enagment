package service.CSFC.CSFC_auth_service.common.config.securitymodel;

public class UserPrincipal {
    private final String userId;
    private final String name;
    public UserPrincipal(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }
    public String getUserId() {
        return userId;
    }
    public String getName() {
        return name;
    }
}

