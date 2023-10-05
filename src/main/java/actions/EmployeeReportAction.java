package actions;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import actions.views.ReportView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import services.EmployeeService;
import services.ReportService;

public class EmployeeReportAction extends ActionBase {

    private EmployeeService employeeService;
    private ReportService reportService;

    @Override
    public void process() throws ServletException, IOException {

        employeeService = new EmployeeService();
        reportService = new ReportService();

        invoke();

        employeeService.close();
        reportService.close();

    }

    public void index() throws ServletException, IOException {

        //idを条件に従業員データを取得する
        EmployeeView ev = employeeService.findOne(toNumber(getRequestParam(AttributeConst.EMP_ID)));

        //従業員が作成した日報データを、指定されたページ数の一覧画面に表示する分取得する
        int page = getPage();
        List<ReportView> reports = reportService.getMinePerPage(ev, page);

        //従業員が作成した日報データの件数を取得
        long employeeReportsCount = reportService.countAllMine(ev);

        putRequestScope(AttributeConst.EMPLOYEE, ev); //取得した従業員情報
        putRequestScope(AttributeConst.REPORTS, reports); //取得した日報データ
        putRequestScope(AttributeConst.REP_COUNT, employeeReportsCount); //従業員が作成した日報の数
        putRequestScope(AttributeConst.PAGE, page); //ページ数
        putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE); //1ページに表示するレコードの数


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
