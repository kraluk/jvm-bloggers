package com.jvm_bloggers.core.utils.speech;

import io.vavr.Value;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import io.vavr.collection.TreeSet;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import static org.apache.commons.collections4.CollectionUtils.size;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

class WordRepository {
    private final Set<String> elements;

    WordRepository(final Set<String> elements) {
        this.elements = elements;
    }

    boolean contains(final String text) {
        return elements.exists(e -> containsIgnoreCase(text, e));
    }

    @Slf4j
    static class WordRepositoryFactory {

        private final List<DictionaryAcquirer> acquirers;

        WordRepositoryFactory(final Iterable<DictionaryAcquirer> acquirers) {
            this.acquirers = List.ofAll(acquirers);
        }

        WordRepository create() {
            log.info("Attempting to execute the following acquirers - '{}'", acquirers);

            final var elements = acquirers
                    .map(this::acquire)
                    .flatMap(Value::toStream)
                    .toSet();

            log.info("Acquired '{}' elements", size(elements));
            return new WordRepository(elements);
        }

        private Set<String> acquire(final DictionaryAcquirer acquirer) {
            return Try.of(acquirer::acquire)
                    .onSuccess(s -> log.info("Got '{}' elements using '{}'", size(s), acquirer))
                    .onFailure(t -> log.error("Unable to execute successfully the following acquirer - '{}'", acquirer, t))
                    .getOrElse(TreeSet::empty);
        }
    }
}
