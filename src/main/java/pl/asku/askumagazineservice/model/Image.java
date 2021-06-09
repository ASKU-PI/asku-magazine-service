package pl.asku.askumagazineservice.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "image")
@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, updatable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "magazine_id", referencedColumnName = "id")
    private Magazine magazine;

    @NotNull
    @Column(name = "picByte", length = 10000)
    private byte[] picByte;

    @NotNull
    private String format;

}
