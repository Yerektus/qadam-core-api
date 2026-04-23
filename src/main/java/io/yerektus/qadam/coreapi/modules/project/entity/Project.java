package io.yerektus.qadam.coreapi.modules.project.entity;

import io.yerektus.qadam.coreapi.common.entity.BaseEntity;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("projects")
public class Project extends BaseEntity {

    @Column("user_id")
    private UUID userId;

    private String name;

    @Column("editor_content")
    private String editorContent;

    public Project() {}

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEditorContent() {
        return editorContent;
    }

    public void setEditorContent(String editorContent) {
        this.editorContent = editorContent;
    }
}
