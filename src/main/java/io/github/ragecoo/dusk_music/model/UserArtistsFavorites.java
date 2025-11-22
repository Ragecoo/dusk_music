package io.github.ragecoo.dusk_music.model;


import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Valid
@Entity
@Table(name = "user_artist_favorites",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_artist", columnNames = {"user_id","artist_id"}),
        indexes = {
                @Index(name = "idx_uaf_user", columnList = "user_id"),
                @Index(name = "idx_uaf_artist", columnList = "artist_id")
        })
public class UserArtistsFavorites {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        @OnDelete(action = OnDeleteAction.CASCADE)
        @ToString.Exclude
        private User user;

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        @JoinColumn(name = "artist_id", nullable = false)
        @OnDelete(action = OnDeleteAction.CASCADE)
        @ToString.Exclude
        private Artist artist;

        @Column(name = "created_at", nullable = false)
        private LocalDateTime createdAt;

        @Override
        public final boolean equals(Object o) {
                if (this == o) return true;
                if (o == null) return false;
                Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
                Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
                if (thisEffectiveClass != oEffectiveClass) return false;
                UserArtistsFavorites that = (UserArtistsFavorites) o;
                return getId() != null && Objects.equals(getId(), that.getId());
        }

        @Override
        public final int hashCode() {
                return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
        }
}
