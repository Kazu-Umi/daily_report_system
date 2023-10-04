package actions;

import java.io.IOException;

import javax.servlet.ServletException;

import constants.AttributeConst;
import constants.ForwardConst;

public class EmployeeReportAction extends ActionBase {

    @Override
    public void process() throws ServletException, IOException {

        invoke();

    }


    public void index() throws ServletException, IOException {

        //セッションにフラッシュメッセージが設定されている場合はリクエストスコープに移し替え、セッションからは削除する
        String flush = getSessionScope(AttributeConst.FLUSH);
        if (flush != null) {
            putRequestScope(AttributeConst.FLUSH, flush);
            removeSessionScope(AttributeConst.FLUSH);
        }

        //一覧画面を表示
        forward(ForwardConst.FW_EMPREP_INDEX);
    }
}
