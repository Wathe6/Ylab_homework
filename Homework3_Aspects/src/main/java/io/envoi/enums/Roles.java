package io.envoi.enums;
/**
 * Roles for Account: User, Admin.
 * */
public enum Roles
{
    USER("User"),
    ADMIN("Admin");

    private final String displayName;

    Roles(String displayName)
    {
        this.displayName = displayName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    @Override
    public String toString()
    {
        return displayName;
    }
}