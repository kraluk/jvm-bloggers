package com.jvm_bloggers.entities.toxic_word;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Access(AccessType.FIELD)
@Getter
@NoArgsConstructor(access = PROTECTED)
@ToString(of = {"languageCode", "value"})
public class ToxicWord {

    @Id
    @GenericGenerator(
        name = "TOXIC_WORD_SEQ",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @Parameter(name = "sequence_name", value = "TOXIC_WORD_SEQ"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
        }
    )
    @GeneratedValue(generator = "TOXIC_WORD_SEQ")
    private Long id;

    @Column(name = "language_code", nullable = false)
    private String languageCode;

    @Column(name = "value", nullable = false)
    private String value;

    public ToxicWord(final SupportedLanguage language, final String value) {
        this.languageCode = language.getCode();
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ToxicWord toxicWord = (ToxicWord) o;
        return Objects.equals(languageCode, toxicWord.languageCode)
            && Objects.equals(value, toxicWord.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(languageCode, value);
    }

    public enum SupportedLanguage {
        POLISH("pl"),
        ENGLISH("en");

        private final String code;

        SupportedLanguage(final String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }
}
