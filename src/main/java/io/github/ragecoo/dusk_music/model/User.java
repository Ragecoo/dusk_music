package io.github.ragecoo.dusk_music.model;

import io.github.ragecoo.dusk_music.model.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Valid
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "username", unique = true, nullable = false)
    @Size(max = 50)
    String username;

    @Column(name = "email", unique = true, nullable = false)
    @Size(max = 100)

    String email;

    @Column(name = "password_hash", nullable = false)
    String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    Role role;

    @Column(name = "created_at",nullable = false)
    LocalDateTime createdAt;

    @Column(name = "avatar_url", nullable = true)
    String avatarUrl;

    @Column(name = "subscription_status", nullable = false)
    boolean subActive;


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
