package hu.agoston.contractmanager.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = "partners",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "tax_number")
        }
)
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "tax_number", nullable = false, length = 16)
    private String taxNumber;

    @Column
    private String address;

    @Column
    private String email;

    @Column
    private String phone;
}
