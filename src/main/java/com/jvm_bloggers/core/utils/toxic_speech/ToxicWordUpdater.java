package com.jvm_bloggers.core.utils.toxic_speech;

import com.jvm_bloggers.entities.toxic_word.ToxicWord;
import com.jvm_bloggers.entities.toxic_word.ToxicWordRepository;

import io.vavr.Value;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import io.vavr.control.Try;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;

import static org.apache.commons.collections4.CollectionUtils.size;

@Slf4j
class ToxicWordUpdater {

    private final ToxicWordRepository repository;
    private final List<DictionaryAcquirer> acquirers;

    ToxicWordUpdater(final ToxicWordRepository repository,
                     final List<DictionaryAcquirer> acquirers) {
        this.repository = repository;
        this.acquirers = acquirers;
    }

    @Scheduled(cron = "${scheduler.update-toxic-words}")
    void update() {
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
}
