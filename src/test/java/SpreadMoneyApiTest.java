import com.kakao.pay.api.enums.ErrorType;
import com.kakao.pay.api.exception.ApiRuntimeException;
import com.kakao.pay.api.model.SpreadMoneyDistribution;
import com.kakao.pay.api.model.SpreadMoneyEvent;
import com.kakao.pay.api.model.SpreadMoneyStatus;
import com.kakao.pay.api.model.User;
import com.kakao.pay.api.service.SpreadMoneyService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SpreadMoneyApiTest extends IntegrationTest {
    private static final int TOKEN_LENGTH = 3;

    @Autowired
    private SpreadMoneyService spreadMoneyService;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private SpreadMoneyEvent spreadMoneyEventOnly1Money;
    private SpreadMoneyEvent spreadMoneyEventNormal;

    private User eventOwner;
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private User user5;
    private User userTooLate;
    private User userFromAnotherRoom;

    @Before
    public void setUp() {
        spreadMoneyEventOnly1Money = new SpreadMoneyEvent();

        spreadMoneyEventOnly1Money.setUserId(1);
        spreadMoneyEventOnly1Money.setRoomId("123");
        spreadMoneyEventOnly1Money.setTargetMemberCount(5);
        spreadMoneyEventOnly1Money.setTotalAmount("1");

        spreadMoneyEventNormal = new SpreadMoneyEvent();

        spreadMoneyEventNormal.setUserId(1);
        spreadMoneyEventNormal.setRoomId("123");
        spreadMoneyEventNormal.setTargetMemberCount(5);
        spreadMoneyEventNormal.setTotalAmount("1234");

        eventOwner = new User(1, "123", "tp*");
        user1 = new User(2, "123", "tp*");
        user2 = new User(3, "123", "tp*");
        user3 = new User(4, "123", "tp*");
        user4 = new User(5, "123", "tp*");
        user5 = new User(6, "123", "ady");
        userTooLate = new User(7, "123", "tp*");
        userFromAnotherRoom = new User(8, "456", "tp*");
    }

    @Test
    public void testDistributeMoney() {
        List<SpreadMoneyDistribution> result = ReflectionTestUtils.invokeMethod(spreadMoneyService, "distributeMoney", spreadMoneyEventOnly1Money);

        assertTrue(CollectionUtils.isNotEmpty(result));
        assertEquals(spreadMoneyEventOnly1Money.getTargetMemberCount(), result.size());
        assertEquals(spreadMoneyEventOnly1Money.getTotalAmount(), result.stream().map(SpreadMoneyDistribution::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    @Test
    public void testCreateEvent() {
        spreadMoneyService.createEvent(spreadMoneyEventNormal);

        assertNotEquals(NumberUtils.LONG_ZERO, spreadMoneyEventNormal.getId());
        assertTrue(StringUtils.isNoneEmpty(spreadMoneyEventNormal.getToken()));
        assertEquals(TOKEN_LENGTH, spreadMoneyEventNormal.getToken().length());
    }

    @Test
    public void testGetSpreadMoneyStatus() {
        SpreadMoneyStatus spreadMoneyStatus = spreadMoneyService.getSpreadMoneyStatus(eventOwner);

        assertNotNull(spreadMoneyStatus);
    }

    @Test
    public void testCheckValidateReceiveNotExist() {
        SpreadMoneyStatus spreadMoneyStatus = null;

        exception.expect(ApiRuntimeException.class);
        exception.expectMessage(ErrorType.SPREAD_MONEY_RECEIVE_NOT_EXIST.getMessage());
        spreadMoneyService.checkValidateReceive(eventOwner, spreadMoneyStatus);
    }

    @Test
    public void testCheckValidateReceiveReceiveAlready() {
        SpreadMoneyStatus spreadMoneyStatus = spreadMoneyService.getSpreadMoneyStatus(user1);

        spreadMoneyStatus.getSpreadMoneyReceivedList().get(spreadMoneyStatus.getSpreadMoneyReceivedList().size() - 1).setUserId(user1.getId());

        exception.expect(ApiRuntimeException.class);
        exception.expectMessage(ErrorType.SPREAD_MONEY_RECEIVE_ALREADY.getMessage());
        spreadMoneyService.checkValidateReceive(user1, spreadMoneyStatus);
    }

    @Test
    public void testCheckValidateReceiveInvalidUser() {
        SpreadMoneyStatus spreadMoneyStatus = spreadMoneyService.getSpreadMoneyStatus(eventOwner);

        exception.expect(ApiRuntimeException.class);
        exception.expectMessage(ErrorType.SPREAD_MONEY_RECEIVE_INVALID_USER.getMessage());
        spreadMoneyService.checkValidateReceive(eventOwner, spreadMoneyStatus);
    }

    @Test
    public void testCheckValidateReceiveInvalidRoom() {
        SpreadMoneyStatus spreadMoneyStatus = spreadMoneyService.getSpreadMoneyStatus(userFromAnotherRoom);

        exception.expect(ApiRuntimeException.class);
        exception.expectMessage(ErrorType.SPREAD_MONEY_RECEIVE_INVALID_ROOM.getMessage());
        spreadMoneyService.checkValidateReceive(userFromAnotherRoom, spreadMoneyStatus);
    }

    @Test
    public void testCheckValidateReceiveReceiveFull() {
        SpreadMoneyStatus spreadMoneyStatus = spreadMoneyService.getSpreadMoneyStatus(userTooLate);

        spreadMoneyStatus.getSpreadMoneyReceivedList().forEach(spreadMoneyReceived -> spreadMoneyReceived.setUserId(NumberUtils.LONG_ONE));

        exception.expect(ApiRuntimeException.class);
        exception.expectMessage(ErrorType.SPREAD_MONEY_RECEIVE_FULL.getMessage());
        spreadMoneyService.checkValidateReceive(userTooLate, spreadMoneyStatus);
    }

    @Test
    public void testCheckValidateReceiveInvalidTimeBefore() {
        SpreadMoneyStatus spreadMoneyStatus = spreadMoneyService.getSpreadMoneyStatus(user1);

        spreadMoneyStatus.setRegisteredTime(LocalDateTime.now().plusDays(3));

        exception.expect(ApiRuntimeException.class);
        exception.expectMessage(ErrorType.SPREAD_MONEY_RECEIVE_INVALID_TIME.getMessage());
        spreadMoneyService.checkValidateReceive(user1, spreadMoneyStatus);
    }

    @Test
    public void testCheckValidateReceiveInvalidTimeAfter() {
        SpreadMoneyStatus spreadMoneyStatus = spreadMoneyService.getSpreadMoneyStatus(user1);

        spreadMoneyStatus.setRegisteredTime(LocalDateTime.now().minusMinutes(10));

        exception.expect(ApiRuntimeException.class);
        exception.expectMessage(ErrorType.SPREAD_MONEY_RECEIVE_INVALID_TIME.getMessage());
        spreadMoneyService.checkValidateReceive(user1, spreadMoneyStatus);
    }

    @Test
    public void testReceive() {
        assertNotEquals(NumberUtils.LONG_ZERO, spreadMoneyService.receive(user1, spreadMoneyService.getSpreadMoneyStatus(user1)).getId());
    }

    @Test
    public void testCheckValidateStatusInvalidUser() {
        SpreadMoneyStatus spreadMoneyStatus = spreadMoneyService.getSpreadMoneyStatus(user1);

        exception.expect(ApiRuntimeException.class);
        exception.expectMessage(ErrorType.SPREAD_MONEY_STATUS_INVALID_USER.getMessage());
        spreadMoneyService.checkValidateStatus(user1, spreadMoneyStatus);
    }

    @Test
    public void testCheckValidateStatusInvalidTimeBefore() {
        SpreadMoneyStatus spreadMoneyStatus = spreadMoneyService.getSpreadMoneyStatus(eventOwner);

        spreadMoneyStatus.setRegisteredTime(LocalDateTime.now().plusDays(10));

        exception.expect(ApiRuntimeException.class);
        exception.expectMessage(ErrorType.SPREAD_MONEY_STATUS_INVALID_TIME.getMessage());
        spreadMoneyService.checkValidateStatus(eventOwner, spreadMoneyStatus);
    }

    @Test
    public void testCheckValidateStatusInvalidTimeAfter() {
        SpreadMoneyStatus spreadMoneyStatus = spreadMoneyService.getSpreadMoneyStatus(eventOwner);

        spreadMoneyStatus.setRegisteredTime(LocalDateTime.now().minusDays(7));

        exception.expect(ApiRuntimeException.class);
        exception.expectMessage(ErrorType.SPREAD_MONEY_STATUS_INVALID_TIME.getMessage());
        spreadMoneyService.checkValidateStatus(eventOwner, spreadMoneyStatus);
    }
}
