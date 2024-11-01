package com.backend;

import com.backend.domain.entity.Event;
import com.backend.mapper.EventMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@SpringBootTest
class BackendApplicationTests {

    @Resource
    private EventMapper eventMapper;

    @Test
    void contextLoads() {
        String pic1 = "http://localhost:8080/file/download/f27dff9661344314bbcbb53a7860aca2.png";
        String pic2 = "http://localhost:8080/file/download/d2778a474daa44f8a0f80fc9c38bb777.png";
        String pic3 = "http://localhost:8080/file/download/efdadcfadc674cd6b41ff01b46343444.png";
        String pic4 = "http://localhost:8080/file/download/c76cb0a6f4754499a31efb674dcf067e.png";

        String[] pics = {pic1, pic2, pic3, pic4};
        Random random = new Random();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (int i = 1; i < 99; i++) {
            String title = "测试" + i;
            String preview = pics[random.nextInt(pics.length)];
            String location = String.valueOf(i);
            Date startTime = new Date(); // 示例开始时间
            Date endTime = new Date(startTime.getTime() + (1000 * 60 * 60 * 24)); // 示例结束时间，开始时间后一天

            // 创建事件对象
            Event event = new Event();
            event.setId(i);
            event.setUpdateBy(1);


            // 调用 mapper 插入事件
            eventMapper.updateById(event);
        }
    }

}
