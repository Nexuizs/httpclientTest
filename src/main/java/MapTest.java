import java.util.HashMap;
import java.util.Map;

public class MapTest {
    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("1","张三");
        map.put("2","李四");
        map.put("3","王五");
        map.put("4","赵六");
        map.put("5","田七");
        map.put("6","王八");
        map.put("7","刘九");
        map.put("8","石狮");

        for (String s : map.keySet()) {
            System.out.println(s);
        }

        for (String value : map.values()) {
            System.out.println(value);
        }

        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " | " + entry.getValue());
        }
    }
}
