package pl.asku.askumagazineservice.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import pl.asku.askumagazineservice.model.reservation.Reservation;

@Entity
@Table(name = "review")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(unique = true, updatable = false)
  private Long id;

  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @UpdateTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  private Date updatedDate;

  @NotNull
  @OneToOne
  @JoinColumn(name = "reservation_id", referencedColumnName = "id")
  private Reservation reservation;

  @NotNull
  @Min(1)
  @Max(5)
  private Integer rating;

  @Column(columnDefinition = "TEXT")
  @Size(max = 500)
  private String body;
}
