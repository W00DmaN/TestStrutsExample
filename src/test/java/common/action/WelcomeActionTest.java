package common.action;

import common.db.model.User;
import common.utils.Attributes;
import common.utils.StatusAction;


public class WelcomeActionTest extends UtilActionTest {

    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    String getRequestPathInfo() {
        return "/welcome";
    }

    public void testSuccessWelcomeAction() {
        actionPerform();
        verifyForward(StatusAction.SUCCESS);
    }

    public void testErrorWelcomeAction() {
        getSession().setAttribute(Attributes.SESSION_USER, new User(10, "username", "firstName", "lastName"));
        actionPerform();
        verifyForward(StatusAction.ERROR);
    }

}