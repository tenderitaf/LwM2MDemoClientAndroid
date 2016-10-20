package appiot.ericcson.com.demolwm2mclient;

/**
 * Created by JoHe1056 on 2016-10-12.
 */
public class Resource {

    private String name;
    private int id;
    private AccessType accessType;
    private boolean multipleInstances;
    private ResourceType type;
    private String rangeOrEnumeration;
    private String units;
    private String description;
    private String value;

    public Resource(int id, String name, AccessType accessType, boolean multipleInstances, ResourceType resourceType, String rangeOrEnumeration, String units, String description) {
        this.id = id;
        this.name = name;
        this.accessType = accessType;
        this.multipleInstances = multipleInstances;
        this.type = resourceType;
        this.rangeOrEnumeration = rangeOrEnumeration;
        this.units = units;
        this.description = description;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    public boolean isMultipleInstances() {
        return multipleInstances;
    }

    public void setMultipleInstances(boolean multipleInstances) {
        this.multipleInstances = multipleInstances;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public String getRangeOrEnumeration() {
        return rangeOrEnumeration;
    }

    public void setRangeOrEnumeration(String rangeOrEnumeration) {
        this.rangeOrEnumeration = rangeOrEnumeration;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
