package bae.springexperiment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "MEMBER")
public class Member {
    @Id
    @Column(name = "member_id", nullable = false)
    private Long id;

    @Column(name = "phone", length = 11)
    private String phone;

    @Column(name = "email", length = 40)
    private String email;

    @Column(name = "name", length = 20)
    private String name;

    @Column(name = "nickname", length = 30)
    private String nickname;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @ColumnDefault("0")
    @Column(name = "is_removed")
    private Boolean isRemoved;

}