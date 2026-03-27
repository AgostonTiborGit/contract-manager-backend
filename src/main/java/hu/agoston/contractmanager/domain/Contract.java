package hu.agoston.contractmanager.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "contracts")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "reference_number")
    private String referenceNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type", nullable = false)
    private ContractType contractType;

    @Column(name = "fixed_term", nullable = false)
    private boolean fixedTerm;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column(name = "notice_period_days")
    private Integer noticePeriodDays;

    @Column(length = 2000)
    private String notes;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;

    protected Contract() {
        // JPA
    }

    public Contract(String title,
                    String referenceNumber,
                    ContractType contractType,
                    boolean fixedTerm,
                    LocalDate startDate,
                    LocalDate endDate,
                    Integer noticePeriodDays,
                    String notes,
                    BigDecimal amount,
                    Currency currency,
                    Partner partner) {
        this.title = title;
        this.referenceNumber = referenceNumber;
        this.contractType = contractType;
        this.fixedTerm = fixedTerm;
        this.startDate = startDate;
        this.endDate = endDate;
        this.noticePeriodDays = noticePeriodDays;
        this.notes = notes;
        this.amount = amount;
        this.currency = currency;
        this.partner = partner;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public ContractType getContractType() {
        return contractType;
    }

    public boolean isFixedTerm() {
        return fixedTerm;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Integer getNoticePeriodDays() {
        return noticePeriodDays;
    }

    public String getNotes() {
        return notes;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public void setContractType(ContractType contractType) {
        this.contractType = contractType;
    }

    public void setFixedTerm(boolean fixedTerm) {
        this.fixedTerm = fixedTerm;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setNoticePeriodDays(Integer noticePeriodDays) {
        this.noticePeriodDays = noticePeriodDays;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}