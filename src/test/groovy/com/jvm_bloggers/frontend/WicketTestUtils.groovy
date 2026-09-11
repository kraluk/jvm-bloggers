package com.jvm_bloggers.frontend

import java.util.regex.Pattern

class WicketTestUtils {

    private static String SEPARATOR = ":"

    private static Pattern HEAD_SECTION = ~/(?s)<head>(.*?)<\/head>/

    static String pathVia(Object... componentIds) {
        return componentIds.toList().join(SEPARATOR)
    }

    static String headOf(String renderedPage) {
        def matcher = HEAD_SECTION.matcher(renderedPage)
        assert matcher.find(): "Rendered page contains no <head> section"
        return matcher.group(1)
    }

}
