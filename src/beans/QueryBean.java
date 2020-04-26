package beans;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class QueryBean implements Serializable {

    private static final String[] AVAILABLE_X = new String[] {"-2", "-1.5", "-1", "-0.5", "0", "0.5", "1", "1.5", "2"};
    private static final String[] AVAILABLE_R = new String[] {"1", "1.5", "2", "2.5", "3"};
    private static final double AVAILABLE_Y_MIN = -3;
    private static final double AVAILABLE_Y_MAX = 5;

    private HistoryBean historyBean;

    private String r, y, FormX;
    private final Map<String, Boolean> x = new HashMap<>();

    private String errorMessage;

    public QueryBean() {}

    public Objects formAction(){

    }
    public Object rAction(String r){
        this.r = r;
        return null;
    }

    public Object mainAction() {
        Boolean result = getResult();

        if (r == null || r.isEmpty()) {
            errorMessage = "потому что ты не ввёл R";
            return null;
        }

        if (y == null || y.isEmpty()) {
            errorMessage = "потому что ты не ввёл Y";
            return null;
        }

        try {
            double y = Double.parseDouble(this.y);

            if (y <= AVAILABLE_Y_MIN || y >= AVAILABLE_Y_MAX) {
                errorMessage = "потому что Y не входит в (" + AVAILABLE_Y_MIN + ", " + AVAILABLE_Y_MAX + ")";
                return null;
            }
        } catch (NumberFormatException e) {
            errorMessage = "потому что Y не число";
            return null;
        }
        if (!x.containsValue(true)) {
            errorMessage = "потому что ты не выбрал X";
            return null;
        }

        if (x.values().parallelStream().filter(v -> v).count() > 1) {
            errorMessage = "потому что ты выбрал слишком много X";
            return null;
        }
        if (result != null) {
            if (!result){
                errorMessage = "потому что меня уронили:'(";
            }
            try {
                historyBean.addQuery(new HistoryBean.Query(x.entrySet().parallelStream().filter(Map.Entry::getValue)
                        .map(Map.Entry::getKey).findAny().orElse(null), y, r, result));
            }
            catch (SQLException ignored){}

        }

        return null;
    }

    private Boolean getResult() {
        Double x = this.x.entrySet().parallelStream()
                .filter(Map.Entry::getValue).map(Map.Entry::getKey)
                .map(Double::parseDouble).findAny().orElse(null);
        try {
            Double y = this.y == null ? null : Double.parseDouble(this.y);
            Double r = this.r == null ? null : Double.parseDouble(this.r);

            if (x != null && y != null && r != null) {
                return x >= 0 && y >= 0 && x <= r && y <= r || //переделать
                        x <= 0 && y >= 0 && x * x + y * y <= r * r ||
                        x >= 0 && y <= 0 && y >= 2 * x - r;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setHistoryBean(HistoryBean historyBean) {
        this.historyBean = historyBean;
    }

    public String getR() {
        return r;
    }

    public void setR(String r) {
        this.r = r;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public Map<String, Boolean> getX() {
        return x;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean getHasErrorMessage() { return errorMessage != null; }

    public String[] getAvailableX() {
        return AVAILABLE_X;
    }

    public String[] getAvailableR() {
        return AVAILABLE_R;
    }
}
