package wannabit.io.ringowallet.model;

public class Password {
    String resource;
    String spec;
    boolean usingBio;


    public Password(String resource) {
        this.resource = resource;
    }

    public Password(String resource, String spec, boolean usingBio) {
        this.resource = resource;
        this.spec = spec;
        this.usingBio = usingBio;
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

    public boolean isUsingBio() {
        return usingBio;
    }

    public void setUsingBio(boolean usingBio) {
        this.usingBio = usingBio;
    }

}
