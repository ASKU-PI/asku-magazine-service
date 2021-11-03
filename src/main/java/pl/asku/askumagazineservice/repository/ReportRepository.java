package pl.asku.askumagazineservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.PagingAndSortingRepository;
import pl.asku.askumagazineservice.model.report.Report;

public interface ReportRepository extends PagingAndSortingRepository<Report, Long> {
  Page<Report> findAllByClosed(Boolean closed, PageRequest pageRequest);
}