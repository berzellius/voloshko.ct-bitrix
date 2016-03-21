package com.voloshko.ctbitrix.dto.api.bitrix.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by berz on 20.03.2016.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BitrixCRMLiveFeedMessage extends BitrixCRMEntity {

    public BitrixCRMLiveFeedMessage() {
    }

    public BitrixCRMLiveFeedMessage(String post_title, String message, EntityType entityType, Long entityid) {
        this.post_title = post_title;
        this.message = message;
        this.entitytypeid = entityType.getType();
        this.entityid = entityid;
    }

    public enum EntityType{
        LEAD(1),
        DEAL(2),
        CONTACT(3),
        COMPANY(4);

        private Integer type;

        EntityType(Integer t){
            this.type = t;
        }

        public Integer getType() {
            return type;
        }
    }

    @JsonProperty("POST_TITLE")
    private String post_title;
    @JsonProperty("MESSAGE")
    private String message;
    @JsonProperty("ENTITYTYPEID")
    private Integer entitytypeid;
    @JsonProperty("ENTITYID")
    private Long entityid;

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getEntitytypeid() {
        return entitytypeid;
    }

    public void setEntitytypeid(Integer entitytypeid) {
        this.entitytypeid = entitytypeid;
    }

    public void setEntitytypeid(EntityType et){
        this.setEntitytypeid(et.getType());
    }

    public Long getEntityid() {
        return entityid;
    }

    public void setEntityid(Long entityid) {
        this.entityid = entityid;
    }
}
