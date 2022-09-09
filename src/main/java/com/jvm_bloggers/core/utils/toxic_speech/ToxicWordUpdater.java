package com.jvm_bloggers.core.utils.toxic_speech;

import com.jvm_bloggers.entities.toxic_word.ToxicWord;
import com.jvm_bloggers.entities.toxic_word.ToxicWordRepository;

import io.vavr.Value;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import io.vavr.control.Try;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Objects;

import static org.apache.commons.collections4.CollectionUtils.size;

@Slf4j
class ToxicWordUpdater {

    private final ToxicWordRepository repository;
    private final List<DictionaryAcquirer> acquirers;
    private final CacheManager cacheManager;

    ToxicWordUpdater(final ToxicWordRepository repository,
                     final List<DictionaryAcquirer> acquirers,
                     final CacheManager cacheManager) {
        this.repository = repository;
        this.acquirers = acquirers;
        this.cacheManager = cacheManager;
    }

    @Scheduled(cron = "${scheduler.update-toxic-words}")
    void update() {
        clearCache();

        acquirers
            .map(this::acquire)
            .flatMap(Value::toStream)
            .forEach(repository::upsert);
    }

    private Set<ToxicWord> acquire(final DictionaryAcquirer acquirer) {
        return Try.of(acquirer::acquire)
            .onSuccess(s -> log.info("Got '{}' elements using '{}'", size(s), acquirer))
            .onFailure(t -> log.error("Unable to execute successfully the following acquirer - '{}'", acquirer, t))
            .getOrElse(HashSet::empty);
    }

    private void clearCache() {
        Option.of(cacheManager.getCache(ToxicWordRepository.TOXIC_WORDS_CACHE))
            .filter(Objects::nonNull)
            .forEach(Cache::clear);
    }
}
