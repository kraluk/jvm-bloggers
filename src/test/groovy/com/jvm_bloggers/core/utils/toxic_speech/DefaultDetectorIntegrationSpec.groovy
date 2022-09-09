package com.jvm_bloggers.core.utils.toxic_speech

import com.jvm_bloggers.SpringContextAwareSpecification
import spock.lang.Subject

@Subject(ToxicSpeechDetector.Holder.DefaultDetector)
class DefaultDetectorIntegrationSpec extends SpringContextAwareSpecification {
}
