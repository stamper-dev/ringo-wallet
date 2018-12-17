package wannabit.io.ringowallet.model;

import java.util.UUID;

public class Mnemonic {

    private Long id;
    private String uuid;
    private String resource;
    private String spec;

    public Mnemonic() {
        this.uuid = UUID.randomUUID().toString();
    }

    public Mnemonic(Long id, String uuid, String resource, String spec) {
        this.id = id;
        this.uuid = uuid;
        this.resource = resource;
        this.spec = spec;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }
}
