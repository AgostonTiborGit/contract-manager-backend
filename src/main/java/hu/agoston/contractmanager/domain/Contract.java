package hu.agoston.contractmanager.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "contracts")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;

    protected Contract() {
        // JPA
    }

    public Contract(String title,
                    LocalDate startDate,
                    LocalDate endDate,
                    Partner partner) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.partner = partner;
    }

    /* ---------- GETTERS ---------- */

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Partner getPartner() {
        return partner;
    }

    /* ---------- SETTERS (UPDATE SUPPORT) ---------- */

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
