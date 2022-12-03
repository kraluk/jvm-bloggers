package com.jvm_bloggers.entities.wykop;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import static java.util.Objects.requireNonNull;

@Entity
@Table(name = "tweet")
public class Wykop {

    @Id
    @GenericGenerator(
        name = "WYKOP_SEQ",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @Parameter(name = "sequence_name", value = "WYKOP_SEQ"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
        }
    )
    @GeneratedValue(generator = "WYKOP_SEQ")
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "posting_date", nullable = false)
    private LocalDateTime postingDate;

    @Column(name = "sent")
    private boolean sent;

    public Wykop(final String content, final LocalDateTime postingDate) {
        this.content = requireNonNull(content, "Content has to be not null!");
        this.postingDate = requireNonNull(postingDate, "Posting Date has to be not null!");
    }

    private Wykop() {
    }

    public void markAsSent() {
        this.sent = true;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getPostingDate() {
        return postingDate;
    }

    public boolean isSent() {
        return sent;
    }
}
