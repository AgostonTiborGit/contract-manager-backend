package hu.agoston.contractmanager.domain;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "partners")
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String taxNumber;
    private String address;

    @OneToMany(mappedBy = "partner", fetch = FetchType.LAZY)
    private List<Contract> contracts;

    protected Partner() {
    }

    public Partner(String name, String email, String taxNumber, String address) {
        this.name = name;
        this.email = email;
        this.taxNumber = taxNumber;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public String getAddress() {
        return address;
    }

    // FONTOS: nincs getContracts()
}
