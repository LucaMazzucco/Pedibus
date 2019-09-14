package it.polito.appinternet.pedibus;

public enum Roles{
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_CONDUCTOR("ROLE_CONDUCTOR"),
    ROLE_ANONYMOUS("ROLE_ANONYMOUS");

    private String role;

    Roles(String role){
        this.role = role;
    }
    public String getRole(){
        return this.role;
    }
}
