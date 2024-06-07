package be.kuleuven.supplierservice.domain;

public class Supplier {
    private String id;
    private String name;
    private String logoUrl;
    private String ip;

    /**
     * Constructor initializing id and name.
     */
    public Supplier(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Full constructor initializing all attributes.
     */
<<<<<<< HEAD
    public Supplier(int id, String name, String logoUrl,String ip) {
=======
    public Supplier(String id, String name, String logoUrl) {
>>>>>>> 0ea28a8047ca7f042dc34340284d41f789983679
        this.id = id;
        this.name = name;
        this.logoUrl = logoUrl;
        this.ip = ip;

    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}