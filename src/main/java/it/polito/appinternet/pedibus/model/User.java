package it.polito.appinternet.pedibus.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polito.appinternet.pedibus.annotations.ValidPassword;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Document(collection = "users")
@NoArgsConstructor
@Getter @Setter
public class User implements UserDetails {
    @Id
    private String id;

    @Email
    private String email;
    @ValidPassword
    private String password;

    private String name;
    private String surname;
    private String registrationNumber;

    private boolean isEnabled; //Flag account abilitato
    private List<String> roles = new ArrayList<>();
    private List<String> adminLines = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();

    private boolean isParent;
    private List<String> childrenIds = new ArrayList<>();

    public User(String email, String password, boolean isEnabled) {
        this.email = email;
        this.password = password;
        this.isEnabled = isEnabled;
    }
    public User(@JsonProperty("email") String email,
                @JsonProperty("name") String name,
                @JsonProperty("surname") String surname,
                @JsonProperty("registrationNumber") String registrationNumber,
                @JsonProperty("isEnabled") boolean isEnabled,
                @JsonProperty("password") String password) {
        this.name=name;
        this.surname = surname;
        this.registrationNumber = registrationNumber;
        this.isEnabled=isEnabled;
        this.password=password;
        this.email=email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(SimpleGrantedAuthority::new).collect(toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
