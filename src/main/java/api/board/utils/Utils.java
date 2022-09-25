package api.board.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {

    public static String getHoursAgo(LocalDateTime createdDate) {
        Duration between = Duration.between(createdDate, LocalDateTime.now());
        if(( between.getSeconds()/ 60 )<60) {
            return "최근에 작성됨";
        } else if( ( between.getSeconds()/ 60 / 60) < 24) {
            return ( between.getSeconds()/ 60 / 60) + "시간 전";
        } else if( ( between.getSeconds()/ 60 / 60 / 24) < 7 ) {
            return ( between.getSeconds()/ 60 / 60 / 24) + "일 전";
        } else {
            return createdDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
    }
}
