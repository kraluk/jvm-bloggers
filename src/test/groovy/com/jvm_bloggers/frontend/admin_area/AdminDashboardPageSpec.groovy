package com.jvm_bloggers.frontend.admin_area

import com.jvm_bloggers.MockSpringContextAwareSpecification
import com.jvm_bloggers.entities.top_posts_summary.TopPostsSummaryRepository
import com.jvm_bloggers.frontend.PaginationConfiguration
import org.apache.wicket.util.tester.FormTester

import java.time.YearMonth

import static com.jvm_bloggers.frontend.WicketTestUtils.headOf
import static com.jvm_bloggers.frontend.admin_area.AdminDashboardPage.GENERATE_TOP_POSTS_SUMMARY_FORM_ID
import static com.jvm_bloggers.frontend.admin_area.AdminDashboardPage.YEAR_MONTH_SELECTOR_ID

class AdminDashboardPageSpec extends MockSpringContextAwareSpecification {

    AdminDashboardPageBackingBean backingBean = Mock(AdminDashboardPageBackingBean)

    @Override
    protected void setupContext() {
        backingBean.prepareYearMonthChoices() >> [
            YearMonth.now(),
            YearMonth.now().minusMonths(1),
            YearMonth.now().minusMonths(2)
        ]
        addBean(backingBean)
        addBean(Stub(PaginationConfiguration))
        addBean(Stub(TopPostsSummaryRepository))
    }

    def "should call logic when 'generate` button is clicked"() {
        given:
        currentUserIsAdmin()
        tester.startPage(AdminDashboardPage)

        when:
        FormTester formTester = tester.newFormTester(GENERATE_TOP_POSTS_SUMMARY_FORM_ID)
        formTester.select(YEAR_MONTH_SELECTOR_ID, 1)
        formTester.submit()

        then:
        1 * backingBean.generateTopPostsSummary(_ as YearMonth)
    }

    def "should render page head with admin webjar stylesheets"() {
        given:
        currentUserIsAdmin()

        when:
        tester.startPage(AdminDashboardPage)

        then:
        String head = headOf(tester.getLastResponseAsString())

        and:
        head.contains("/webjars/bootstrap/css/bootstrap.min.css")
        head.contains("/webjars/startbootstrap-sb-admin-2/css/sb-admin-2.min.css")
        head.contains("/webjars/font-awesome/css/font-awesome.min.css")
    }

}
