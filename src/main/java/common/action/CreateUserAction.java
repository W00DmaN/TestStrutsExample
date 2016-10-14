package common.action;

import common.db.dao.UserDao;
import common.db.dao.exceptions.DublicateUserException;
import common.form.UserForm;
import common.utils.ErrorForward;
import common.utils.ErrorMessageKey;
import common.utils.StatusAction;
import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

public class CreateUserAction extends SmartAction {
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws SQLException, DublicateUserException {
        UserForm userForm = (UserForm) form;
        ErrorForward errorForward;

        if (userForm.getPassword() == null || userForm.getPassword().length() < 1) {
            errorForward = new ErrorForward(StatusAction.ERROR, "password",ErrorMessageKey.CreateUser.CAN_NOT_BLANK);
            return actionErrorForward(request, mapping, errorForward, "Password");
        }

        UserDao userDao = getUserDao(request);
        try {
            userDao.addUser(userForm.extractUser(), userForm.getPassword());
        } catch (DublicateUserException e) {
            errorForward = new ErrorForward(StatusAction.CreateUser.DUBLICATE_USER, "dublicateUser", ErrorMessageKey.CreateUser.DUBLICATE_LOGIN);
            return actionErrorForward(request, mapping, errorForward);
        }
        ActionMessages messages = new ActionMessages();
        messages.add("newUser", new ActionMessage("create.new.user",userForm.getLogin()));
        saveMessages(request.getSession(),messages);

        return mapping.findForward(StatusAction.SUCCESS);
    }
}
