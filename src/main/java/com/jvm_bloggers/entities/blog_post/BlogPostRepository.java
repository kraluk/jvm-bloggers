package com.jvm_bloggers.entities.blog_post;

import com.jvm_bloggers.entities.blog.BlogType;

import io.vavr.control.Option;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

    Option<BlogPost> findByUrlEndingWith(String urlWithoutProtocol);

    Option<BlogPost> findByUid(String uid);

    List<BlogPost> findByApprovedDateAfterAndApprovedTrueOrderByApprovedDateAsc(
        LocalDateTime publishedDate);

    List<BlogPost> findByApprovedTrueAndBlogAuthorNotInOrderByApprovedDateDesc(
        Pageable page, Set<String> excludedAuthors);

    @Query("""
        from BlogPost bp order by \
        case when bp.approved is null then 0 else 1 end, \
        bp.publishedDate desc
        """)
    List<BlogPost> findLatestPosts(Pageable page);

    int countByPublishedDateAfter(LocalDateTime publishedDate);

    int countByApprovedIsNull();

    List<BlogPost> findByBlogIdAndApprovedTrueOrderByPublishedDateDesc(Long blogId, Pageable page);

    List<BlogPost> findByBlogIdOrderByPublishedDateDesc(Long blogId, Pageable page);

    int countByBlogId(Long blogId);

    @Query("FROM BlogPost bp JOIN bp.blog b WHERE b.blogType = :blogType")
    List<BlogPost> findBlogPostsOfType(@Param("blogType") BlogType blogType, Pageable page);

    @Query("""
        FROM BlogPost bp JOIN bp.blog b \
        WHERE b.blogType = :blogType \
        AND bp.approved is null \
        ORDER BY bp.publishedDate DESC
        """)
    List<BlogPost> findUnapprovedPostsByBlogType(@Param("blogType") BlogType blogType,
                                                 Pageable page);

    /**
     * Be aware: proper indexes are not set up to support this query efficiently.
     */
    @Query("""
        SELECT DISTINCT bp FROM BlogPost bp LEFT JOIN bp.tags t
        WHERE bp.approved = true
        AND (LOWER(bp.title) LIKE LOWER(CONCAT('%', :phrase, '%'))
        OR  LOWER(t.value)  LIKE LOWER(CONCAT('%', :phrase, '%')))
        ORDER BY bp.publishedDate DESC
        """)
    List<BlogPost> findApprovedPostsByTagOrTitle(@Param("phrase") String phrase, Pageable page);

    /**
     * Be aware: proper indexes are not set up to support this query efficiently.
     */
    @Query("""
        SELECT COUNT(DISTINCT bp) FROM BlogPost bp LEFT JOIN bp.tags t
        WHERE bp.approved = true
        AND (LOWER(bp.title) LIKE LOWER(CONCAT('%', :phrase, '%'))
        OR LOWER(t.value)  LIKE LOWER(CONCAT('%', :phrase, '%')))
        """)
    int countApprovedPostsByTagOrTitle(@Param("phrase") String phrase);

}
