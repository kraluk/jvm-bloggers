package com.jvm_bloggers.core.utils.toxic_speech;

import com.jvm_bloggers.core.utils.toxic_speech.ToxicSpeechDetector.DefaultDetector;
import com.jvm_bloggers.core.utils.toxic_speech.ToxicSpeechDetector.NoOpDetector;
import com.jvm_bloggers.entities.toxic_word.ToxicWordRepository;

import io.vavr.collection.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import static com.jvm_bloggers.core.utils.toxic_speech.DictionaryAcquirer.englishAcquirer;
import static com.jvm_bloggers.core.utils.toxic_speech.DictionaryAcquirer.polishAcquirer;

@Configuration
@EnableConfigurationProperties(ToxicSpeechDetectorConfiguration.Dictionaries.class)
class ToxicSpeechDetectorConfiguration {

    @ConditionalOnProperty(value = "toxic-speech.detector.enabled", havingValue = "true")
    @Bean
    ToxicSpeechDetector toxicSpeechDetector(final ToxicWordRepository repository) {
        return new DefaultDetector(repository);
    }

    @ConditionalOnProperty(value = "toxic-speech.updater.enabled", havingValue = "true")
    @Bean
    ToxicWordUpdater toxicWordUpdater(final ToxicWordRepository repository,
                                      final WebClient webClient,
                                      final CacheManager cacheManager,
                                      final Dictionaries dictionaries) {
        final var acquirers = dictionaryAcquirers(webClient, dictionaries);

        return new ToxicWordUpdater(repository, acquirers, cacheManager);
    }

    @ConditionalOnMissingBean(ToxicSpeechDetector.class)
    @Bean
    ToxicSpeechDetector toxicSpeechDetector() {
        return new NoOpDetector();
    }

    List<DictionaryAcquirer> dictionaryAcquirers(final WebClient webClient,
                                                 final Dictionaries dictionaries) {
        final var polishAcquirer = polishAcquirer(webClient, dictionaries.polish());
        final var englishAcquirer = englishAcquirer(webClient, dictionaries.english());

        return List.of(polishAcquirer, englishAcquirer);
    }

    @ConstructorBinding
    @ConfigurationProperties(prefix = "toxic-speech.dictionaries")
    record Dictionaries(String polish,
                        String english) {
    }
}
