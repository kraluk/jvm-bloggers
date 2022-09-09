package com.jvm_bloggers.core.utils.toxic_speech

import spock.lang.Specification
import spock.lang.Subject

@Subject(ToxicSpeechDetector.Holder.NoOpDetector)
class NoOpDetectorSpec extends Specification {
}
