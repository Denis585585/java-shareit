package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dao.BookingServiceImpl;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ShareItTests {

    @Autowired
    private BookingServiceImpl bookingService;

    @Test
    void contextLoads() {
        assertNotNull(bookingService);
    }

}
