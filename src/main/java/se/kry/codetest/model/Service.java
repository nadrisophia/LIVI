package se.kry.codetest.model;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class Service {

    private final Integer id;
    private String name;
    private String url;
    private ServiceStatus status;
    private final Instant createdAt;

    public Service(Integer id, String name, String url, ServiceStatus status, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ServiceStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
