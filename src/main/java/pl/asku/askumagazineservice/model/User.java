package pl.asku.askumagazineservice.model;

import java.time.LocalDate;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class User {

  @Id
  @NotBlank
  @Column(unique = true, updatable = false)
  private String id;

  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @UpdateTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  private Date updatedDate;

  @NotNull
  @NotBlank
  @Size(max = 50)
  private String firstName;

  @NotNull
  @NotBlank
  @Size(max = 50)
  private String lastName;

  @NotNull
  @NotBlank
  @Email
  @Column(unique = true)
  private String email;

  @Pattern(regexp = "[0-9\\-+\\s()]+")
  @Size(min = 3, max = 15)
  private String phoneNumber;

  @NotNull
  @NotBlank
  @Size(max = 100)
  private String address;

  @NotNull
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate birthDate;
}
