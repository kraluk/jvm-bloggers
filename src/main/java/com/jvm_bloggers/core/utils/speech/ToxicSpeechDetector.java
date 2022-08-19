package com.jvm_bloggers.core.utils.speech;

import lombok.experimental.UtilityClass;

public interface ToxicSpeechDetector {

    boolean detect(final String text);

    @UtilityClass
    class Holder {

        static class DefaultDetector implements ToxicSpeechDetector {
            private final WordRepository repository;

            DefaultDetector(final WordRepository repository) {
                this.repository = repository;
            }

            @Override
            public boolean detect(final String text) {
                return repository.contains(text);
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






