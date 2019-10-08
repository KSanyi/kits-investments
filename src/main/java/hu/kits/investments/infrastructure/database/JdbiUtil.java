package hu.kits.investments.infrastructure.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.statement.Update;

public class JdbiUtil {

    public static Update createInsert(Handle handle, String tableName, Map<String, ?> values) {
        
        List<String> keys = new ArrayList<>(values.keySet());
        
        String sql = "INSERT INTO " + tableName + "(" + String.join(", ", keys) + ") VALUES (" + createQuestionMarks(keys.size()) + ")";
        
        Update update = handle.createUpdate(sql);
        for(String key : keys) {
            update = update.bind(keys.indexOf(key), values.get(key));
        }
        
        return update;
    }
    
    private static String createQuestionMarks(int n) {
        return IntStream.rangeClosed(1, n).mapToObj(i -> "?").collect(Collectors.joining(", "));
    }
    
}
