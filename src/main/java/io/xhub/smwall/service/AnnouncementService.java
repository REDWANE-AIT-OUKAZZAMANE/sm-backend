package io.xhub.smwall.service;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import io.xhub.smwall.commands.AnnouncementAddCommand;
import io.xhub.smwall.commands.AnnouncementUpdateCommand;
import io.xhub.smwall.constants.ApiClientErrorCodes;
import io.xhub.smwall.domains.Announcement;
import io.xhub.smwall.domains.QAnnouncement;
import io.xhub.smwall.events.announcement.AnnouncementEvent;
import io.xhub.smwall.events.announcement.NoCurrentAnnouncementFoundEvent;
import io.xhub.smwall.exceptions.BusinessException;
import io.xhub.smwall.repositories.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.function.BiPredicate;

import static io.xhub.smwall.utlis.AssertUtils.assertIsAfterDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnnouncementService {
    private final ApplicationEventPublisher eventPublisher;

    private final AnnouncementRepository announcementRepository;

    public Page<Announcement> getAllAnnouncement(Predicate basePredicate, Pageable pageable) {
        log.info("Start getting all announcements");
        Predicate deletedFilter = QAnnouncement.announcement.deleted.eq(false);
        Predicate finalPredicate = basePredicate != null ? ExpressionUtils
                .allOf(basePredicate, deletedFilter) : deletedFilter;
        assert finalPredicate != null;
        return announcementRepository.findAllByDeletedFalseOrderByCreatedAtDesc(finalPredicate, pageable);
    }

    public Announcement getCurrentAnnouncement() {
        log.info("Start getting Current announcement ");
        return announcementRepository.findFirstByEndDateAfterAndDeletedFalseOrderByStartDateAsc(Instant.now())
                .orElse(null);
    }


    public Announcement getAnnouncementById(String id) {
        log.info("Start getting announcement by id '{}'", id);
        return announcementRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new BusinessException(ApiClientErrorCodes.ANNOUNCEMENT_NOT_FOUND.getErrorMessage()));
    }

    public void deleteAnnouncementById(String id) {
        log.info("Start deleting announcement with ID: {}", id);
        Announcement announcement = getAnnouncementById(id);
        announcement.delete();
        announcementRepository.save(announcement);
        publishCurrentAnnouncementEvent();
    }

    public Announcement addAnnouncement(final AnnouncementAddCommand announcementAddCommand) {
        log.info("Start creating an announcement");
        Announcement announcement = announcementRepository.save(Announcement.create(thereAnyAnnouncement(), announcementAddCommand));
        log.info("Announcement created: {}", announcement);
        publishCurrentAnnouncementEvent();
        return announcement;
    }

    private BiPredicate<Instant, Instant> thereAnyAnnouncement() {
        return (startDate, endDate) -> {
            log.info("Existence check for announcements");
            return announcementRepository.existsByStartDateLessThanEqualAndEndDateGreaterThanEqualAndDeletedFalse(endDate, startDate);
        };
    }

    public Announcement updateAnnouncement(String id, AnnouncementUpdateCommand announcementUpdateCommand) {
        log.info("Start updating announcement ");
        Announcement announcement = getAnnouncementById(id);

        if (announcementUpdateCommand.getStartDate() != null) {
            assertIsAfterDate(announcementUpdateCommand.getStartDate(), announcementUpdateCommand.getEndDate());
        } else {
            assertIsAfterDate(announcement.getStartDate(), announcementUpdateCommand.getEndDate());
        }

        announcement.update(this::existInBetween, announcementUpdateCommand);
        announcementRepository.save(announcement);
        publishCurrentAnnouncementEvent();

        return announcement;
    }

    private void existInBetween(Instant endDate, Instant startDate, String id) {
        if (announcementRepository.existsByStartDateLessThanEqualAndEndDateGreaterThanEqualAndIdIsNotAndDeletedFalse(endDate, startDate, id)) {
            throw new BusinessException(ApiClientErrorCodes.ANNOUNCEMENT_ALREADY_EXISTS.getErrorMessage());
        }
    }

    private void publishCurrentAnnouncementEvent() {
        Announcement currentAnnouncement = getCurrentAnnouncement();
        if (currentAnnouncement != null) {
            eventPublisher.publishEvent(new AnnouncementEvent(currentAnnouncement));
        } else {
            eventPublisher.publishEvent(new NoCurrentAnnouncementFoundEvent(this));
        }
    }
}
