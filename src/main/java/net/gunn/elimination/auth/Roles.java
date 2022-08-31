package net.gunn.elimination.auth;

import net.gunn.elimination.model.Role;

public class Roles {
    private Roles() {}

    public static final Role USER = new Role("ROLE_USER");
    public static final Role PLAYER = new Role("ROLE_PLAYER");
    public static final Role ADMIN = new Role("ROLE_ADMIN");
}
