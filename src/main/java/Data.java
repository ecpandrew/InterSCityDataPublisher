public class Data {

    private String timestamp;
    private String current;
    private String power;
    private String energy;

    @Override
    public String toString() {
        return "Data{" +
                "timestamp='" + timestamp + '\'' +
                ", current='" + current + '\'' +
                ", power='" + power + '\'' +
                ", energy='" + energy + '\'' +
                '}';
    }

    public Data(String timestamp, String current, String power, String energy) {
        this.timestamp = timestamp;
        this.current = current;
        this.power = power;
        this.energy = energy;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getCurrent() {
        return current;
    }

    public String getPower() {
        return power;
    }

    public String getEnergy() {
        return energy;
    }
}
