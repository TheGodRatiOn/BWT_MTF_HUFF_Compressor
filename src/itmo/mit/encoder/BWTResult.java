package itmo.mit.encoder;

public class BWTResult {
    private final String result;
    private final int position;

    public BWTResult(String result, int position) {
        this.result = result;
        this.position = position;
    }

    public String getResult() {
        return result;
    }

    public int getPosition() {
        return position;
    }
}
