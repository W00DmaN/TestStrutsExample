package common.action;

import common.db.dao.UserDao;
import common.db.dao.exceptions.DublicateUserException;
import common.db.model.User;
import common.form.UserForm;
import common.utils.StatusAction;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

public class CreateUserAction extends SmartAction {
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws SQLException, DublicateUserException {
        UserForm userForm = (UserForm) form;
        ActionErrors errors = new ActionErrors();

        if (!isManager(request)) {
            errors.add("access denied", new ActionMessage("error.access.denied"));
            saveErrors(request, errors);
            return mapping.getInputForward();// TODO: А как пользователь узнает что случилось?
        }
        // TODO: А почему не используется ActionForm.validate()?
        if (userForm.getPassword() == null || userForm.getPassword().length() < 1) {
            errors.add("pass", new ActionMessage("errors.required", "Password"));
            saveErrors(request, errors);
            return mapping.getInputForward();
        }

        UserDao userDao = getUserDao(request);

        User user = userDao.getUserByUsername(userForm.getLogin());
        if (user != null) { // TODO: Так проверять плохо
            errors.add("dublicateUser", new ActionMessage("error.dublicate.user.login"));
            saveErrors(request, errors);
            return mapping.getInputForward();
        }

        userDao.addUser(userForm.extractUser(), userForm.getPassword());
        return mapping.findForward(StatusAction.SUCCESS);
    }
}
