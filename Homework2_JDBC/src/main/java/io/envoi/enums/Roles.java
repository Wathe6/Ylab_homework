package io.envoi.enums;

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
