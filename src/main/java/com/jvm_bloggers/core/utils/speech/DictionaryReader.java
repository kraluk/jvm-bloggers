package com.jvm_bloggers.core.utils.speech;

import io.vavr.collection.Set;
import io.vavr.collection.Stream;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.apache.commons.collections4.CollectionUtils.size;

interface DictionaryReader {

    Set<String> read();

    // --- Implementations

    /**
     * Raw dictionary format:
     *
     * <pre>
     * word1
     * word2
     * word3</pre>
     */
    class EnglishDictionaryReader implements DictionaryReader {

        private final BufferedReader reader;

        EnglishDictionaryReader(final BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public Set<String> read() {
            return Stream.ofAll(reader.lines())
                    .map(String::strip)
                    .filter(StringUtils::isNotBlank)
                    .toSet();
        }

        static DictionaryReader createFor(final String content) {
            return new EnglishDictionaryReader(
                    new BufferedReader(
                            new StringReader(content)));
        }
    }

    /**
     * Raw dictionary format:
     *
     * <pre>
     * lp;word;category
     * 1;word1;N
     * 2;word2;N
     * 3;word3;N</pre>
     */
    class PolishDictionaryReader implements DictionaryReader {

        private static final int WORD_INDEX = 1;

        private final BufferedReader reader;

        PolishDictionaryReader(final BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public Set<String> read() {
            return Stream.ofAll(reader.lines())
                    .drop(1) // skips header
                    .map(String::strip)
                    .filter(StringUtils::isNotBlank)
                    .map(l -> l.split(";"))
                    .filter(s -> size(s) > 3)
                    .map(s -> s[WORD_INDEX])
                    .map(String::strip)
                    .filter(StringUtils::isNotBlank)
                    .toSet();
        }

        static DictionaryReader createFor(final String content) {
            return new PolishDictionaryReader(
                    new BufferedReader(
                            new StringReader(content)));
        }
    }
}
