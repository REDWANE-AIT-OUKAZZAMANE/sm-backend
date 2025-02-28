package io.xhub.smwall.filter.filterMedia;

import io.xhub.smwall.config.ContentFilterProperties;
import io.xhub.smwall.domains.Media;
import io.xhub.smwall.enumeration.MediaType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@AllArgsConstructor
public class FilterMedia {
    private ContentFilterProperties contentFilterProperties;

    public void filterContent(Media media) {
        log.error("start to filter content image /video ");

        try {
            String scriptPath = getScriptPath(media.getType());
            float nsfwScore = executePythonScript(scriptPath, media.getUrl());
            setCleanliness(media, nsfwScore);
        } catch (Exception e) {
            log.error("Failed to filter content: {}", e.getMessage());
        }
    }

    private String getScriptPath(MediaType type) {
        if (type == MediaType.IMAGE) {
            return contentFilterProperties.getUrlScriptImage();
        } else {
            return contentFilterProperties.getUrlScriptVideo();
        }
    }

    private float executePythonScript(String scriptPath, String mediaUrl) throws Exception {
        CommandLine commandLine = new CommandLine("python")
                .addArgument(scriptPath)
                .addArgument(mediaUrl);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(streamHandler);
        executor.execute(commandLine);
        return Float.parseFloat(outputStream.toString(StandardCharsets.UTF_8).trim());
    }

    private void setCleanliness(Media media, float nsfwScore) {
        media.setClean(nsfwScore < 0.1);
        media.setHidden(!media.getClean());
        log.info("Media: {}, Clean: {}", media.getUrl(), media.getClean());
    }

}

