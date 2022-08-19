package com.jvm_bloggers.core.utils.speech;

import com.jvm_bloggers.core.utils.speech.ToxicSpeechDetector.Holder.DefaultDetector;
import com.jvm_bloggers.core.utils.speech.ToxicSpeechDetector.Holder.NoOpDetector;
import com.jvm_bloggers.core.utils.speech.WordRepository.WordRepositoryFactory;
import io.vavr.collection.List;
import lombok.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(ToxicSpeechDetectorConfiguration.Dictionaries.class)
class ToxicSpeechDetectorConfiguration {

    @ConditionalOnProperty(value = "toxic-speech.dictionaries.enabled", havingValue = "true")
    @Bean
    ToxicSpeechDetector toxicSpeechDetector(final WebClient webClient,
                                            final Dictionaries dictionaries) {
        final var dictionaryAcquirers = dictionaryAcquirers(webClient, dictionaries);

        final var factory = new WordRepositoryFactory(dictionaryAcquirers);
        final var repository = factory.create();

        return new DefaultDetector(repository);
    }

    @ConditionalOnProperty(value = "toxic-speech.dictionaries.enabled", havingValue = "false", matchIfMissing = true)
    @Bean
    ToxicSpeechDetector toxicSpeechDetector() {
        return new NoOpDetector();
    }

    List<DictionaryAcquirer> dictionaryAcquirers(final WebClient webClient,
                                                 final Dictionaries dictionaries) {
        final var polishAcquirer = DictionaryAcquirer.polishAcquirer(webClient, dictionaries.getPolish());
        final var englishAcquirer = DictionaryAcquirer.englishAcquirer(webClient, dictionaries.getEnglish());

        return List.of(polishAcquirer, englishAcquirer);
    }

    @ConstructorBinding
    @ConfigurationProperties(prefix = "toxic-speech.dictionaries")
    @Value
    static class Dictionaries {

        String polish;
        String english;
    }
}
