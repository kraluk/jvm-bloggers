package com.jvm_bloggers.core.utils.toxic_speech;

import com.jvm_bloggers.entities.toxic_word.ToxicWord;

import io.vavr.Function1;
import io.vavr.collection.Set;

import lombok.ToString;

import org.springframework.web.reactive.function.client.WebClient;

interface DictionaryAcquirer {

    Set<ToxicWord> acquire();

    static DictionaryAcquirer polishAcquirer(final WebClient client,
                                             final String dictionaryPath) {
        return new DefaultDictionaryAcquirer(client, dictionaryPath, DictionaryReader::polishReader);
    }

    static DictionaryAcquirer englishAcquirer(final WebClient client,
                                              final String dictionaryPath) {
        return new DefaultDictionaryAcquirer(client, dictionaryPath, DictionaryReader::englishReader);
    }

    // --- Implementations

    @ToString(of = "dictionaryPath")
    class DefaultDictionaryAcquirer implements DictionaryAcquirer {

        private final WebClient client;
        private final String dictionaryPath;
        private final Function1<String, DictionaryReader> reader;

        DefaultDictionaryAcquirer(final WebClient client,
                                  final String dictionaryPath,
                                  final Function1<String, DictionaryReader> reader) {
            this.client = client;
            this.dictionaryPath = dictionaryPath;
            this.reader = reader;
        }

        /**
         * We're assuming that downloadable lists are very small, therefore we're able to download them completely at once
         */
        @Override
        public Set<ToxicWord> acquire() {
            final var rawResult = client.get()
                .uri(dictionaryPath)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            return reader.apply(rawResult).read();
        }
    }
}
