package model;

public class customer extends user {
    private int phone;
    private String address;
    private float accumulatedPoint;

    public customer(int userId, String userName, String password, String role, String email,
                    int phone, String address, float accumulatedPoint) {
        super(userId, userName, password, role, email);
        this.phone = phone;
        this.address = address;
        this.accumulatedPoint = accumulatedPoint;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getAccumulatedPoint() {
        return accumulatedPoint;
    }

    public void setAccumulatedPoint(float accumulatedPoint) {
        this.accumulatedPoint = accumulatedPoint;
    }

    @Override
    public String toString() {
        return super.toString() + " | Customer{" +
                "phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", accumulatedPoint=" + accumulatedPoint +
                '}';
    }
}