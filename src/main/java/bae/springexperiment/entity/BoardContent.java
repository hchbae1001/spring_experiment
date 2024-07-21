package bae.springexperiment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "BOARD_CONTENT")
public class BoardContent {
    @Id
    @Column(name = "content_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "board_id")
    private Board board;

    @Lob
    @Column(name = "content")
    private String content;

}