package com.jvm_bloggers.frontend.common_components.toastr

import com.jvm_bloggers.MockSpringContextAwareSpecification
import com.jvm_bloggers.frontend.public_area.common_layout.RightFrontendSidebarBackingBean
import com.jvm_bloggers.frontend.public_area.varia_suggestion.VariaSuggestionPage
import com.jvm_bloggers.frontend.public_area.varia_suggestion.VariaSuggestionPageBackingBean

import static com.jvm_bloggers.frontend.WicketTestUtils.headOf

class ToastrBehaviorSpec extends MockSpringContextAwareSpecification {

    @Override
    protected void setupContext() {
        addBean(Stub(RightFrontendSidebarBackingBean))
        addBean(Stub(VariaSuggestionPageBackingBean))
    }

    def "Should add toast to page"() {
        when:
        tester.startPage(VariaSuggestionPage)
        String head = headOf(tester.getLastResponseAsString())

        then:
        head.contains('toastr.min.css')

        and:
        tester.getLastResponseAsString().contains('toastr.min.js')
    }
}
