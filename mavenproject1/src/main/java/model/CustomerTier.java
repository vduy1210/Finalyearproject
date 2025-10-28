package model;

public class CustomerTier {
    private int id;
    private String tierName;
    private float minPoints;
    private float maxPoints;
    private float discountPercent;
    private String description;

    public CustomerTier() {}

    public CustomerTier(int id, String tierName, float minPoints, float maxPoints, float discountPercent, String description) {
        this.id = id;
        this.tierName = tierName;
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
        this.discountPercent = discountPercent;
        this.description = description;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTierName() {
        return tierName;
    }

    public void setTierName(String tierName) {
        this.tierName = tierName;
    }

    public float getMinPoints() {
        return minPoints;
    }

    public void setMinPoints(float minPoints) {
        this.minPoints = minPoints;
    }

    public float getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(float maxPoints) {
        this.maxPoints = maxPoints;
    }

    public float getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(float discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return tierName + " (" + minPoints + "-" + maxPoints + " pts, " + discountPercent + "% off)";
    }
}
