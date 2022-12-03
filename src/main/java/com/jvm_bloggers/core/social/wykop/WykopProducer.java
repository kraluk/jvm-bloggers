package com.jvm_bloggers.core.social.wykop;

import com.jvm_bloggers.core.blogpost_redirect.LinkGenerator;
import com.jvm_bloggers.core.newsletter_issues.NewIssuePublished;
import com.jvm_bloggers.entities.newsletter_issue.NewsletterIssue;
import com.jvm_bloggers.entities.wykop.Wykop;
import com.jvm_bloggers.entities.wykop.WykopRepository;
import com.jvm_bloggers.utils.NowProvider;

import org.springframework.context.event.EventListener;

class WykopProducer {

    private final WykopContentGenerator contentGenerator;
    private final WykopRepository repository;
    private final LinkGenerator linkGenerator;
    private final NowProvider nowProvider;

    WykopProducer(final WykopContentGenerator contentGenerator,
                  final WykopRepository repository,
                  final LinkGenerator linkGenerator,
                  final NowProvider nowProvider) {
        this.contentGenerator = contentGenerator;
        this.repository = repository;
        this.linkGenerator = linkGenerator;
        this.nowProvider = nowProvider;
    }

    @EventListener
    void handleNewIssueEvent(final NewIssuePublished event) {
        final NewsletterIssue issue = event.getNewsletterIssue();
        final String content = contentGenerator.generate(issue);
        repository.save(new Wykop(content, nowProvider.now()));
    }
}
