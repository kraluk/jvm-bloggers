package com.jvm_bloggers.entities.newsletter_issue;

import com.jvm_bloggers.entities.blog.Blog;
import com.jvm_bloggers.entities.blog_post.BlogPost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Singular;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "newsletter_issue")
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
@Builder
@Getter
@ToString(exclude = {"blogPosts", "newBlogs"})
public class NewsletterIssue {

    @Id
    @GenericGenerator(
        name = "NEWSLETTER_ISSUE_SEQ",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @Parameter(name = "sequence_name", value = "NEWSLETTER_ISSUE_SEQ"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
        }
    )
    @GeneratedValue(generator = "NEWSLETTER_ISSUE_SEQ")
    @Column(name = "ID")
    private Long id;

    @NonNull
    @Column(name = "ISSUE_NUMBER", nullable = false, updatable = false)
    private Long issueNumber;

    @Column(name = "PUBLISHED_DATE", updatable = false)
    private LocalDate publishedDate;

    @Column(name = "HEADING", length = 4000, updatable = false)
    private String heading;

    @Singular
    @NonNull
    @OneToMany(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(name = "blog_posts_in_newsletter_issue",
        joinColumns = {@JoinColumn(name = "newsletter_issue_id", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name = "blog_post_id", referencedColumnName = "id")}
    )
    private List<BlogPost> blogPosts;

    @Singular
    @NonNull
    @OneToMany(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(name = "new_blogs_in_newsletter_issue",
        joinColumns = {@JoinColumn(name = "newsletter_issue_id", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name = "new_blog_id", referencedColumnName = "id")}
    )
    private List<Blog> newBlogs;

    @Column(name = "VARIA", length = 4000, updatable = false)
    private String varia;

}
