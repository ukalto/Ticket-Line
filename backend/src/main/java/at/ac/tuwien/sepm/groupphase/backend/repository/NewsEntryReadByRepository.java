package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.NewsEntryReadBy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsEntryReadByRepository
    extends JpaRepository<NewsEntryReadBy, NewsEntryReadBy.PrimaryKeys> {}
