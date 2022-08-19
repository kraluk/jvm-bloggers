package com.jvm_bloggers.core.utils.speech;

import com.jvm_bloggers.core.utils.speech.DictionaryReader.EnglishDictionaryReader;
import com.jvm_bloggers.core.utils.speech.DictionaryReader.PolishDictionaryReader;
import io.vavr.Function1;
import io.vavr.collection.Set;
import lombok.ToString;
import org.springframework.web.reactive.function.client.WebClient;

interface DictionaryAcquirer {

    Set<String> acquire();

    static DictionaryAcquirer polishAcquirer(final WebClient client,
                                             final String dictionaryPath) {
        return new DefaultDictionaryAcquirer(client, dictionaryPath, PolishDictionaryReader::createFor);
    }

    static DictionaryAcquirer englishAcquirer(final WebClient client,
                                              final String dictionaryPath) {
        return new DefaultDictionaryAcquirer(client, dictionaryPath, EnglishDictionaryReader::createFor);
    }

    // --- Implementations

    @ToString(of = "dictionaryPath")
    class DefaultDictionaryAcquirer implements DictionaryAcquirer {

        private final WebClient client;
        private final String dictionaryPath;

        private final Function1<String, DictionaryReader> readerCreator;

        DefaultDictionaryAcquirer(final WebClient client,
                                  final String dictionaryPath,
                                  final Function1<String, DictionaryReader> readerCreator) {
            this.client = client;
            this.dictionaryPath = dictionaryPath;
            this.readerCreator = readerCreator;
        }

        @Override
        public Set<String> acquire() {
            final var rawResult = client.get()
                    .uri(dictionaryPath)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return readerCreator.apply(rawResult).read();
        }
    }
}
