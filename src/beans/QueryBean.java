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

    private String r, y, formX, formY, formR;
    private final Map<String, Boolean> x = new HashMap<>();

    private String errorMessage;

    public QueryBean() {}

    public Objects formAction(){
        Boolean result = getResultforCanvas();

        if (formR == null || formR.isEmpty()) {
            errorMessage = "Вы не ввели R";
            return null;
        }

        if (result != null) {
            if (!result){
                errorMessage = "Вырос красный цветочек";
            }
            try {
                historyBean.addQuery(new HistoryBean.Query(formX, formY, formR, result));
            }
            catch (SQLException ignored){}

        }

        return null;
    }
    private Boolean getResultforCanvas() {
        try {
            Double x = this.formX == null ? null : Double.parseDouble(this.formX);
            Double y = this.formY == null ? null : Double.parseDouble(this.formY);
            Double r = this.formR == null ? null : Double.parseDouble(this.formR);

            if (x != null && y != null && r != null) {
                return x >= 0 && y >= 0 && x <= r && y <= r ||
                        x <= 0 && y <= 0 && - x - 2*y <= r ||
                        x >= 0 && y <= 0 && x*x + y*y <= r/2;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object rAction(String r){
        this.r = r;
        return null;
    }

    public Object mainAction() {
        Boolean result = getResult();

        if (r == null || r.isEmpty()) {
            errorMessage = "Вы не ввели R";
            return null;
        }

        if (y == null || y.isEmpty()) {
            errorMessage = "Вы не ввели Y";
            return null;
        }

        try {
            double y = Double.parseDouble(this.y);

            if (y <= AVAILABLE_Y_MIN || y >= AVAILABLE_Y_MAX) {
                errorMessage = "Y не входит в (" + AVAILABLE_Y_MIN + ", " + AVAILABLE_Y_MAX + ")";
                return null;
            }
        } catch (NumberFormatException e) {
            errorMessage = "Y не число";
            return null;
        }
        if (!x.containsValue(true)) {
            errorMessage = "Вы не выбрали X";
            return null;
        }

        if (x.values().parallelStream().filter(v -> v).count() > 1) {
            errorMessage = "Выбрали слишком много X";
            return null;
        }
        if (result != null) {
            if (!result){
                errorMessage = "Вырос красненький цветочек";
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
                return x >= 0 && y >= 0 && x <= r && y <= r ||
                        x <= 0 && y <= 0 && - x - 2*y <= r ||
                        x >= 0 && y <= 0 && x*x + y*y <= r/2;
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

    public String getFormX() {
        return formX;
    }

    public void setFormX(String formX) {
        this.formX = formX;
    }

    public String getFormY() {
        return formY;
    }

    public void setFormY(String formY) {
        this.formY = formY;
    }
    public String getFormR() {
        return formR;
    }

    public void setFormR(String formR) {
        this.formR = formR;
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
