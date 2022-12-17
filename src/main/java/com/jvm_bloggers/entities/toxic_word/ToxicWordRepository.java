package com.jvm_bloggers.entities.toxic_word;

import io.vavr.collection.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ToxicWordRepository extends JpaRepository<ToxicWord, Long> {

    String TOXIC_WORDS_CACHE = "toxic_words";

    @Cacheable(TOXIC_WORDS_CACHE)
    List<ToxicWord> findAllWords();

    @CacheEvict(cacheNames = TOXIC_WORDS_CACHE)
    @Modifying
    @Query(
        value = """
            INSERT INTO TOXIC_WORD (language_code, value)
            VALUES (:language_code, :value)
            ON CONFLICT DO UPDATE SET updated_date = current_timestamp
            """,
        nativeQuery = true)
    void upsert(@Param("name") final String languageCode, @Param("value") final String value);

    default void upsert(final ToxicWord word) {
        upsert(word.getLanguageCode(), word.getValue());
    }
}
