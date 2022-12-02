package com.fjh.commity.entity;


public class Comment {

  private Integer id;
  private Integer userId;
  private Integer entityType;
  private Integer entityId;
  private Integer targetId;
  private String content;
  private Integer status;
  private java.util.Date createTime;


  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }


  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }


  public Integer getEntityType() {
    return entityType;
  }

  public void setEntityType(Integer entityType) {
    this.entityType = entityType;
  }


  public Integer getEntityId() {
    return entityId;
  }

  public void setEntityId(Integer entityId) {
    this.entityId = entityId;
  }


  public Integer getTargetId() {
    return targetId;
  }

  public void setTargetId(Integer targetId) {
    this.targetId = targetId;
  }


  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }


  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }


  public java.util.Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(java.util.Date createTime) {
    this.createTime = createTime;
  }

  @Override
  public String toString() {
    return "CommentMapper{" +
            "id=" + id +
            ", userId=" + userId +
            ", entityType=" + entityType +
            ", entityId=" + entityId +
            ", targetId=" + targetId +
            ", content='" + content + '\'' +
            ", status=" + status +
            ", createTime=" + createTime +
            '}';
  }
}