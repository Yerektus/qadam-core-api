package io.yerektus.qadam.coreapi.modules.document.entity;

import io.yerektus.qadam.coreapi.common.entity.BaseEntity;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("documents")
public class Document extends BaseEntity {

    @Column("user_id")
    private UUID userId;

    @Column("project_id")
    private UUID projectId;

    @Column("file_name")
    private String fileName;

    @Column("file_path")
    private String filePath;

    private String description;

    private String status;

    public Document() {}

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
