import java.time.Instant;
import java.util.UUID;

public record PageContent(
        UUID uuid,
        String pageUrl,
        String domain,
        String pageTitle,
        String pageContent,
        String lang,
        Instant requestTime
) {
}