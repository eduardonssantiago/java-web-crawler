import java.time.Instant;
import java.util.UUID;

public record PageContent(
        UUID uuid,
        String websiteUrl,
        String domainUrl,
        String websiteTitle,
        String websiteContent,
        String lang,
        Instant requestTime
) {
}