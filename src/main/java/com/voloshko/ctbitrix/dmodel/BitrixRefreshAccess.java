package com.voloshko.ctbitrix.dmodel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by berz on 12.03.2016.
 */
@Entity(name = "BitrixRefreshAccess")
@Table(
        name = "bitrix_refresh_access"
)
@Access(AccessType.FIELD)
public class BitrixRefreshAccess extends DModelEntityFiscalable {
    public BitrixRefreshAccess() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "bitrix_refresh_access_id_generator")
    @SequenceGenerator(name = "bitrix_refresh_access_id_generator", sequenceName = "bitrix_refresh_access_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    private Long id;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token")
    private String refreshToken;

    private String scope;

    @Column(name = "member_id")
    private String memberId;

    private String domain;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BitrixRefreshAccess && this.getId().equals(((BitrixRefreshAccess) obj).getId());
    }

    @Override
    public String toString() {
        return "BitixRefreshAccess#".concat(this.getId().toString());
    }
}
