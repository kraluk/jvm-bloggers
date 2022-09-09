package com.jvm_bloggers.core.utils.toxic_speech;

import com.jvm_bloggers.entities.toxic_word.ToxicWordRepository;

import lombok.experimental.UtilityClass;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

public interface ToxicSpeechDetector {

    boolean detect(final String text);

    @UtilityClass
    class Holder {

        static class DefaultDetector implements ToxicSpeechDetector {
            private final ToxicWordRepository repository;

            DefaultDetector(final ToxicWordRepository repository) {
                this.repository = repository;
            }

            @Override
            public boolean detect(final String text) {
                return repository.findAllWords()
                    .exists(e -> containsIgnoreCase(text, e.getValue()));
            }
        }

        static class NoOpDetector implements ToxicSpeechDetector {

            @Override
            public boolean detect(final String text) {
                return false;
            }
        }
    }
}






