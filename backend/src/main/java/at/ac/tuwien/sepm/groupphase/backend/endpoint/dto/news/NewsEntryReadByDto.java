package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news;

import java.sql.Timestamp;

public record NewsEntryReadByDto(Long applicationUserId, Long newsId, Timestamp visited) {}
